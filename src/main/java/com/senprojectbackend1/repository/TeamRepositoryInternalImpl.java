package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.criteria.TeamCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
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
import org.springframework.data.relational.core.sql.Comparison;
import org.springframework.data.relational.core.sql.Condition;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.Expression;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.SelectBuilder.SelectFromAndJoin;
import org.springframework.data.relational.core.sql.Table;
import org.springframework.data.relational.repository.support.MappingRelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.r2dbc.core.RowsFetchSpec;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.ConditionBuilder;

/**
 * Spring Data R2DBC custom repository implementation for the Team entity.
 */
@SuppressWarnings("unused")
class TeamRepositoryInternalImpl extends SimpleR2dbcRepository<Team, Long> implements TeamRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TeamRowMapper teamMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("team", EntityManager.ENTITY_ALIAS);

    private static final EntityManager.LinkTable membersLink = new EntityManager.LinkTable("rel_team__members", "team_id", "members_id");

    public TeamRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TeamRowMapper teamMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation<Team, Long>(
                (RelationalPersistentEntity<Team>) converter.getMappingContext().getRequiredPersistentEntity(Team.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.teamMapper = teamMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Team> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Team> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TeamSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Team.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Team> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Team> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<Team> findOneWithEagerRelationships(Long id) {
        return findById(id);
    }

    @Override
    public Flux<Team> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<Team> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private Team process(Row row, RowMetadata metadata) {
        return teamMapper.apply(row, "e");
    }

    @Override
    public <S extends Team> Mono<S> save(S entity) {
        return super.save(entity).flatMap(this::updateRelations);
    }

    protected <S extends Team> Mono<S> updateRelations(S entity) {
        Mono<Void> result = entityManager
            .updateLinkTable(membersLink, entity.getId(), entity.getMembers().stream().map(UserProfile::getId))
            .then();
        return result.thenReturn(entity);
    }

    @Override
    public Mono<Void> deleteById(Long entityId) {
        return deleteRelations(entityId).then(super.deleteById(entityId));
    }

    protected Mono<Void> deleteRelations(Long entityId) {
        return entityManager.deleteFromLinkTable(membersLink, entityId);
    }

    @Override
    public Flux<Team> findByCriteria(TeamCriteria teamCriteria, Pageable page) {
        return createQuery(page, buildConditions(teamCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TeamCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TeamCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getName() != null) {
                if (criteria.getName().getContains() != null) {
                    allConditions.add(
                        Conditions.like(entityTable.column("name"), Conditions.just("'%" + criteria.getName().getContains() + "%'"))
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getName(), entityTable.column("name"));
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
            if (criteria.getLogo() != null) {
                builder.buildFilterConditionForField(criteria.getLogo(), entityTable.column("logo"));
            }
            if (criteria.getCreatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedAt(), entityTable.column("created_at"));
            }
            if (criteria.getUpdatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getUpdatedAt(), entityTable.column("updated_at"));
            }
            if (criteria.getVisibility() != null) {
                builder.buildFilterConditionForField(criteria.getVisibility(), entityTable.column("visibility"));
            }
            if (criteria.getTotalLikes() != null) {
                builder.buildFilterConditionForField(criteria.getTotalLikes(), entityTable.column("total_likes"));
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
