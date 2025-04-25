package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.EngagementTeam;
import com.senprojectbackend1.domain.criteria.EngagementTeamCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.EngagementTeamRowMapper;
import com.senprojectbackend1.repository.rowmapper.TeamRowMapper;
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
 * Spring Data R2DBC custom repository implementation for the EngagementTeam entity.
 */
@SuppressWarnings("unused")
class EngagementTeamRepositoryInternalImpl extends SimpleR2dbcRepository<EngagementTeam, Long> implements EngagementTeamRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TeamRowMapper teamMapper;
    private final UserProfileRowMapper userprofileMapper;
    private final EngagementTeamRowMapper engagementteamMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("engagement_team", EntityManager.ENTITY_ALIAS);
    private static final Table teamTable = Table.aliased("team", "team");
    private static final Table userTable = Table.aliased("user_profile", "e_user");

    public EngagementTeamRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TeamRowMapper teamMapper,
        UserProfileRowMapper userprofileMapper,
        EngagementTeamRowMapper engagementteamMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation<EngagementTeam, Long>(
                (RelationalPersistentEntity<EngagementTeam>) converter.getMappingContext().getRequiredPersistentEntity(EngagementTeam.class)
            ),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.teamMapper = teamMapper;
        this.userprofileMapper = userprofileMapper;
        this.engagementteamMapper = engagementteamMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<EngagementTeam> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<EngagementTeam> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = EngagementTeamSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        columns.addAll(TeamSqlHelper.getColumns(teamTable, "team"));
        columns.addAll(UserProfileSqlHelper.getColumns(userTable, "user"));
        SelectFromAndJoinCondition selectFrom = Select.builder()
            .select(columns)
            .from(entityTable)
            .leftOuterJoin(teamTable)
            .on(Column.create("team_id", entityTable))
            .equals(Column.create("id", teamTable))
            .leftOuterJoin(userTable)
            .on(Column.create("user_id", entityTable))
            .equals(Column.create("id", userTable));
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, EngagementTeam.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<EngagementTeam> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<EngagementTeam> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private EngagementTeam process(Row row, RowMetadata metadata) {
        EngagementTeam entity = engagementteamMapper.apply(row, "e");
        entity.setTeam(teamMapper.apply(row, "team"));
        entity.setUser(userprofileMapper.apply(row, "user"));
        return entity;
    }

    @Override
    public <S extends EngagementTeam> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<EngagementTeam> findByCriteria(EngagementTeamCriteria engagementTeamCriteria, Pageable page) {
        return createQuery(page, buildConditions(engagementTeamCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(EngagementTeamCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(EngagementTeamCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getLike() != null) {
                builder.buildFilterConditionForField(criteria.getLike(), entityTable.column("jhi_like"));
            }
            if (criteria.getCreatedAt() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedAt(), entityTable.column("created_at"));
            }
            if (criteria.getTeamId() != null) {
                builder.buildFilterConditionForField(criteria.getTeamId(), teamTable.column("id"));
            }
            if (criteria.getUserId() != null) {
                builder.buildFilterConditionForField(criteria.getUserId(), userTable.column("id"));
            }
        }
        return builder.buildConditions();
    }
}
