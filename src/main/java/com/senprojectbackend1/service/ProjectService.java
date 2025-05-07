package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSimpleDTO;
import com.senprojectbackend1.service.dto.ProjectSubmissionDTO;
import java.util.List;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.Project}.
 */
public interface ProjectService {
    /**
     * Save a project.
     *
     * @param projectDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> save(ProjectDTO projectDTO);

    /**
     * Updates a project.
     *
     * @param projectDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> update(ProjectDTO projectDTO);

    /**
     * Partially updates a project.
     *
     * @param projectDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<ProjectDTO> partialUpdate(ProjectDTO projectDTO);
    /**
     * Find projects by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProjectDTO> findByCriteria(ProjectCriteria criteria, Pageable pageable);

    /**
     * Find the count of projects by criteria.
     * @param criteria filtering criteria
     * @return the count of projects
     */
    Mono<Long> countByCriteria(ProjectCriteria criteria);

    /**
     * Get all the projects with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<ProjectDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of projects available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProjectDTO> findOne(Long id);

    /**
     * Get the "id" project.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<ProjectSimpleDTO> findOneSimple(Long id);
    /**
     * Delete the "id" project.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    Flux<ProjectSimpleDTO> findAllProjectsOfCurrentUser(String userLogin);

    /**
     * Get a project by id.
     *
     * @param id the id of the project
     * @return the project
     */
    Mono<ProjectDTO> getProject(Long id);

    /**
     * Update a project.
     *
     * @param projectDTO the project to update
     * @return the updated project
     */
    Mono<ProjectDTO> updateProject(ProjectDTO projectDTO);

    /**
     * Delete a project.
     *
     * @param id the id of the project to delete
     * @return void
     */
    Mono<Void> deleteProject(Long id);

    /**
     * Approve a project.
     *
     * @param id the id of the project to approve
     * @return the approved project
     */
    Mono<ProjectDTO> approveProject(Long id);

    /**
     * Reject a project.
     *
     * @param id the id of the project to reject
     * @return the rejected project
     */
    Mono<ProjectDTO> rejectProject(Long id);

    /**
     * Get the "id" project with its sections.
     *
     * @param id the id of the entity.
     * @return the entity with its sections.
     */
    Mono<ProjectDTO> findOneWithSections(Long id);

    /**
     * Récupère un projet avec ses sections et gère l'incrémentation des vues.
     *
     * @param id l'ID du projet
     * @param login le login de l'utilisateur
     * @return le projet avec ses sections
     */
    Mono<ProjectDTO> findOneWithSectionsAndIncrementViews(Long id, String login);

    /**
     * Toggle le statut favori d'un projet pour l'utilisateur courant.
     * Si le projet n'est pas en favori, il sera ajouté.
     * Si le projet est déjà en favori, il sera retiré.
     *
     * @param id l'ID du projet
     * @param login le login de l'utilisateur
     * @return true si le projet est maintenant en favori, false sinon
     */
    Mono<Boolean> toggleFavorite(Long id, String login);

    /**
     * Vérifie si un projet est en favori pour l'utilisateur donné.
     *
     * @param id l'ID du projet
     * @param login le login de l'utilisateur
     * @return true si le projet est en favori, false sinon
     */
    Mono<Boolean> isFavorite(Long id, String login);

    Mono<Void> markProjectAsDeleted(Long id, String userLogin);

    Mono<ProjectDTO> changeProjectStatus(Long projectId, String newStatus, String userLogin);

    /**
     * Crée ou met à jour un projet à partir d'un ProjectSubmissionDTO (logique soumission unique).
     *
     * @param submissionDTO le DTO de soumission (création ou update)
     * @param userLogin le login de l'utilisateur courant
     * @return le projet créé ou mis à jour
     */
    Mono<ProjectDTO> submitProject(ProjectSubmissionDTO submissionDTO, String userLogin);

    /**
     * Met à jour un projet à partir d'un ProjectSubmissionDTO (logique soumission update).
     *
     * @param submissionDTO le DTO de soumission (update)
     * @param userLogin le login de l'utilisateur courant
     * @return le projet mis à jour
     */
    Mono<ProjectDTO> updateSubmittedProject(ProjectSubmissionDTO submissionDTO, String userLogin);

    /**
     * Récupère une page de projets.
     *
     * @param pageable la pagination Spring
     * @param categories liste des catégories (tags) à filtrer (optionnel, union)
     * @return un Flux contenant les ProjectDTO
     */
    Flux<ProjectDTO> getPaginatedProjects(Pageable pageable, List<String> categories);

    /**
     * Récupère les projets les plus populaires (paginé).
     *
     * @param pageable la pagination Spring
     * @return un Flux contenant les ProjectDTO populaires
     */
    Flux<ProjectDTO> getTopPopularProjects(Pageable pageable);

    // Ajout des méthodes utilitaires pour le traitement des images
    Mono<ProjectSubmissionDTO> processGalleryImages(ProjectSubmissionDTO projectData, String userLogin);
    /**
     * Processes and uploads the showcase image for a project submission.
     *
     * @param projectData the project submission data containing the showcase image.
     * @param userLogin the login of the user submitting the project.
     * @return a Mono containing the updated ProjectSubmissionDTO with the processed showcase image.
     */

    Mono<ProjectSubmissionDTO> processShowcaseImage(ProjectSubmissionDTO projectData, String userLogin);

    /**
     * Processes and uploads the section images for a project submission.
     *
     * @param projectData the project submission data containing the section images.
     * @param userLogin the login of the user submitting the project.
     * @return a Mono containing the updated ProjectSubmissionDTO with the processed section images.
     */
    Mono<ProjectSubmissionDTO> processSectionImages(ProjectSubmissionDTO projectData, String userLogin);
    Mono<ProjectSubmissionDTO> processAllImages(ProjectSubmissionDTO projectData, String userLogin);
    Mono<String> uploadBase64Image(String rawData, String prefix, String userLogin);

    /**
     * Retourne le nombre total de projets (tous).
     */
    Mono<Long> countAllProjects();

    /**
     * Retourne le nombre total de projets correspondant à au moins une des catégories.
     */
    Mono<Long> countProjectsByCategories(List<String> categories);

    /**
     * Retourne le nombre total de projets populaires (PUBLISHED et non supprimés).
     */
    Mono<Long> countPopularProjects();
}
