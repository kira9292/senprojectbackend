package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.criteria.TeamCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Team entity.
 */
@SuppressWarnings("unused")
@Repository
public interface TeamRepository extends ReactiveCrudRepository<Team, Long>, TeamRepositoryInternal {
    Flux<Team> findAllBy(Pageable pageable);

    @Override
    Mono<Team> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Team> findAllWithEagerRelationships();

    @Override
    Flux<Team> findAllWithEagerRelationships(Pageable page);

    @Query(
        "SELECT entity.* FROM team entity JOIN rel_team__members joinTable ON entity.id = joinTable.team_id WHERE joinTable.members_id = :id"
    )
    Flux<Team> findByMembers(String id);

    @Query("SELECT * FROM team WHERE id = :projectId")
    Mono<Team> findByProject(Long projectId);

    @Override
    <S extends Team> Mono<S> save(S entity);

    @Override
    Flux<Team> findAll();

    @Override
    Mono<Team> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT * FROM team t " +
        "WHERE t.id IN (" +
        "    SELECT team_id FROM rel_team__members rtm " +
        "    JOIN user_profile u ON u.id = rtm.members_id " +
        "    WHERE u.login = :login" +
        ")"
    )
    Flux<Team> findAllByMembers_Login(@Param("login") String login);

    @Query(
        "SELECT t.* FROM team t " +
        "JOIN rel_team__members rtm ON t.id = rtm.team_id " +
        "JOIN user_profile u ON rtm.members_id = u.id " +
        "WHERE t.id = :id AND u.login = :login"
    )
    Mono<Team> findOneByIdAndMemberLogin(@Param("id") Long id, @Param("login") String login);

    @Query("SELECT t.* FROM team t " + "JOIN project p ON p.team_id = t.id " + "WHERE p.id = :projectId")
    Mono<Team> findByProjectWithEagerRelationships(@Param("projectId") Long projectId);

    @Query(
        "SELECT rtm.status FROM rel_team__members rtm " +
        "WHERE rtm.team_id = :teamId AND rtm.members_id = :userId"
    )
    Mono<String> findMemberStatus(@Param("teamId") Long teamId, @Param("userId") String userId);

    @Query("UPDATE project SET team_id = NULL, status = 'DELETED' WHERE team_id = :teamId")
    Mono<Void> updateProjectsForDeletedTeam(@Param("teamId") Long teamId);

    @Query("UPDATE team SET is_deleted = true WHERE id = :teamId")
    Mono<Void> markTeamAsDeleted(@Param("teamId") Long teamId);
}

interface TeamRepositoryInternal {
    <S extends Team> Mono<S> save(S entity);

    Flux<Team> findAllBy(Pageable pageable);

    Flux<Team> findAll();

    Mono<Team> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Team> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Team> findByCriteria(TeamCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(TeamCriteria criteria);

    Mono<Team> findOneWithEagerRelationships(Long id);

    Flux<Team> findAllWithEagerRelationships();

    Flux<Team> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
