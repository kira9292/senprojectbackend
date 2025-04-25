package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.security.SecurityUtils;
import com.senprojectbackend1.service.TeamMemberService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing team membership operations.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamMembershipResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeamMembershipResource.class);
    private final TeamMemberService teamMemberService;
    private final UserProfileRepository userProfileRepository;

    public TeamMembershipResource(TeamMemberService teamMemberService, UserProfileRepository userProfileRepository) {
        this.teamMemberService = teamMemberService;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * {@code POST /teams/{teamId}/invite/{userId}} : Invite a user to join a team
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user to invite
     * @param role Optional role for the user
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the result,
     * or with status {@code 404 (Not Found)} if the team doesn't exist
     */
    @PostMapping("/{teamId}/invite/{userId}")
    public Mono<ResponseEntity<Boolean>> inviteUserToTeam(
        @PathVariable Long teamId,
        @PathVariable String userId,
        @RequestParam(required = false) String role
    ) {
        LOG.debug("REST request to invite user {} to team {} with role {}", userId, teamId, role);
        return teamMemberService
            .inviteUserToTeam(teamId, userId, role)
            .map(result -> {
                if (Boolean.TRUE.equals(result)) {
                    return ResponseEntity.ok().body(true);
                } else {
                    return ResponseEntity.notFound().build();
                }
            });
    }

    /**
     * {@code POST /teams/{teamId}/respond} : Respond to a team invitation
     *
     * @param teamId   The ID of the team
     * @param accepted Whether the invitation was accepted
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the result,
     * or with status {@code 404 (Not Found)} if the team doesn't exist
     */

    @PostMapping("/{teamId}/respond")
    public Mono<ResponseEntity<?>> respondToInvitation(@PathVariable Long teamId, @RequestParam(required = true) boolean accepted) {
        LOG.debug("REST request to respond to team invitation: teamId={}, accepted={}", teamId, accepted);

        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login ->
                userProfileRepository
                    .findOneByLogin(login)
                    .flatMap(user ->
                        teamMemberService
                            .processTeamInvitationResponse(teamId, user.getId(), accepted)
                            .map(result -> {
                                if (Boolean.TRUE.equals(result)) {
                                    return ResponseEntity.ok().body(true);
                                } else {
                                    return ResponseEntity.notFound().build();
                                }
                            })
                    )
            )
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE /teams/{teamId}/members/{userId}} : Remove a user from a team
     *
     * @param teamId The ID of the team
     * @param userId The ID of the user to remove
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the result,
     * or with status {@code 404 (Not Found)} if the team or user doesn't exist
     */
    @DeleteMapping("/{teamId}/members/{userId}")
    public Mono<ResponseEntity<Boolean>> removeTeamMember(@PathVariable Long teamId, @PathVariable String userId) {
        LOG.debug("REST request to remove user {} from team {}", userId, teamId);

        return teamMemberService
            .removeUserFromTeam(teamId, userId)
            .map(result -> {
                if (Boolean.TRUE.equals(result)) {
                    return ResponseEntity.ok().body(true);
                } else {
                    return ResponseEntity.notFound().build();
                }
            });
    }
}
