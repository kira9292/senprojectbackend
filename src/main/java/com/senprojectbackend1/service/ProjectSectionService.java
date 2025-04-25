package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.ProjectSectionCriteria;
import com.senprojectbackend1.service.dto.ProjectSectionDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.ProjectSection}.
 */
public interface ProjectSectionService {
    /**
     * Save a projectSection.
     *
     * @param projectSectionDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProjectSectionDTO> save(ProjectSectionDTO projectSectionDTO);

    /**
     * Updates a projectSection.
     *
     * @param projectSectionDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProjectSectionDTO> update(ProjectSectionDTO projectSectionDTO);

    /**
     * Partially updates a projectSection.
     *
     * @param projectSectionDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProjectSectionDTO> partialUpdate(ProjectSectionDTO projectSectionDTO);
    /**
     * Find projectSections by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProjectSectionDTO> findByCriteria(ProjectSectionCriteria criteria, Pageable pageable);

    /**
     * Find the count of projectSections by criteria.
     * @param criteria filtering criteria
     * @return the count of projectSections
     */
    public Mono<Long> countByCriteria(ProjectSectionCriteria criteria);

    /**
     * Returns the number of projectSections available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" projectSection.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProjectSectionDTO> findOne(Long id);

    /**
     * Delete the "id" projectSection.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
