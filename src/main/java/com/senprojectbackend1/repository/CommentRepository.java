package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Comment;
import com.senprojectbackend1.domain.criteria.CommentCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Comment entity.
 */
@SuppressWarnings("unused")
@Repository
public interface CommentRepository extends ReactiveCrudRepository<Comment, Long>, CommentRepositoryInternal {
    Flux<Comment> findAllBy(Pageable pageable);

    @Query("SELECT * FROM comment entity WHERE entity.user_id = :id")
    Flux<Comment> findByUser(Long id);

    @Query("SELECT * FROM comment entity WHERE entity.user_id IS NULL")
    Flux<Comment> findAllWhereUserIsNull();

    @Query("SELECT * FROM comment entity WHERE entity.project_id = :id")
    Flux<Comment> findByProject(Long id);

    @Query("SELECT * FROM comment entity WHERE entity.project_id IS NULL")
    Flux<Comment> findAllWhereProjectIsNull();

    @Override
    <S extends Comment> Mono<S> save(S entity);

    @Override
    Flux<Comment> findAll();

    @Override
    Mono<Comment> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface CommentRepositoryInternal {
    <S extends Comment> Mono<S> save(S entity);

    Flux<Comment> findAllBy(Pageable pageable);

    Flux<Comment> findAll();

    Mono<Comment> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Comment> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Comment> findByCriteria(CommentCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(CommentCriteria criteria);
}
