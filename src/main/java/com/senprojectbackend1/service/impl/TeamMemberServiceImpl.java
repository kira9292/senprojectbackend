package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.broker.KafkaProducer;
import com.senprojectbackend1.domain.enumeration.MembershipStatus;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.repository.TeamMembershipRepository;
import com.senprojectbackend1.service.NotificationService;
import com.senprojectbackend1.service.TeamMemberService;
import com.senprojectbackend1.service.TeamRelationshipService;
import com.senprojectbackend1.service.TeamService;
import com.senprojectbackend1.service.util.NotificationActionUtil;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TeamMemberServiceImpl implements TeamMemberService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamMemberServiceImpl.class);
    private final NotificationService notificationService;
    private final TeamService teamService;
    private final TeamRelationshipService teamRelationshipService;
    private final NotificationActionUtil notificationActionUtil;
    private final TeamMembershipRepository teamMembershipRepository;

    public TeamMemberServiceImpl(
        KafkaProducer kafkaProducer,
        NotificationService notificationService,
        TeamService teamService,
        TeamRelationshipService teamRelationshipService,
        NotificationActionUtil notificationActionUtil,
        TeamMembershipRepository teamMembershipRepository
    ) {
        this.notificationService = notificationService;
        this.teamService = teamService;
        this.teamRelationshipService = teamRelationshipService;
        this.notificationActionUtil = notificationActionUtil;
        this.teamMembershipRepository = teamMembershipRepository;
    }

    /**
     * Invites a user to join a team by creating a notification
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user to invite
     * @return A Mono containing a boolean indicating success
     */
    @Override
    public Mono<Boolean> inviteUserToTeam(Long teamId, String userId) {
        return inviteUserToTeam(teamId, userId, null);
    }

    /**
     * Invites a user to join a team by creating a notification and a pending relationship
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user to invite
     * @param role Optional role for the user
     * @return A Mono containing a boolean indicating success
     */
    @Override
    public Mono<Boolean> inviteUserToTeam(Long teamId, String userId, String role) {
        LOG.debug("Inviting user {} to team {} with role {}", userId, teamId, role);

        return teamService
            .findOne(teamId)
            .flatMap(teamDTO -> {
                String content = "You have been invited to join the team: " + teamDTO.getName();
                String baseEndpoint = "/api/teams/" + teamId + "/respond";

                // Nouvelle implémentation des actions
                List<Map<String, Object>> actions = new ArrayList<>();

                // Action Accepter
                Map<String, Object> acceptPayload = new HashMap<>();
                acceptPayload.put("userId", userId);
                acceptPayload.put("accepted", true);
                actions.add(notificationActionUtil.createApiAction("Accept", baseEndpoint, "POST", acceptPayload));

                // Action Rejeter
                Map<String, Object> rejectPayload = new HashMap<>();
                rejectPayload.put("userId", userId);
                rejectPayload.put("accepted", false);
                actions.add(notificationActionUtil.createApiAction("Reject", baseEndpoint, "POST", rejectPayload));

                // Convertir en JSON
                String actionsJson = notificationActionUtil.createActionsJson(actions);

                // Utiliser la méthode addPendingMember du repository
                Instant invitedAt = Instant.now();
                return teamMembershipRepository
                    .addPendingMember(teamId, userId, role, invitedAt)
                    .flatMap(rowsUpdated -> {
                        if (rowsUpdated > 0) {
                            return notificationService
                                .createNotification(userId, content, NotificationType.TEAM_INVITATION, teamId.toString(), actionsJson)
                                .map(notification -> true);
                        } else {
                            return Mono.just(false);
                        }
                    })
                    .defaultIfEmpty(false);
            })
            .defaultIfEmpty(false);
    }

    /**
     * Process a user's response to a team invitation
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user
     * @param accepted Whether the invitation was accepted
     * @return A Mono containing a boolean indicating success
     */
    @Override
    public Mono<Boolean> processTeamInvitationResponse(Long teamId, String userId, boolean accepted) {
        NotificationType notificationType = accepted ? NotificationType.TEAM_JOINED : NotificationType.TEAM_REJECTED;
        MembershipStatus status = accepted ? MembershipStatus.ACCEPTED : MembershipStatus.REJECTED;

        return teamService
            .findOne(teamId)
            .flatMap(teamDTO -> {
                String content = accepted
                    ? "You have joined the team: " + teamDTO.getName()
                    : "You have declined the invitation to join: " + teamDTO.getName();

                return teamRelationshipService
                    .updateMembershipStatus(teamId, userId, status)
                    .flatMap(membership -> {
                        if (accepted) {
                            // Pour les invitations acceptées, ajouter une action "Quitter l'équipe"
                            String baseEndpoint = "/api/teams/" + teamId + "/members/" + userId;

                            List<Map<String, Object>> actions = new ArrayList<>();
                            actions.add(notificationActionUtil.createApiAction("Leave Team", baseEndpoint, "DELETE", null));

                            // Ajouter une action de redirection pour voir l'équipe
                            actions.add(notificationActionUtil.createRedirectAction("View Team", "/team/" + teamId));

                            String actionsJson = notificationActionUtil.createActionsJson(actions);

                            return notificationService.createNotification(
                                userId,
                                content,
                                notificationType,
                                teamId.toString(),
                                actionsJson
                            );
                        } else {
                            // Pour les invitations refusées, pas d'action supplémentaire
                            return notificationService.createNotification(userId, content, notificationType, teamId.toString(), null);
                        }
                    })
                    .flatMap(notification -> {
                        // Supprimer la notification d'invitation
                        return notificationService
                            .deleteByUserIdAndEntityIdAndType(userId, teamId.toString(), NotificationType.TEAM_INVITATION)
                            .thenReturn(true);
                    })
                    .map(notification -> true)
                    .defaultIfEmpty(false);
            })
            .defaultIfEmpty(false);
    }

    /**
     * Remove a user from a team
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user
     * @return A Mono containing a boolean indicating success
     */
    @Override
    public Mono<Boolean> removeUserFromTeam(Long teamId, String userId) {
        LOG.debug("Removing user {} from team {}", userId, teamId);

        return teamService
            .findOne(teamId)
            .flatMap(teamDTO -> {
                String content = "You have been removed from the team: " + teamDTO.getName();
                return teamRelationshipService
                    .removeMember(teamId, userId)
                    .flatMap(removed -> {
                        if (Boolean.TRUE.equals(removed)) {
                            return notificationService
                                .createNotification(userId, content, NotificationType.TEAM_LEFT, teamId.toString())
                                .map(notification -> true);
                        }
                        return Mono.just(false);
                    });
            })
            .defaultIfEmpty(false);
    }
}
