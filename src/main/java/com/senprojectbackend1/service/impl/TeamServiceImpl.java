package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.TeamMembership;
import com.senprojectbackend1.domain.criteria.TeamCriteria;
import com.senprojectbackend1.domain.enumeration.MembershipStatus;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.repository.TeamMembershipRepository;
import com.senprojectbackend1.repository.TeamRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.CloudinaryService;
import com.senprojectbackend1.service.NotificationService;
import com.senprojectbackend1.service.TeamService;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.ProjectSimple2DTO;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.dto.TeamMemberDetailsDTO;
import com.senprojectbackend1.service.dto.UserProfileSimpleDTO;
import com.senprojectbackend1.service.exception.ProjectBusinessException;
import com.senprojectbackend1.service.mapper.ProjectMapper;
import com.senprojectbackend1.service.mapper.TeamMapper;
import com.senprojectbackend1.service.mapper.UserProfileMapper;
import com.senprojectbackend1.service.util.NotificationActionUtil;
import java.time.Instant;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.Team}.
 */
@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamServiceImpl.class);
    private final TeamRepository teamRepository;
    private final ProjectRepository projectRepository;
    private final TeamMapper teamMapper;
    private final NotificationService notificationService;
    private final ProjectMapper projectMapper;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserProfileService userProfileService;
    private final NotificationActionUtil notificationActionUtil;
    private final TeamMembershipRepository teamMembershipRepository;
    private final CloudinaryService cloudinaryService;

    public TeamServiceImpl(
        TeamRepository teamRepository,
        ProjectRepository projectRepository,
        TeamMapper teamMapper,
        NotificationService notificationService,
        ProjectMapper projectMapper,
        UserProfileRepository userProfileRepository,
        UserProfileMapper userProfileMapper,
        UserProfileService userProfileService,
        NotificationActionUtil notificationActionUtil,
        TeamMembershipRepository teamMembershipRepository,
        CloudinaryService cloudinaryService
    ) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.teamMapper = teamMapper;
        this.notificationService = notificationService;
        this.projectMapper = projectMapper;
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userProfileService = userProfileService;
        this.notificationActionUtil = notificationActionUtil;
        this.teamMembershipRepository = teamMembershipRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public Mono<TeamDTO> save(TeamDTO teamDTO) {
        LOG.debug("Request to save Team : {}", teamDTO);
        teamDTO.setCreatedAt(Instant.now());
        teamDTO.setTotalLikes(0);
        teamDTO.setIsDeleted(false);
        return teamRepository.save(teamMapper.toEntity(teamDTO)).map(teamMapper::toDto);
    }

    @Override
    public Mono<TeamDTO> update(TeamDTO teamDTO) {
        LOG.debug("Request to update Team : {}", teamDTO);
        return teamRepository.save(teamMapper.toEntity(teamDTO)).map(teamMapper::toDto);
    }

    @Override
    public Mono<TeamDTO> partialUpdate(TeamDTO teamDTO) {
        LOG.debug("Request to partially update Team : {}", teamDTO);

        return teamRepository
            .findById(teamDTO.getId())
            .map(existingTeam -> {
                teamMapper.partialUpdate(existingTeam, teamDTO);

                return existingTeam;
            })
            .flatMap(teamRepository::save)
            .map(teamMapper::toDto);
    }

    @Override
    public Mono<TeamDTO> updateWithValidation(TeamDTO teamDTO, String userLogin) {
        LOG.debug("Request to update Team with validation : {}, userLogin: {}", teamDTO, userLogin);
        return validateUserIsTeamLead(teamDTO.getId(), userLogin).then(update(teamDTO));
    }

    @Override
    public Mono<TeamDTO> partialUpdateWithValidation(TeamDTO teamDTO, String userLogin) {
        LOG.debug("Request to partially update Team with validation : {}, userLogin: {}", teamDTO, userLogin);
        return validateUserIsTeamLead(teamDTO.getId(), userLogin).then(partialUpdate(teamDTO));
    }

    private Mono<Void> validateUserIsTeamLead(Long teamId, String userLogin) {
        return userProfileRepository
            .findOneByLogin(userLogin)
            .switchIfEmpty(Mono.error(new RuntimeException("Profil utilisateur non trouvé")))
            .flatMap(userProfile ->
                isMember(teamId, userProfile.getId()).flatMap(isMember -> {
                    if (Boolean.FALSE.equals(isMember)) {
                        return Mono.error(new RuntimeException("Vous n'êtes pas membre de cette équipe"));
                    }
                    return getMemberRole(teamId, userProfile.getId()).flatMap(role -> {
                        if (!"LEAD".equals(role)) {
                            return Mono.error(new RuntimeException("Seul un LEAD peut modifier les informations de l'équipe"));
                        }
                        return Mono.empty();
                    });
                })
            );
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamDTO> findByCriteria(TeamCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Teams by Criteria");
        return teamRepository.findByCriteria(criteria, pageable).map(teamMapper::toDto);
    }

    /**
     * Find the count of teams by criteria.
     * @param criteria filtering criteria
     * @return the count of teams
     */
    public Mono<Long> countByCriteria(TeamCriteria criteria) {
        LOG.debug("Request to get the count of all Teams by Criteria");
        return teamRepository.countByCriteria(criteria);
    }

    public Flux<TeamDTO> findAllWithEagerRelationships(Pageable pageable) {
        return teamRepository.findAllWithEagerRelationships(pageable).map(teamMapper::toDto);
    }

    public Mono<Long> countAll() {
        return teamRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TeamDTO> findOne(Long id) {
        LOG.debug("Request to get Team : {}", id);
        return teamRepository.findOneWithEagerRelationships(id).map(teamMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Team : {}", id);
        return teamRepository.deleteById(id);
    }

    @Override
    public Flux<TeamDTO> findAllByMemberLogin(String login) {
        LOG.debug("Request to find all teams for user {}", login);
        return teamRepository
            .findAllByMembers_Login(login)
            .map(teamMapper::toDto)
            .flatMap(this::enrichTeamWithMembers)
            .filter(team -> team.getMembers().stream().anyMatch(m -> m.getStatus() != null && m.getLogin().equals(login)));
    }

    private Mono<TeamDTO> enrichTeamWithMembers(TeamDTO team) {
        return userProfileRepository
            .findTeamMembersByTeamId(team.getId())
            .flatMap(userProfile ->
                teamMembershipRepository
                    .findByTeamIdAndUserId(team.getId(), userProfile.getId())
                    .map(membership -> {
                        UserProfileSimpleDTO simple = userProfileMapper.toSimpleDto(userProfile);
                        return new TeamMemberDetailsDTO(simple, membership.getStatus(), membership.getRole());
                    })
            )
            .collectList()
            .map(members -> {
                team.setMembers(new HashSet<>(members));
                return team;
            });
    }

    @Override
    public Mono<TeamDTO> findOneByIdAndMemberLogin(Long id, String login) {
        LOG.debug("Request to find team with id {} for user {}", id, login);
        return teamRepository
            .findOneByIdAndMemberLogin(id, login)
            .filter(Objects::nonNull)
            .switchIfEmpty(Mono.error(new ProjectBusinessException("Équipe non trouvée ou accès refusé")))
            .map(teamMapper::toDto)
            .flatMap(this::enrichTeamWithMembers);
    }

    @Override
    public Flux<ProjectSimple2DTO> getTeamProjects(Long teamId) {
        LOG.debug("Request to find all projects for team with id {}", teamId);
        return projectRepository
            .findProjectsByTeamId(teamId)
            .flatMap(project ->
                projectRepository
                    .findTagsByProjectId(project.getId())
                    .collectList()
                    .map(tags -> {
                        project.setTags(new HashSet<>(tags));
                        return projectMapper.toSimpleDto(project);
                    })
            );
    }

    @Override
    public Mono<TeamDTO> findByProjectId(Long id) {
        LOG.debug("Request to find team for Project : {}", id);
        return teamRepository.findByProjectWithEagerRelationships(id).map(teamMapper::toDto).flatMap(this::enrichTeamWithMembers);
    }

    @Override
    public Mono<Void> deleteTeamAndUpdateProjects(Long teamId, String userLogin) {
        LOG.debug("Request to delete Team and update its projects : {}, user: {}", teamId, userLogin);
        return userProfileService
            .getUserProfileSimpleByLogin(userLogin)
            .switchIfEmpty(Mono.error(new ProjectBusinessException("User profile not found")))
            .flatMap(userProfile -> {
                return teamRepository
                    .findMemberStatus(teamId, userProfile.getId())
                    .switchIfEmpty(Mono.error(new ProjectBusinessException("User is not a member of the team")))
                    .flatMap(status -> {
                        if (!MembershipStatus.ACCEPTED.name().equals(status)) {
                            return Mono.error(new ProjectBusinessException("User is not an accepted member of the team"));
                        }
                        return teamRepository.updateProjectsForDeletedTeam(teamId).then(teamRepository.markTeamAsDeleted(teamId));
                    });
            });
    }

    @Override
    public Mono<TeamDTO> createTeamWithMembers(TeamDTO teamDTO, List<String> targetLogins) {
        LOG.debug("Création d'une équipe avec membres : {}", targetLogins);

        // Vérification préalable de l'unicité du nom
        return checkTeamNameExists(teamDTO.getName(), null).flatMap(exists -> {
            if (exists) {
                return Mono.error(new ProjectBusinessException("Une équipe avec ce nom existe déjà"));
            }

            teamDTO.setCreatedAt(Instant.now());
            teamDTO.setTotalLikes(0);
            teamDTO.setIsDeleted(false);
            return createTeamInternal(teamDTO, targetLogins);
        });
    }

    private Mono<TeamDTO> createTeamInternal(TeamDTO teamDTO, List<String> targetLogins) {
        Mono<TeamDTO> teamMono;
        if (teamDTO.getLogo() != null && !teamDTO.getLogo().isBlank() && !teamDTO.getLogo().startsWith("http")) {
            teamMono = cloudinaryService
                .uploadBase64Image(teamDTO.getLogo(), "team_logo")
                .map(url -> {
                    teamDTO.setLogo(url);
                    return teamDTO;
                });
        } else {
            teamMono = Mono.just(teamDTO);
        }
        return teamMono
            .flatMap(dto -> teamRepository.save(teamMapper.toEntity(dto)))
            .flatMap(team ->
                // Récupérer le login du créateur depuis le contexte de sécurité
                ReactiveSecurityContextHolder.getContext()
                    .map(ctx -> ctx.getAuthentication().getName())
                    .flatMap(creatorLogin ->
                        userProfileRepository
                            .findOneByLogin(creatorLogin)
                            .flatMap(creatorProfile ->
                                // Ajouter le créateur comme LEAD/ACCEPTED sans notification
                                teamMembershipRepository
                                    .addMemberWithStatusAndRole(
                                        team.getId(),
                                        creatorProfile.getId(),
                                        MembershipStatus.ACCEPTED.name(),
                                        "LEAD",
                                        Instant.now(),
                                        Instant.now()
                                    )
                                    .thenReturn(creatorProfile)
                            )
                    )
                    .flatMap(creatorProfile ->
                        // Ajouter les autres membres comme READ/PENDING avec notification
                        Flux.fromIterable(targetLogins)
                            .filter(login -> !login.equals(creatorProfile.getLogin()))
                            .flatMap(login ->
                                userProfileRepository
                                    .findOneByLogin(login)
                                    .flatMap(userProfile -> inviteUserToTeam(team.getId(), userProfile.getId(), "READ"))
                            )
                            .then(Mono.just(team))
                    )
            )
            .flatMap(savedTeam -> teamRepository.findOneWithEagerRelationships(savedTeam.getId()))
            .map(teamMapper::toDto);
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

        return findOne(teamId)
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

        return findOne(teamId)
            .flatMap(teamDTO -> {
                String content = accepted
                    ? "You have joined the team: " + teamDTO.getName()
                    : "You have declined the invitation to join: " + teamDTO.getName();

                return updateMembershipStatus(teamId, userId, status)
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

        return findOne(teamId)
            .flatMap(teamDTO -> {
                String content = "You have been removed from the team: " + teamDTO.getName();
                return removeMember(teamId, userId).flatMap(removed -> {
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

    @Override
    public Mono<String> getMemberRole(Long teamId, String userId) {
        return teamMembershipRepository.findByTeamIdAndUserId(teamId, userId).map(TeamMembership::getRole);
    }

    @Override
    public Mono<Void> changeMemberRole(Long teamId, String userId, String newRole) {
        return teamMembershipRepository.updateMemberRole(teamId, userId, newRole).then();
    }

    @Override
    public Mono<Long> countLeads(Long teamId) {
        return teamMembershipRepository.countByTeamIdAndRole(teamId, "LEAD");
    }

    @Override
    public Mono<TeamDTO> updateTeamInfo(Long id, String name, String description, String logo) {
        Mono<String> logoMono;
        if (logo != null && !logo.isBlank() && !logo.startsWith("http")) {
            logoMono = cloudinaryService.uploadBase64Image(logo, "team_logo");
        } else {
            logoMono = Mono.just(logo);
        }
        return logoMono.flatMap(finalLogo ->
            teamRepository
                .updateTeamInfo(id, name, description, finalLogo)
                .flatMap(rows -> {
                    if (rows > 0) {
                        return teamRepository.findOneWithEagerRelationships(id).map(teamMapper::toDto);
                    } else {
                        return Mono.error(new ProjectBusinessException("Équipe non trouvée"));
                    }
                })
        );
    }

    @Override
    public Mono<Boolean> checkTeamNameExists(String name, Long editTeamId) {
        if (name == null || name.trim().isEmpty()) {
            return Mono.error(new ProjectBusinessException("Le nom ne peut pas être vide"));
        }

        if (editTeamId != null) {
            return teamRepository
                .findById(editTeamId)
                .switchIfEmpty(Mono.error(new ProjectBusinessException("Équipe à éditer non trouvée")))
                .flatMap(existingTeam -> {
                    if (existingTeam.getName().equalsIgnoreCase(name.trim())) {
                        return Mono.just(false); // Même nom que l'existant
                    }
                    return teamRepository.existsByNameAndIdNot(name.trim(), editTeamId);
                });
        }

        return teamRepository.existsByName(name.trim());
    }

    @Override
    public Flux<TeamMemberDetailsDTO> getAcceptedMembersWithoutRoles(Long teamId) {
        LOG.debug("Getting accepted members without roles for team: {}", teamId);
        return teamMembershipRepository
            .findByTeamIdAndStatus(teamId, "ACCEPTED")
            .flatMap(membership ->
                userProfileRepository
                    .findById(membership.getMembersId())
                    .map(user -> {
                        TeamMemberDetailsDTO memberDTO = new TeamMemberDetailsDTO();
                        memberDTO.setId(user.getId());
                        memberDTO.setLogin(user.getLogin());
                        memberDTO.setFirstName(user.getFirstName());
                        memberDTO.setLastName(user.getLastName());
                        memberDTO.setEmail(user.getEmail());
                        memberDTO.setImageUrl(user.getImageUrl());
                        // Ne pas définir le rôle ni le statut pour la vue publique
                        return memberDTO;
                    })
            );
    }

    @Override
    public Flux<ProjectSimple2DTO> getPublishedTeamProjects(Long teamId) {
        LOG.debug("Request to find published projects for team with id {}", teamId);
        return getTeamProjects(teamId).filter(project -> ProjectStatus.PUBLISHED.equals(project.getStatus()));
    }
}
