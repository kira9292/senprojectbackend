package com.senprojectbackend1.service;

import com.netflix.appinfo.ApplicationInfoManager;
import com.senprojectbackend1.domain.criteria.TeamCriteria;
import com.senprojectbackend1.service.dto.ProjectSimple2DTO;
import com.senprojectbackend1.service.dto.TeamDTO;
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
}
