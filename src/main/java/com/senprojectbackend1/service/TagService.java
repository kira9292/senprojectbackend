package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.TagCriteria;
import com.senprojectbackend1.service.dto.TagDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.Tag}.
 */
public interface TagService {
    /**
     * Save a tag.
     *
     * @param tagDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<TagDTO> save(TagDTO tagDTO);

    /**
     * Updates a tag.
     *
     * @param tagDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<TagDTO> update(TagDTO tagDTO);

    /**
     * Partially updates a tag.
     *
     * @param tagDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<TagDTO> partialUpdate(TagDTO tagDTO);
    /**
     * Find tags by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<TagDTO> findByCriteria(TagCriteria criteria, Pageable pageable);

    /**
     * Find the count of tags by criteria.
     * @param criteria filtering criteria
     * @return the count of tags
     */
    public Mono<Long> countByCriteria(TagCriteria criteria);

    /**
     * Returns the number of tags available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" tag.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<TagDTO> findOne(Long id);

    /**
     * Delete the "id" tag.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    @Transactional(readOnly = true)
    Flux<TagDTO> findByProjectId(Long projectId);
}
