package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.criteria.EngagementProjectCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EngagementProject entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EngagementProjectRepository extends ReactiveCrudRepository<EngagementProject, Long>, EngagementProjectRepositoryInternal {
    Flux<EngagementProject> findAllBy(Pageable pageable);

    @Query("SELECT * FROM engagement_project entity WHERE entity.user_id = :id")
    Flux<EngagementProject> findByUser(Long id);

    @Query("SELECT * FROM engagement_project entity WHERE entity.user_id IS NULL")
    Flux<EngagementProject> findAllWhereUserIsNull();

    @Query("SELECT * FROM engagement_project entity WHERE entity.project_id = :id")
    Flux<EngagementProject> findByProject(Long id);

    @Query("SELECT * FROM engagement_project entity WHERE entity.project_id IS NULL")
    Flux<EngagementProject> findAllWhereProjectIsNull();

    @Override
    <S extends EngagementProject> Mono<S> save(S entity);

    @Override
    Flux<EngagementProject> findAll();

    @Override
    Mono<EngagementProject> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    @Query("SELECT * FROM engagement_project WHERE user_id = :userId AND project_id = :projectId AND type = :type")
    Mono<EngagementProject> findEngagementByUserIdAndProjectIdAndType(
        @Param("userId") String userId,
        @Param("projectId") Long projectId,
        @Param("type") String type
    );
}

interface EngagementProjectRepositoryInternal {
    <S extends EngagementProject> Mono<S> save(S entity);

    Flux<EngagementProject> findAllBy(Pageable pageable);

    Flux<EngagementProject> findAll();

    Mono<EngagementProject> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<EngagementProject> findAllBy(Pageable pageable, Criteria criteria);
    Flux<EngagementProject> findByCriteria(EngagementProjectCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(EngagementProjectCriteria criteria);
}
