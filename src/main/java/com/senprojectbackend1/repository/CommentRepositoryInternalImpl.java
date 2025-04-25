package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Comment;
import com.senprojectbackend1.domain.criteria.CommentCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.CommentRowMapper;
import com.senprojectbackend1.repository.rowmapper.ProjectRowMapper;
import com.senprojectbackend1.repository.rowmapper.UserProfileRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.mapping.RelationalPersistentEntity;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoinCondition;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Comment entity.
 */
@SuppressWarnings("unused")
class CommentRepositoryInternalImpl extends SimpleR2dbcRepository<Comment, Long> implements CommentRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;

    private final UserProfileRowMapper userprofileMapper;
    private final ProjectRowMapper projectMapper;
    private final CommentRowMapper commentMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("comment", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("user_profile", "e_user");
    private static final Table projectTable = Table.aliased("project", "project");

    public CommentRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserProfileRowMapper userprofileMapper,
        ProjectRowMapper projectMapper,
        CommentRowMapper commentMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation<Comment, Long>(
                (RelationalPersistentEntity<Comment>) converter.getMappingContext().getRequiredPersistentEntity(Comment.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.userprofileMapper = userprofileMapper;
        this.projectMapper = projectMapper;
        this.commentMapper = commentMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Comment> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Comment> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = CommentSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(UserProfileSqlHelper.getColumns(userTable, "user"));
        columns.addAll(ProjectSqlHelper.getColumns(projectTable, "project"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable))
            .leftOuterJoin(projectTable)
            .on(Column.create("project_id", entityTable))
            .equals(Column.create("id", projectTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Comment.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Comment> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Comment> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Comment process(Row row, RowMetadata metadata) {
        Comment entity = commentMapper.apply(row, "e");
        entity.setUser(userprofileMapper.apply(row, "user"));
        entity.setProject(projectMapper.apply(row, "project"));
        return entity;
    }

    @Override
    public <S extends Comment> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Comment> findByCriteria(CommentCriteria commentCriteria, Pageable page) {
        return createQuery(page, buildConditions(commentCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(CommentCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(CommentCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getContent() != null) {
                builder.buildFilterConditionForField(criteria.getContent(), entityTable.column("content"));
            }
            if (criteria.getCreatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedAt(), entityTable.column("created_at"));
            }
            if (criteria.getUpdatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getUpdatedAt(), entityTable.column("updated_at"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getUserId() != null) {
                builder.buildFilterConditionForField(criteria.getUserId(), userTable.column("id"));
            }
            if (criteria.getProjectId() != null) {
                builder.buildFilterConditionForField(criteria.getProjectId(), projectTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
