package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.ProjectSqlHelper;
import com.senprojectbackend1.repository.TeamSqlHelper;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.ProjectRowMapper;
import com.senprojectbackend1.repository.rowmapper.TeamRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the Project entity.
 */
@SuppressWarnings("unused")
class ProjectRepositoryInternalImpl extends SimpleR2dbcRepository<Project, Long> implements ProjectRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TeamRowMapper teamMapper;
    private final ProjectRowMapper projectMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("project", EntityManager.ENTITY_ALIAS);
    private static final Table teamTable = Table.aliased("team", "team");

    private static final EntityManager.LinkTable favoritedbyLink = new EntityManager.LinkTable(
        "rel_project__favoritedby",
        "project_id",
        "favoritedby_id"
    );
    private static final EntityManager.LinkTable tagsLink = new EntityManager.LinkTable("rel_project__tags", "project_id", "tags_id");

    public ProjectRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        ProjectRowMapper projectMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        TeamRowMapper teamMapper,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation<Project, Long>(
                (RelationalPersistentEntity<Project>) converter.getMappingContext().getRequiredPersistentEntity(Project.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.projectMapper = projectMapper;
        this.teamMapper = teamMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Project> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Project> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = ProjectSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TeamSqlHelper.getColumns(teamTable, "team"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(teamTable)
            .on(Column.create("team_id", entityTable))
            .equals(Column.create("id", teamTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Project.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Project> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Project> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Project> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Project> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Project> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Project process(Row row, RowMetadata metadata) {
        Project entity = projectMapper.apply(row, "e");
        entity.setTeam(teamMapper.apply(row, "team"));
        return entity;
    }

    @Override
    public <S extends Project> Mono<S> save(S entity) {
        return super.save(entity).flatMap((S e) -> updateRelations(e));
    }

    protected <S extends Project> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(favoritedbyLink, entity.getId(), entity.getFavoritedbies().stream().map(UserProfile::getId))
            .then();
        result = result.and(entityManager.updateLinkTable(tagsLink, entity.getId(), entity.getTags().stream().map(Tag::getId)));
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(favoritedbyLink, entityId).and(entityManager.deleteFromLinkTable(tagsLink, entityId));
    }

    @Override
    public Flux<Project> findByCriteria(ProjectCriteria projectCriteria, Pageable page) {
        return createQuery(page, buildConditions(projectCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(ProjectCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(ProjectCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getTitle() != null) {
                if (criteria.getTitle().getContains() != null) {
                    allConditions.add(
                        Conditions.like(entityTable.column("title"), Conditions.just("'%" + criteria.getTitle().getContains() + "%'"))
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getTitle(), entityTable.column("title"));
                }
            }
            if (criteria.getDescription() != null) {
                if (criteria.getDescription().getContains() != null) {
                    allConditions.add(
                        Conditions.like(
                            entityTable.column("description"),
                            Conditions.just("'%" + criteria.getDescription().getContains() + "%'")
                        )
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getDescription(), entityTable.column("description"));
                }
            }
            if (criteria.getShowcase() != null) {
                builder.buildFilterConditionForField(criteria.getShowcase(), entityTable.column("showcase"));
            }
            if (criteria.getStatus() != null) {
                builder.buildFilterConditionForField(criteria.getStatus(), entityTable.column("status"));
            }
            if (criteria.getCreatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedAt(), entityTable.column("created_at"));
            }
            if (criteria.getUpdatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getUpdatedAt(), entityTable.column("updated_at"));
            }
            if (criteria.getGithubUrl() != null) {
                if (criteria.getGithubUrl().getContains() != null) {
                    allConditions.add(
                        Conditions.like(
                            entityTable.column("github_url"),
                            Conditions.just("'%" + criteria.getGithubUrl().getContains() + "%'")
                        )
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getGithubUrl(), entityTable.column("github_url"));
                }
            }
            if (criteria.getWebsiteUrl() != null) {
                if (criteria.getWebsiteUrl().getContains() != null) {
                    allConditions.add(
                        Conditions.like(
                            entityTable.column("website_url"),
                            Conditions.just("'%" + criteria.getWebsiteUrl().getContains() + "%'")
                        )
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getWebsiteUrl(), entityTable.column("website_url"));
                }
            }
            if (criteria.getDemoUrl() != null) {
                if (criteria.getDemoUrl().getContains() != null) {
                    allConditions.add(
                        Conditions.like(entityTable.column("demo_url"), Conditions.just("'%" + criteria.getDemoUrl().getContains() + "%'"))
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getDemoUrl(), entityTable.column("demo_url"));
                }
            }
            if (criteria.getOpenToCollaboration() != null) {
                builder.buildFilterConditionForField(criteria.getOpenToCollaboration(), entityTable.column("open_to_collaboration"));
            }
            if (criteria.getOpenToFunding() != null) {
                builder.buildFilterConditionForField(criteria.getOpenToFunding(), entityTable.column("open_to_funding"));
            }
            if (criteria.getType() != null) {
                builder.buildFilterConditionForField(criteria.getType(), entityTable.column("type"));
            }
            if (criteria.getTotalLikes() != null) {
                builder.buildFilterConditionForField(criteria.getTotalLikes(), entityTable.column("total_likes"));
            }
            if (criteria.getTotalShares() != null) {
                builder.buildFilterConditionForField(criteria.getTotalShares(), entityTable.column("total_shares"));
            }
            if (criteria.getTotalViews() != null) {
                builder.buildFilterConditionForField(criteria.getTotalViews(), entityTable.column("total_views"));
            }
            if (criteria.getTotalComments() != null) {
                builder.buildFilterConditionForField(criteria.getTotalComments(), entityTable.column("total_comments"));
            }
            if (criteria.getTotalFavorites() != null) {
                builder.buildFilterConditionForField(criteria.getTotalFavorites(), entityTable.column("total_favorites"));
            }
            if (criteria.getIsDeleted() != null) {
                builder.buildFilterConditionForField(criteria.getIsDeleted(), entityTable.column("is_deleted"));
            }
            if (criteria.getCreatedBy() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedBy(), entityTable.column("created_by"));
            }
            if (criteria.getLastUpdatedBy() != null) {
                builder.buildFilterConditionForField(criteria.getLastUpdatedBy(), entityTable.column("last_updated_by"));
            }
            if (criteria.getTeamId() != null) {
                builder.buildFilterConditionForField(criteria.getTeamId(), teamTable.column("id"));
            }
        }
        Condition builderCondition = builder.buildConditions();
        if (!allConditions.isEmpty()) {
            if (builderCondition != null) {
                allConditions.add(builderCondition);
            }
            return allConditions.stream().reduce(Condition::and).orElse(null);
        }
        return builderCondition;
    }
}
