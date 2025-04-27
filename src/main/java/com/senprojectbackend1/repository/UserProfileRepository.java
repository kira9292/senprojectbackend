package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.criteria.UserProfileCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the UserProfile entity.
 */
@SuppressWarnings("unused")
@Repository
public interface UserProfileRepository extends R2dbcRepository<UserProfile, String>, UserProfileRepositoryInternal {
    Flux<UserProfile> findAllBy(Pageable pageable);

    @Override
    Mono<UserProfile> findOneWithEagerRelationships(String id);

    Mono<UserProfile> findOneByLogin(String login);

    @Override
    Flux<UserProfile> findAllWithEagerRelationships();

    @Override
    Flux<UserProfile> findAllWithEagerRelationships(Pageable page);

    @Override
    <S extends UserProfile> Mono<S> save(S entity);

    @Override
    Flux<UserProfile> findAll();

    @Override
    Mono<UserProfile> findById(String id);

    @Override
    Mono<Void> deleteById(String id);

    @Override
    Mono<Void> update(UserProfile userProfile);

    @Query("SELECT u.* FROM user_profile u JOIN rel_team__members rtm ON u.id = rtm.members_id WHERE rtm.team_id = :teamId")
    Flux<UserProfile> findTeamMembersByTeamId(@Param("teamId") Long teamId);
}

interface UserProfileRepositoryInternal {
    <S extends UserProfile> Mono<S> save(S entity);

    Flux<UserProfile> findAllBy(Pageable pageable);

    Flux<UserProfile> findAll();

    Mono<UserProfile> findById(String id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<UserProfile> findAllBy(Pageable pageable, Criteria criteria);
    Flux<UserProfile> findByCriteria(UserProfileCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(UserProfileCriteria criteria);

    Mono<UserProfile> findOneWithEagerRelationships(String id);

    Flux<UserProfile> findAllWithEagerRelationships();

    Flux<UserProfile> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(String id);

    Mono<UserProfile> create(UserProfile userProfile);

    Mono<Void> update(UserProfile userProfile);
}
