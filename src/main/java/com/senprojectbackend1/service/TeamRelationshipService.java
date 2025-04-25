package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.TeamMembership;
import com.senprojectbackend1.domain.enumeration.MembershipStatus;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing team relationships.
 */
public interface TeamRelationshipService {
    /**
     * Add a pending member to a team.
     *
     * @param teamId the team ID
     * @param userId the user ID
     * @return the created membership
     */
    Mono<TeamMembership> addPendingMember(Long teamId, String userId);

    /**
     * Update the membership status of a team member.
     *
     * @param teamId the team ID
     * @param userId the user ID
     * @param status the new status
     * @return the updated membership
     */
    Mono<TeamMembership> updateMembershipStatus(Long teamId, String userId, MembershipStatus status);

    public Mono<Boolean> removeMember(Long teamId, String userId);

    public Mono<Boolean> isMember(Long teamId, String userId);
}
