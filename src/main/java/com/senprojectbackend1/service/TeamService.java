package com.senprojectbackend1.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.senprojectbackend1.domain.TeamMembership;
import com.senprojectbackend1.domain.criteria.TeamCriteria;
import com.senprojectbackend1.domain.enumeration.MembershipStatus;
import com.senprojectbackend1.service.dto.ProjectSimple2DTO;
import com.senprojectbackend1.service.dto.TeamDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.Team}.
 */
public interface TeamService {
    /**
     * Save a team.
     *
     * @param teamDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TeamDTO> save(TeamDTO teamDTO);

    /**
     * Updates a team.
     *
     * @param teamDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TeamDTO> update(TeamDTO teamDTO);

    /**
     * Partially updates a team.
     *
     * @param teamDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TeamDTO> partialUpdate(TeamDTO teamDTO);
    /**
     * Find teams by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamDTO> findByCriteria(TeamCriteria criteria, Pageable pageable);

    /**
     * Find the count of teams by criteria.
     * @param criteria filtering criteria
     * @return the count of teams
     */
    public Mono<Long> countByCriteria(TeamCriteria criteria);

    /**
     * Get all the teams with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TeamDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of teams available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" team.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TeamDTO> findOne(Long id);

    /**
     * Delete the "id" team.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    Flux<TeamDTO> findAllByMemberLogin(String login);

    Mono<TeamDTO> findOneByIdAndMemberLogin(Long id, String login);

    Flux<ProjectSimple2DTO> getTeamProjects(Long teamId);

    Mono<TeamDTO> findByProjectId(Long id);

    Mono<Void> deleteTeamAndUpdateProjects(Long teamId, String userLogin);

    /**
     * Crée une équipe et ajoute les membres à partir d'une liste de logins.
     *
     * @param teamDTO les infos de l'équipe à créer
     * @param targetLogins les logins des membres à ajouter
     * @return l'équipe créée avec les membres
     */
    Mono<TeamDTO> createTeamWithMembers(TeamDTO teamDTO, List<String> targetLogins);

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
     * @return the updated membership
     */
    public Mono<Boolean> inviteUserToTeam(Long teamId, String userId);

    public Mono<Boolean> processTeamInvitationResponse(Long teamId, String userId, boolean accepted);

    public Mono<Boolean> inviteUserToTeam(Long teamId, String userId, String role);

    public Mono<Boolean> removeUserFromTeam(Long teamId, String userId);

    Mono<TeamMembership> updateMembershipStatus(Long teamId, String userId, MembershipStatus status);

    Mono<Boolean> removeMember(Long teamId, String userId);

    Mono<Boolean> isMember(Long teamId, String userId);

    Mono<String> getMemberRole(Long teamId, String userId);

    Mono<Void> changeMemberRole(Long teamId, String userId, String newRole);

    Mono<Long> countLeads(Long teamId);

    /**
     * Met à jour uniquement le nom, la description et le logo d'une équipe.
     * @param id l'id de l'équipe
     * @param name le nouveau nom
     * @param description la nouvelle description
     * @param logo le nouveau logo
     * @return Mono<TeamDTO> l'équipe mise à jour
     */
    Mono<TeamDTO> updateTeamInfo(Long id, String name, String description, String logo);
}
