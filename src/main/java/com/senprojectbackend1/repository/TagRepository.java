package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.criteria.TagCriteria;
import com.senprojectbackend1.service.dto.TagDTO;
import java.util.Set;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Tag entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TagRepository extends ReactiveCrudRepository<Tag, Long>, TagRepositoryInternal {
    Flux<Tag> findAllBy(Pageable pageable);

    @Override
    <S extends Tag> Mono<S> save(S entity);

    @Override
    Flux<Tag> findAll();

    @Override
    Mono<Tag> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT * FROM tag entity JOIN rel_project__tags joinTable ON entity.id = joinTable.tags_id Join project p on p.id = joinTable.project_id WHERE joinTable.project_id = :projectId"
    )
    Flux<Tag> findByProjectId(Long projectId);
}

interface TagRepositoryInternal {
    <S extends Tag> Mono<S> save(S entity);

    Flux<Tag> findAllBy(Pageable pageable);

    Flux<Tag> findAll();

    Mono<Tag> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Tag> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Tag> findByCriteria(TagCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TagCriteria criteria);
}
