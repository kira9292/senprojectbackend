package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.ProjectGalleryCriteria;
import com.senprojectbackend1.service.dto.ProjectGalleryDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.ProjectGallery}.
 */
public interface ProjectGalleryService {
    /**
     * Save a projectGallery.
     *
     * @param projectGalleryDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProjectGalleryDTO> save(ProjectGalleryDTO projectGalleryDTO);

    /**
     * Updates a projectGallery.
     *
     * @param projectGalleryDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProjectGalleryDTO> update(ProjectGalleryDTO projectGalleryDTO);

    /**
     * Partially updates a projectGallery.
     *
     * @param projectGalleryDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProjectGalleryDTO> partialUpdate(ProjectGalleryDTO projectGalleryDTO);
    /**
     * Find projectGalleries by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProjectGalleryDTO> findByCriteria(ProjectGalleryCriteria criteria, Pageable pageable);

    /**
     * Find the count of projectGalleries by criteria.
     * @param criteria filtering criteria
     * @return the count of projectGalleries
     */
    public Mono<Long> countByCriteria(ProjectGalleryCriteria criteria);

    /**
     * Returns the number of projectGalleries available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" projectGallery.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProjectGalleryDTO> findOne(Long id);

    /**
     * Delete the "id" projectGallery.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
