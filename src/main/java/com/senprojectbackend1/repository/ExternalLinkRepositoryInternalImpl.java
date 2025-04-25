package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.ExternalLink;
import com.senprojectbackend1.domain.criteria.ExternalLinkCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.ExternalLinkRowMapper;
import com.senprojectbackend1.repository.rowmapper.ProjectRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ExternalLink entity.
 */
@SuppressWarnings("unused")
class ExternalLinkRepositoryInternalImpl extends SimpleR2dbcRepository<ExternalLink, Long> implements ExternalLinkRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProjectRowMapper projectMapper;
    private final ExternalLinkRowMapper externallinkMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("external_link", EntityManager.ENTITY_ALIAS);
    private static final Table projectTable = Table.aliased("project", "project");

    public ExternalLinkRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ExternalLinkRowMapper externallinkMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ProjectRowMapper projectMapper,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation<ExternalLink, Long>(
                (RelationalPersistentEntity<ExternalLink>) converter.getMappingContext().getRequiredPersistentEntity(ExternalLink.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.externallinkMapper = externallinkMapper;
        this.projectMapper = projectMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<ExternalLink> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ExternalLink> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ExternalLinkSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProjectSqlHelper.getColumns(projectTable, "project"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(projectTable)
            .on(Column.create("project_id", entityTable))
            .equals(Column.create("id", projectTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ExternalLink.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ExternalLink> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ExternalLink> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ExternalLink process(Row row, RowMetadata metadata) {
        ExternalLink entity = externallinkMapper.apply(row, "e");
        entity.setProject(projectMapper.apply(row, "project"));
        return entity;
    }

    @Override
    public <S extends ExternalLink> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ExternalLink> findByCriteria(ExternalLinkCriteria externalLinkCriteria, Pageable page) {
        return createQuery(page, buildConditions(externalLinkCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ExternalLinkCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ExternalLinkCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getTitle() != null) {
                builder.buildFilterConditionForField(criteria.getTitle(), entityTable.column("title"));
            }
            if (criteria.getUrl() != null) {
                builder.buildFilterConditionForField(criteria.getUrl(), entityTable.column("url"));
            }
            if (criteria.getType() != null) {
                builder.buildFilterConditionForField(criteria.getType(), entityTable.column("type"));
            }
            if (criteria.getProjectId() != null) {
                builder.buildFilterConditionForField(criteria.getProjectId(), projectTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
