package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.TeamMembership;
import com.senprojectbackend1.domain.enumeration.MembershipStatus;
import com.senprojectbackend1.repository.TeamMembershipRepository;
import com.senprojectbackend1.service.TeamRelationshipService;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

/**
 * Service for managing team member relationships
 */
@Service
@Transactional
public class TeamRelationshipServiceImpl implements TeamRelationshipService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamRelationshipServiceImpl.class);
    private final TeamMembershipRepository teamMembershipRepository;

    public TeamRelationshipServiceImpl(TeamMembershipRepository teamMembershipRepository) {
        this.teamMembershipRepository = teamMembershipRepository;
    }

    @Override
    public Mono<TeamMembership> addPendingMember(Long teamId, String userId) {
        LOG.debug("Adding pending member {} to team {}", userId, teamId);
        Instant invitedAt = Instant.now();

        return teamMembershipRepository
            .addPendingMember(teamId, userId, null, invitedAt)
            .flatMap(rowsUpdated -> {
                if (rowsUpdated > 0) {
                    return teamMembershipRepository.findByTeamIdAndUserId(teamId, userId);
                } else {
                    return Mono.empty();
                }
            });
    }

    @Override
    public Mono<TeamMembership> updateMembershipStatus(Long teamId, String userId, MembershipStatus status) {
        LOG.debug("Updating membership status for user {} in team {} to {}", userId, teamId, status);
        Instant respondedAt = Instant.now();

        return teamMembershipRepository
            .updateMembershipStatus(teamId, userId, status.name(), respondedAt)
            .flatMap(rowsUpdated -> {
                if (rowsUpdated > 0) {
                    // Retourner l'objet TeamMembership mis à jour
                    return teamMembershipRepository.findByTeamIdAndUserId(teamId, userId);
                } else {
                    return Mono.empty();
                }
            });
    }

    @Override
    public Mono<Boolean> removeMember(Long teamId, String userId) {
        LOG.debug("Removing member from team: teamId={}, userId={}", teamId, userId);
        return teamMembershipRepository
            .deleteByTeamIdAndMembersId(teamId, userId)
            .map(rowsUpdated -> rowsUpdated > 0)
            .defaultIfEmpty(false);
    }

    @Override
    public Mono<Boolean> isMember(Long teamId, String userId) {
        return teamMembershipRepository.findByTeamIdAndUserId(teamId, userId).hasElement();
    }
}
