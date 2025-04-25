package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.criteria.TagCriteria;
import com.senprojectbackend1.repository.rowmapper.ColumnConverter;
import com.senprojectbackend1.repository.rowmapper.TagRowMapper;
import io.r2dbc.spi.Row;
import io.r2dbc.spi.RowMetadata;
import java.util.ArrayList;
import java.util.List;
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
 * Spring Data R2DBC custom repository implementation for the Tag entity.
 */
@SuppressWarnings("unused")
class TagRepositoryInternalImpl extends SimpleR2dbcRepository<Tag, Long> implements TagRepositoryInternal {

    private final DatabaseClient db;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;
    private final EntityManager entityManager;

    private final TagRowMapper tagMapper;
    private final ColumnConverter columnConverter;

    private static final Table entityTable = Table.aliased("tag", EntityManager.ENTITY_ALIAS);

    public TagRepositoryInternalImpl(
        R2dbcEntityTemplate template,
        EntityManager entityManager,
        TagRowMapper tagMapper,
        R2dbcEntityOperations entityOperations,
        R2dbcConverter converter,
        ColumnConverter columnConverter
    ) {
        super(
            new MappingRelationalEntityInformation(converter.getMappingContext().getRequiredPersistentEntity(Tag.class)),
            entityOperations,
            converter
        );
        this.db = template.getDatabaseClient();
        this.r2dbcEntityTemplate = template;
        this.entityManager = entityManager;
        this.tagMapper = tagMapper;
        this.columnConverter = columnConverter;
    }

    @Override
    public Flux<Tag> findAllBy(Pageable pageable) {
        return createQuery(pageable, null).all();
    }

    RowsFetchSpec<Tag> createQuery(Pageable pageable, Condition whereClause) {
        List<Expression> columns = TagSqlHelper.getColumns(entityTable, EntityManager.ENTITY_ALIAS);
        SelectFromAndJoin selectFrom = Select.builder().select(columns).from(entityTable);
        // we do not support Criteria here for now as of https://github.com/jhipster/generator-jhipster/issues/18269
        String select = entityManager.createSelect(selectFrom, Tag.class, pageable, whereClause);
        return db.sql(select).map(this::process);
    }

    @Override
    public Flux<Tag> findAll() {
        return findAllBy(null);
    }

    @Override
    public Mono<Tag> findById(Long id) {
        Comparison whereClause = Conditions.isEqual(entityTable.column("id"), Conditions.just(id.toString()));
        return createQuery(null, whereClause).one();
    }

    private Tag process(Row row, RowMetadata metadata) {
        Tag entity = tagMapper.apply(row, "e");
        return entity;
    }

    @Override
    public <S extends Tag> Mono<S> save(S entity) {
        return super.save(entity);
    }

    @Override
    public Flux<Tag> findByCriteria(TagCriteria tagCriteria, Pageable page) {
        return createQuery(page, buildConditions(tagCriteria)).all();
    }

    @Override
    public Mono<Long> countByCriteria(TagCriteria criteria) {
        return findByCriteria(criteria, null)
            .collectList()
            .map(collectedList -> collectedList != null ? (long) collectedList.size() : (long) 0);
    }

    private Condition buildConditions(TagCriteria criteria) {
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
            if (criteria.getColor() != null) {
                builder.buildFilterConditionForField(criteria.getColor(), entityTable.column("color"));
            }
            if (criteria.getIsForbidden() != null) {
                builder.buildFilterConditionForField(criteria.getIsForbidden(), entityTable.column("is_forbidden"));
            }
            if (criteria.getCreatedBy() != null) {
                builder.buildFilterConditionForField(criteria.getCreatedBy(), entityTable.column("created_by"));
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
