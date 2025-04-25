package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.ProjectGallery;
import com.senprojectbackend1.domain.criteria.ProjectGalleryCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.ProjectGalleryRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the ProjectGallery entity.
 */
@SuppressWarnings("unused")
class ProjectGalleryRepositoryInternalImpl extends SimpleR2dbcRepository<ProjectGallery, Long> implements ProjectGalleryRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final ProjectRowMapper projectMapper;
    private final ProjectGalleryRowMapper projectgalleryMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("project_gallery", EntityManager.ENTITY_ALIAS);
    private static final Table projectTable = Table.aliased("project", "project");

    public ProjectGalleryRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProjectRowMapper projectMapper,
        ProjectGalleryRowMapper projectgalleryMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(ProjectGallery.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.projectMapper = projectMapper;
        this.projectgalleryMapper = projectgalleryMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<ProjectGallery> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<ProjectGallery> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProjectGallerySqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(ProjectSqlHelper.getColumns(projectTable, "project"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(projectTable)
            .on(Column.create("project_id", entityTable))
            .equals(Column.create("id", projectTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, ProjectGallery.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<ProjectGallery> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<ProjectGallery> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private ProjectGallery process(Row row, RowMetadata metadata) {
        ProjectGallery entity = projectgalleryMapper.apply(row, "e");
        entity.setProject(projectMapper.apply(row, "project"));
        return entity;
    }

    @Override
    public <S extends ProjectGallery> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<ProjectGallery> findByCriteria(ProjectGalleryCriteria projectGalleryCriteria, Pageable page) {
        return createQuery(page, buildConditions(projectGalleryCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ProjectGalleryCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ProjectGalleryCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getImageUrl() != null) {
                builder.buildFilterConditionForField(criteria.getImageUrl(), entityTable.column("image_url"));
            }
            if (criteria.getDescription() != null) {
                builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
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
