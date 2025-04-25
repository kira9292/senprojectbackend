package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.CommentCriteria;
import com.senprojectbackend1.service.dto.CommentDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.Comment}.
 */
public interface CommentService {
    /**
     * Save a comment.
     *
     * @param commentDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<CommentDTO> save(CommentDTO commentDTO);

    /**
     * Updates a comment.
     *
     * @param commentDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<CommentDTO> update(CommentDTO commentDTO);

    /**
     * Partially updates a comment.
     *
     * @param commentDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<CommentDTO> partialUpdate(CommentDTO commentDTO);
    /**
     * Find comments by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<CommentDTO> findByCriteria(CommentCriteria criteria, Pageable pageable);

    /**
     * Find the count of comments by criteria.
     * @param criteria filtering criteria
     * @return the count of comments
     */
    public Mono<Long> countByCriteria(CommentCriteria criteria);

    /**
     * Returns the number of comments available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" comment.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<CommentDTO> findOne(Long id);

    /**
     * Delete the "id" comment.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Find all comments by project id.
     *
     * @param projectId the id of the project
     * @return the list of entities
     */
    Flux<CommentDTO> findByProjectId(Long projectId);

    Flux<CommentDTO> findByProject(Long id);

    /**
     * Create a new comment for a project.
     *
     * @param projectId l'ID du projet
     * @param content le contenu du commentaire
     * @param login le login de l'utilisateur
     * @return le commentaire créé
     */
    Mono<CommentDTO> createProjectComment(Long projectId, String content, String login);
}
