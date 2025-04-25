package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.EngagementTeam;
import com.senprojectbackend1.domain.criteria.EngagementTeamCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the EngagementTeam entity.
 */
@SuppressWarnings("unused")
@Repository
public interface EngagementTeamRepository extends ReactiveCrudRepository<EngagementTeam, Long>, EngagementTeamRepositoryInternal {
    Flux<EngagementTeam> findAllBy(Pageable pageable);

    @Query("SELECT * FROM engagement_team entity WHERE entity.team_id = :id")
    Flux<EngagementTeam> findByTeam(Long id);

    @Query("SELECT * FROM engagement_team entity WHERE entity.team_id IS NULL")
    Flux<EngagementTeam> findAllWhereTeamIsNull();

    @Query("SELECT * FROM engagement_team entity WHERE entity.user_id = :id")
    Flux<EngagementTeam> findByUser(Long id);

    @Query("SELECT * FROM engagement_team entity WHERE entity.user_id IS NULL")
    Flux<EngagementTeam> findAllWhereUserIsNull();

    @Override
    <S extends EngagementTeam> Mono<S> save(S entity);

    @Override
    Flux<EngagementTeam> findAll();

    @Override
    Mono<EngagementTeam> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface EngagementTeamRepositoryInternal {
    <S extends EngagementTeam> Mono<S> save(S entity);

    Flux<EngagementTeam> findAllBy(Pageable pageable);

    Flux<EngagementTeam> findAll();

    Mono<EngagementTeam> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<EngagementTeam> findAllBy(Pageable pageable, Criteria criteria);
    Flux<EngagementTeam> findByCriteria(EngagementTeamCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(EngagementTeamCriteria criteria);
}
