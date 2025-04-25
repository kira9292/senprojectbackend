package com.senprojectbackend1.service;

import reactor.core.publisher.Mono;

public interface TeamMemberService {
    public Mono<Boolean> inviteUserToTeam(Long teamId, String userId);

    public Mono<Boolean> processTeamInvitationResponse(Long teamId, String userId, boolean accepted);

    public Mono<Boolean> inviteUserToTeam(Long teamId, String userId, String role);

    public Mono<Boolean> removeUserFromTeam(Long teamId, String userId);
}
