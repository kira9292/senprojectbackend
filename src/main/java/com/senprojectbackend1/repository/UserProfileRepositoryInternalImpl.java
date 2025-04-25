package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.criteria.UserProfileCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.UserProfileRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
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
 * Spring Data R2DBC custom repository implementation for the UserProfile entity.
 */
@SuppressWarnings("unused")
class UserProfileRepositoryInternalImpl extends SimpleR2dbcRepository<UserProfile, String> implements UserProfileRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final UserProfileRowMapper userprofileMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("user_profile", EntityManager.ENTITY_ALIAS);

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileRepositoryInternalImpl.class);

    public UserProfileRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        UserProfileRowMapper userprofileMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(UserProfile.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.userprofileMapper = userprofileMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<UserProfile> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<UserProfile> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = UserProfileSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, UserProfile.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<UserProfile> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<UserProfile> findById(String id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(StringUtils.wrap(id.toString(), "'")));
        return createQuery(null, whereClause).one();
    }

    @Override
    public Mono<UserProfile> findOneWithEagerRelationships(String id) {
        return findById(id);
    }

    @Override
    public Flux<UserProfile> findAllWithEagerRelationships() {
        return findAll();
    }

    @Override
    public Flux<UserProfile> findAllWithEagerRelationships(Pageable page) {
        return findAllBy(page);
    }

    private UserProfile process(Row row, RowMetadata metadata) {
        UserProfile entity = userprofileMapper.apply(row, "e");
        return entity;
    }

    @Override
    public Flux<UserProfile> findByCriteria(UserProfileCriteria userProfileCriteria, Pageable page) {
        return createQuery(page, buildConditions(userProfileCriteria)).all();
    }

    @Override
    public Mono<UserProfile> create(UserProfile userProfile) {
        return r2dbcEntityTemplate.insert(UserProfile.class).using(userProfile);
    }

    @Override
    public Mono<Long> countByCriteria(UserProfileCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(UserProfileCriteria criteria) {
        ConditionBuilder builder = new ConditionBuilder(this.columnConverter);
        List<Condition> allConditions = new ArrayList<Condition>();
        if (criteria != null) {
            if (criteria.getId() != null) {
                builder.buildFilterConditionForField(criteria.getId(), entityTable.column("id"));
            }
            if (criteria.getLogin() != null) {
                if (criteria.getLogin().getContains() != null) {
                    allConditions.add(
                        Conditions.like(entityTable.column("login"), Conditions.just("'%" + criteria.getLogin().getContains() + "%'"))
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getLogin(), entityTable.column("login"));
                }
            }
            if (criteria.getFirstName() != null) {
                if (criteria.getFirstName().getContains() != null) {
                    allConditions.add(
                        Conditions.like(
                            entityTable.column("first_name"),
                            Conditions.just("'%" + criteria.getFirstName().getContains() + "%'")
                        )
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getFirstName(), entityTable.column("first_name"));
                }
            }
            if (criteria.getLastName() != null) {
                if (criteria.getLastName().getContains() != null) {
                    allConditions.add(
                        Conditions.like(
                            entityTable.column("last_name"),
                            Conditions.just("'%" + criteria.getLastName().getContains() + "%'")
                        )
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getLastName(), entityTable.column("last_name"));
                }
            }
            if (criteria.getEmail() != null) {
                if (criteria.getEmail().getContains() != null) {
                    allConditions.add(
                        Conditions.like(entityTable.column("email"), Conditions.just("'%" + criteria.getEmail().getContains() + "%'"))
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getEmail(), entityTable.column("email"));
                }
            }
            if (criteria.getImageUrl() != null) {
                builder.buildFilterConditionForField(criteria.getImageUrl(), entityTable.column("image_url"));
            }
            if (criteria.getActivated() != null) {
                builder.buildFilterConditionForField(criteria.getActivated(), entityTable.column("activated"));
            }
            if (criteria.getLangKey() != null) {
                builder.buildFilterConditionForField(criteria.getLangKey(), entityTable.column("lang_key"));
            }
            if (criteria.getCreatedBy() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedBy(), entityTable.column("created_by"));
            }
            if (criteria.getCreatedDate() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedDate(), entityTable.column("created_date"));
            }
            if (criteria.getLastModifiedBy() != null) {
                builder.buildFilterConditionForField(criteria.getLastModifiedBy(), entityTable.column("last_modified_by"));
            }
            if (criteria.getLastModifiedDate() != null) {
                builder.buildFilterConditionForField(criteria.getLastModifiedDate(), entityTable.column("last_modified_date"));
            }
            if (criteria.getProfileLink() != null) {
                builder.buildFilterConditionForField(criteria.getProfileLink(), entityTable.column("profile_link"));
            }
            if (criteria.getBiography() != null) {
                if (criteria.getBiography().getContains() != null) {
                    allConditions.add(
                        Conditions.like(
                            entityTable.column("biography"),
                            Conditions.just("'%" + criteria.getBiography().getContains() + "%'")
                        )
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getBiography(), entityTable.column("biography"));
                }
            }
            if (criteria.getBirthDate() != null) {
                builder.buildFilterConditionForField(criteria.getBirthDate(), entityTable.column("birth_date"));
            }
            if (criteria.getJob() != null) {
                if (criteria.getJob().getContains() != null) {
                    allConditions.add(
                        Conditions.like(entityTable.column("job"), Conditions.just("'%" + criteria.getJob().getContains() + "%'"))
                    );
                } else {
                    builder.buildFilterConditionForField(criteria.getJob(), entityTable.column("job"));
                }
            }
            if (criteria.getSexe() != null) {
                builder.buildFilterConditionForField(criteria.getSexe(), entityTable.column("sexe"));
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

    @Override
    public Mono<Void> update(UserProfile userProfile) {
        LOG.debug("Updating user profile: {}", userProfile.getLogin());
        String sql =
            "UPDATE user_profile SET " +
            "login = $1, " +
            "first_name = $2, " +
            "last_name = $3, " +
            "email = $4, " +
            "image_url = $5, " +
            "activated = $6, " +
            "lang_key = $7, " +
            "created_by = $8, " +
            "created_date = $9, " +
            "last_modified_by = $10, " +
            "last_modified_date = $11, " +
            "profile_link = $12, " +
            "biography = $13, " +
            "birth_date = $14, " +
            "job = $15, " +
            "sexe = $16 " +
            "WHERE id = $17";

        return db
            .sql(sql)
            .bind("$1", userProfile.getLogin())
            .bind("$2", userProfile.getFirstName())
            .bind("$3", userProfile.getLastName())
            .bind("$4", userProfile.getEmail())
            .bind("$5", userProfile.getImageUrl())
            .bind("$6", userProfile.getActivated())
            .bind("$7", userProfile.getLangKey())
            .bind("$8", userProfile.getCreatedBy())
            .bind("$9", userProfile.getCreatedDate())
            .bind("$10", userProfile.getLastModifiedBy())
            .bind("$11", userProfile.getLastModifiedDate())
            .bind("$12", userProfile.getProfileLink())
            .bind("$13", userProfile.getBiography())
            .bind("$14", userProfile.getBirthDate())
            .bind("$15", userProfile.getJob())
            .bind("$16", userProfile.getSexe() != null ? userProfile.getSexe().toString() : null)
            .bind("$17", userProfile.getId())
            .fetch()
            .rowsUpdated()
            .then();
    }
}
