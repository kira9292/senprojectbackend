package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.ExternalLink;
import com.senprojectbackend1.domain.criteria.ExternalLinkCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ExternalLink entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ExternalLinkRepository extends ReactiveCrudRepository<ExternalLink, Long>, ExternalLinkRepositoryInternal {
    Flux<ExternalLink> findAllBy(Pageable pageable);

    @Query("SELECT * FROM external_link entity WHERE entity.project_id = :id")
    Flux<ExternalLink> findByProject(Long id);

    @Query("SELECT * FROM external_link entity WHERE entity.project_id IS NULL")
    Flux<ExternalLink> findAllWhereProjectIsNull();

    @Override
    <S extends ExternalLink> Mono<S> save(S entity);

    @Override
    Flux<ExternalLink> findAll();

    @Override
    Mono<ExternalLink> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    @Modifying
    @Query("DELETE FROM external_link WHERE project_id = :projectId")
    reactor.core.publisher.Mono<Void> deleteByProjectId(@org.springframework.data.repository.query.Param("projectId") Long projectId);
}

interface ExternalLinkRepositoryInternal {
    <S extends ExternalLink> Mono<S> save(S entity);

    Flux<ExternalLink> findAllBy(Pageable pageable);

    Flux<ExternalLink> findAll();

    Mono<ExternalLink> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ExternalLink> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ExternalLink> findByCriteria(ExternalLinkCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ExternalLinkCriteria criteria);
}
