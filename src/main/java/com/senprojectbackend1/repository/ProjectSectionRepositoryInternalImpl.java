package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.ProjectSection;
import com.senprojectbackend1.domain.criteria.ProjectSectionCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.ProjectRowMapper;
import com.senprojectbackend1.repository.rowmapper.ProjectSectionRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ProjectSection entity.
 */
@SuppressWarnings("unused")
class ProjectSectionRepositoryInternalImpl extends SimpleR2dbcRepository<ProjectSection, Long> implements ProjectSectionRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProjectRowMapper projectMapper;
    private final ProjectSectionRowMapper projectsectionMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("project_section", EntityManager.ENTITY_ALIAS);
    private static final Table projectTable = Table.aliased("project", "project");

    public ProjectSectionRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProjectRowMapper projectMapper,
        ProjectSectionRowMapper projectsectionMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ProjectSection.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.projectMapper = projectMapper;
        this.projectsectionMapper = projectsectionMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<ProjectSection> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProjectSection> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProjectSectionSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProjectSqlHelper.getColumns(projectTable, "project"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(projectTable)
            .on(Column.create("project_id", entityTable))
            .equals(Column.create("id", projectTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ProjectSection.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ProjectSection> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProjectSection> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ProjectSection process(Row row, RowMetadata metadata) {
        ProjectSection entity = projectsectionMapper.apply(row, "e");
        entity.setProject(projectMapper.apply(row, "project"));
        return entity;
    }

    @Override
    public <S extends ProjectSection> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ProjectSection> findByCriteria(ProjectSectionCriteria projectSectionCriteria, Pageable page) {
        return createQuery(page, buildConditions(projectSectionCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ProjectSectionCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ProjectSectionCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getTitle() != null) {
                builder.buildFilterConditionForField(criteria.getTitle(), entityTable.column("title"));
            }
            if (criteria.getContent() != null) {
                builder.buildFilterConditionForField(criteria.getContent(), entityTable.column("content"));
            }
            if (criteria.getMediaUrl() != null) {
                builder.buildFilterConditionForField(criteria.getMediaUrl(), entityTable.column("media_url"));
            }
            if (criteria.getOrder() != null) {
                builder.buildFilterConditionForField(criteria.getOrder(), entityTable.column("jhi_order"));
            }
            if (criteria.getProjectId() != null) {
                builder.buildFilterConditionForField(criteria.getProjectId(), projectTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
