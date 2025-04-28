package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.TeamMembership;
import java.time.Instant;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for team membership operations.
 */
@Repository
public interface TeamMembershipRepository extends ReactiveCrudRepository<TeamMembership, Long> {
    @Query("SELECT * FROM rel_team__members WHERE team_id = :teamId AND members_id = :userId")
    Mono<TeamMembership> findByTeamIdAndUserId(Long teamId, String userId);

    @Modifying
    @Query("DELETE FROM rel_team__members WHERE team_id = :teamId AND members_id = :userId")
    Mono<Integer> deleteByTeamIdAndMembersId(Long teamId, String userId);

    @Modifying
    @Query(
        "INSERT INTO rel_team__members (team_id, members_id, status, role, invited_at) " +
        "VALUES (:teamId, :userId, 'PENDING', :role, :invitedAt)"
    )
    Mono<Integer> addPendingMember(Long teamId, String userId, String role, Instant invitedAt);

    @Modifying
    @Query(
        "UPDATE rel_team__members " +
        "SET status = :status, responded_at = :respondedAt " +
        "WHERE team_id = :teamId AND members_id = :userId"
    )
    Mono<Integer> updateMembershipStatus(Long teamId, String userId, String status, Instant respondedAt);

    @Query("UPDATE rel_team__members SET role = :newRole WHERE team_id = :teamId AND members_id = :userId")
    Mono<Void> updateMemberRole(@Param("teamId") Long teamId, @Param("userId") String userId, @Param("newRole") String newRole);

    @Query("SELECT COUNT(*) FROM rel_team__members WHERE team_id = :teamId AND role = :role")
    Mono<Long> countByTeamIdAndRole(@Param("teamId") Long teamId, @Param("role") String role);

    @Modifying
    @Query(
        "INSERT INTO rel_team__members (team_id, members_id, status, role, invited_at, responded_at) " +
        "VALUES (:teamId, :userId, :status, :role, :invitedAt, :respondedAt)"
    )
    Mono<Integer> addMemberWithStatusAndRole(
        Long teamId,
        String userId,
        String status,
        String role,
        Instant invitedAt,
        Instant respondedAt
    );
}
