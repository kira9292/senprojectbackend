package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.EngagementTeamCriteria;
import com.senprojectbackend1.service.dto.EngagementTeamDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.EngagementTeam}.
 */
public interface EngagementTeamService {
    /**
     * Save a engagementTeam.
     *
     * @param engagementTeamDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EngagementTeamDTO> save(EngagementTeamDTO engagementTeamDTO);

    /**
     * Updates a engagementTeam.
     *
     * @param engagementTeamDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EngagementTeamDTO> update(EngagementTeamDTO engagementTeamDTO);

    /**
     * Partially updates a engagementTeam.
     *
     * @param engagementTeamDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EngagementTeamDTO> partialUpdate(EngagementTeamDTO engagementTeamDTO);
    /**
     * Find engagementTeams by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EngagementTeamDTO> findByCriteria(EngagementTeamCriteria criteria, Pageable pageable);

    /**
     * Find the count of engagementTeams by criteria.
     * @param criteria filtering criteria
     * @return the count of engagementTeams
     */
    public Mono<Long> countByCriteria(EngagementTeamCriteria criteria);

    /**
     * Returns the number of engagementTeams available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" engagementTeam.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EngagementTeamDTO> findOne(Long id);

    /**
     * Delete the "id" engagementTeam.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
