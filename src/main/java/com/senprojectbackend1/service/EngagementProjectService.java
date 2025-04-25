package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.EngagementProjectCriteria;
import com.senprojectbackend1.service.dto.EngagementProjectDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.EngagementProject}.
 */
public interface EngagementProjectService {
    /**
     * Save a engagementProject.
     *
     * @param engagementProjectDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<EngagementProjectDTO> save(EngagementProjectDTO engagementProjectDTO);

    /**
     * Updates a engagementProject.
     *
     * @param engagementProjectDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<EngagementProjectDTO> update(EngagementProjectDTO engagementProjectDTO);

    /**
     * Partially updates a engagementProject.
     *
     * @param engagementProjectDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<EngagementProjectDTO> partialUpdate(EngagementProjectDTO engagementProjectDTO);
    /**
     * Find engagementProjects by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<EngagementProjectDTO> findByCriteria(EngagementProjectCriteria criteria, Pageable pageable);

    /**
     * Find the count of engagementProjects by criteria.
     * @param criteria filtering criteria
     * @return the count of engagementProjects
     */
    public Mono<Long> countByCriteria(EngagementProjectCriteria criteria);

    /**
     * Returns the number of engagementProjects available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" engagementProject.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<EngagementProjectDTO> findOne(Long id);

    /**
     * Delete the "id" engagementProject.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Crée un nouvel engagement (LIKE ou SHARE) pour un projet.
     *
     * @param projectId l'ID du projet
     * @param type le type d'engagement (LIKE ou SHARE)
     * @param login le login de l'utilisateur
     * @return le DTO de l'engagement créé ou existant
     */
    Mono<EngagementProjectDTO> createProjectEngagement(Long projectId, String type, String login);
}
