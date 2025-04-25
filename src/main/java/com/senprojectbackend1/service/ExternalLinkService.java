package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.ExternalLinkCriteria;
import com.senprojectbackend1.service.dto.ExternalLinkDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.ExternalLink}.
 */
public interface ExternalLinkService {
    /**
     * Save a externalLink.
     *
     * @param externalLinkDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ExternalLinkDTO> save(ExternalLinkDTO externalLinkDTO);

    /**
     * Updates a externalLink.
     *
     * @param externalLinkDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ExternalLinkDTO> update(ExternalLinkDTO externalLinkDTO);

    /**
     * Partially updates a externalLink.
     *
     * @param externalLinkDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ExternalLinkDTO> partialUpdate(ExternalLinkDTO externalLinkDTO);
    /**
     * Find externalLinks by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ExternalLinkDTO> findByCriteria(ExternalLinkCriteria criteria, Pageable pageable);

    /**
     * Find the count of externalLinks by criteria.
     * @param criteria filtering criteria
     * @return the count of externalLinks
     */
    public Mono<Long> countByCriteria(ExternalLinkCriteria criteria);

    /**
     * Returns the number of externalLinks available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" externalLink.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ExternalLinkDTO> findOne(Long id);

    /**
     * Delete the "id" externalLink.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);
}
