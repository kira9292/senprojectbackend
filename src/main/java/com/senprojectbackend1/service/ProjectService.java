package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.service.dto.PageDTO;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSimpleDTO;
import com.senprojectbackend1.service.dto.ProjectSubmissionDTO;
import org.springframework.data.domain.Page;
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
    public Mono<Long> countByCriteria(ProjectCriteria criteria);

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
     * Récupère une page de projets.
     *
     * @param page numéro de la page (commence à 0)
     * @param size taille de la page (nombre d'éléments par page)
     * @return un Mono contenant une page de ProjectDTO
     */
    Mono<PageDTO<ProjectDTO>> getPaginatedProjects(int page, int size);

    /**
     * Get the top 10 most popular projects based on views, likes and favorites.
     *
     * @return a Flux of the most popular projects
     */
    Flux<ProjectDTO> getTop10PopularProjects();
}
