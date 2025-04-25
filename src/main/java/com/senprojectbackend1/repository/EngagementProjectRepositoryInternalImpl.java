package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.criteria.EngagementProjectCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.EngagementProjectRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the EngagementProject entity.
 */
@SuppressWarnings("unused")
class EngagementProjectRepositoryInternalImpl
    extends SimpleR2dbcRepository<EngagementProject, Long>
    implements EngagementProjectRepositoryInternal {

    private final DatabaseClient db;
    private final EntityManager entityManager;

    private final UserProfileRowMapper userprofileMapper;
    private final ProjectRowMapper projectMapper;
    private final EngagementProjectRowMapper engagementprojectMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("engagement_project", EntityManager.ENTITY_ALIAS);
    private static final Table userTable = Table.aliased("user_profile", "e_user");
    private static final Table projectTable = Table.aliased("project", "project");

    public EngagementProjectRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserProfileRowMapper userprofileMapper,
        ProjectRowMapper projectMapper,
        EngagementProjectRowMapper engagementprojectMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(EngagementProject.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.entityManager = entityManager;
        this.userprofileMapper = userprofileMapper;
        this.projectMapper = projectMapper;
        this.engagementprojectMapper = engagementprojectMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<EngagementProject> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<EngagementProject> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EngagementProjectSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
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
        String select = entityManager.createSelect(selectFrom, EngagementProject.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<EngagementProject> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<EngagementProject> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private EngagementProject process(Row row, RowMetadata metadata) {
        EngagementProject entity = engagementprojectMapper.apply(row, "e");
        entity.setUser(userprofileMapper.apply(row, "user"));
        entity.setProject(projectMapper.apply(row, "project"));
        return entity;
    }

    @Override
    public <S extends EngagementProject> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<EngagementProject> findByCriteria(EngagementProjectCriteria engagementProjectCriteria, Pageable page) {
        return createQuery(page, buildConditions(engagementProjectCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(EngagementProjectCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(EngagementProjectCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getType() != null) {
                builder.buildFilterConditionForField(criteria.getType(), entityTable.column("type"));
            }
            if (criteria.getCreatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedAt(), entityTable.column("created_at"));
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
