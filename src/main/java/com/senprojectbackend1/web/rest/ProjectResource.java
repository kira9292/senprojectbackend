package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.security.AuthoritiesConstants;
import com.senprojectbackend1.security.SecurityUtils;
import com.senprojectbackend1.service.ProjectService;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSimpleDTO;
import com.senprojectbackend1.service.dto.ProjectSubmissionDTO;
import com.senprojectbackend1.service.exception.ProjectBusinessException;
import com.senprojectbackend1.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.senprojectbackend1.domain.Project}.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1Project";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;

    public ProjectResource(ProjectService projectService, ProjectRepository projectRepository) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
    }

    /**
     * {@code POST  /projects} : Create a new project.
     *
     * @param projectDTO the projectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectDTO, or with status {@code 400 (Bad Request)} if the project has already an ID.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PostMapping("")
    public Mono<ResponseEntity<ProjectDTO>> createProject(@Valid @RequestBody ProjectDTO projectDTO) {
        LOG.debug("REST request to save Project : {}", projectDTO);
        if (projectDTO.getId() != null) {
            throw new BadRequestAlertException("A new project cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectService
            .save(projectDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/projects/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /projects/:id} : Updates an existing project.
     *
     * @param id the id of the projectDTO to save.
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDTO,
     * or with status {@code 400 (Bad Request)} if the projectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectDTO couldn't be updated.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<Void>> updateProject(@PathVariable Long id, @RequestBody ProjectDTO projectDTO) {
        LOG.debug("REST request to update Project : {}", projectDTO);
        return projectService.updateProject(projectDTO).map(project -> ResponseEntity.ok().build());
    }

    /**
     * {@code PATCH  /projects/:id} : Partial updates given fields of an existing project, field will ignore if it is null
     *
     * @param id the id of the projectDTO to save.
     * @param projectDTO the projectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectDTO,
     * or with status {@code 400 (Bad Request)} if the projectDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectDTO couldn't be updated.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectDTO>> partialUpdateProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectDTO projectDTO
    ) {
        LOG.debug("REST request to partial update Project partially : {}, {}", id, projectDTO);
        if (projectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (Boolean.FALSE.equals(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectDTO> result = projectService.partialUpdate(projectDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projects in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProjectDTO>>> getAllProjects(
        ProjectCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Projects by criteria: {}", criteria);
        return projectService
            .countByCriteria(criteria)
            .zipWith(projectService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /projects} : get all the projects.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projects in body.
     */
    @GetMapping(value = "/me", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProjectSimpleDTO>>> getAllSimpleProjectsOfCurrentUser(
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Simple Projects of the current user");
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login -> {
                Flux<ProjectSimpleDTO> projectsFlux = projectService.findAllProjectsOfCurrentUser(login);

                return projectsFlux
                    .collectList()
                    .map(allProjects -> {
                        int start = (int) pageable.getOffset();
                        int end = Math.min((start + pageable.getPageSize()), allProjects.size());
                        List<ProjectSimpleDTO> pageContent = allProjects.subList(start, end);

                        return ResponseEntity.ok()
                            .headers(
                                PaginationUtil.generatePaginationHttpHeaders(
                                    ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                                    new PageImpl<>(pageContent, pageable, allProjects.size())
                                )
                            )
                            .body(pageContent);
                    });
            })
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /projects/count} : count all the projects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countProjects(ProjectCriteria criteria) {
        LOG.debug("REST request to count Projects by criteria: {}", criteria);
        return projectService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /projects/:id} : get the "id" project.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProjectDTO>> getProject(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Project : {}", id);
        Mono<ProjectDTO> projectDTO = projectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectDTO);
    }

    /**
     * {@code GET  /projects/:id} : get the "id" project with eagerRelationship.
     *
     * @param id the id of the projectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDTO, or with status {@code 404 (Not Found)}.
     */

    @GetMapping("/project/{id}")
    public Mono<ResponseEntity<ProjectSimpleDTO>> getProjectSimple(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Project Simple: {}", id);
        Mono<ProjectSimpleDTO> projectDTO = projectService.findOneSimple(id);
        return ResponseUtil.wrapOrNotFound(projectDTO);
    }

    /**
     * {@code DELETE  /projects/:id} : delete the "id" project.
     *
     * @param id the id of the projectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProject(@PathVariable Long id) {
        LOG.debug("REST request to delete Project : {}", id);
        return projectService.deleteProject(id).then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }

    /**
     * {@code POST /projects/{id}/approve} : Approve a project
     *
     * @param id the id of the project to approve
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     */
    @PostMapping("/{id}/approve")
    public Mono<ResponseEntity<Void>> approveProject(@PathVariable Long id) {
        LOG.debug("REST request to approve Project : {}", id);
        return projectService.approveProject(id).map(project -> ResponseEntity.ok().build());
    }

    /**
     * {@code POST /projects/{id}/reject} : Reject a project
     *
     * @param id the id of the project to reject
     * @param reason the reason for rejection
     * @return the {@link ResponseEntity} with status {@code 200 (OK)}
     */
    @PostMapping("/{id}/reject")
    public Mono<ResponseEntity<Void>> rejectProject(@PathVariable Long id, @RequestParam(required = false) String reason) {
        LOG.debug("REST request to reject Project : {}", id);
        return projectService.rejectProject(id).map(project -> ResponseEntity.ok().build());
    }

    /**
     * {@code GET  /projects/:id/with-sections} : get the "id" project with its sections.
     *
     * @param id the id of the project to retrieve with its sections.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectDTO with sections, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}/with-sections")
    public Mono<ResponseEntity<ProjectDTO>> getProjectWithSections(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Project with sections : {}", id);

        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login -> projectService.findOneWithSectionsAndIncrementViews(id, login).map(ResponseEntity::ok))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code POST  /projects/favorite/{id}} : Toggle le statut favori d'un projet.
     *
     * @param id l'ID du projet
     * @return le {@link ResponseEntity} avec status {@code 200 (OK)} et true si le projet est maintenant en favori, false sinon
     */
    @PostMapping("/favorite/{id}")
    public Mono<ResponseEntity<Boolean>> toggleFavorite(@PathVariable Long id) {
        LOG.debug("REST request to toggle favorite status for Project : {}", id);
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login -> projectService.toggleFavorite(id, login))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /projects/favorite/{id}} : Vérifie si un projet est en favori pour l'utilisateur courant.
     *
     * @param id l'ID du projet
     * @return le {@link ResponseEntity} avec status {@code 200 (OK)} et true si le projet est en favori, false sinon
     */
    @GetMapping("/favorite/{id}")
    public Mono<ResponseEntity<Boolean>> isFavorite(@PathVariable Long id) {
        LOG.debug("REST request to check if Project is favorite : {}", id);
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login -> projectService.isFavorite(id, login))
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /projects/{id}/delete} : Marque un projet comme supprimé si l'utilisateur est un membre accepté de l'équipe.
     *
     * @param id l'ID du projet à marquer comme supprimé
     * @return le {@link ResponseEntity} avec status {@code 200 (OK)}
     */
    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<Void>> markProjectAsDeleted(@PathVariable Long id) {
        LOG.debug("REST request to mark Project as deleted : {}", id);
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login -> projectService.markProjectAsDeleted(id, login))
            .then(Mono.fromCallable(() -> ResponseEntity.ok().build()));
    }

    /**
     * PATCH /projects/{id}/status : Change le statut d'un projet (règles : membre accepté, rôle LEAD/MODIFY, seul admin/support pour PUBLISHED)
     */
    @PatchMapping("/{id}/status")
    public Mono<ResponseEntity<ProjectDTO>> changeProjectStatus(@PathVariable Long id, @RequestParam String newStatus) {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName)
            .flatMap(login -> projectService.changeProjectStatus(id, newStatus, login))
            .map(ResponseEntity::ok);
    }

    /**
     * {@code POST  /projects/submit} : Soumettre un projet (création ou modification).
     *
     * @param projectSubmissionDTO le projet à soumettre.
     * @return le {@link ResponseEntity} avec le projet créé ou modifié.
     */
    @PostMapping("/submit")
    public Mono<ResponseEntity<ProjectDTO>> submitProject(@Valid @RequestBody ProjectSubmissionDTO projectSubmissionDTO) {
        LOG.debug("[DEBUG] Début de la méthode submitProject");
        // Valider les données d'entrée ici au lieu du service
        if (projectSubmissionDTO.getTitle() == null || projectSubmissionDTO.getTitle().trim().length() < 3) {
            return Mono.error(
                new BadRequestAlertException("Le titre du projet doit contenir au moins 3 caractères", ENTITY_NAME, "titleinvalid")
            );
        }
        if (projectSubmissionDTO.getTeamId() == null) {
            return Mono.error(new BadRequestAlertException("Une équipe valide doit être renseignée pour le projet", ENTITY_NAME, "noteam"));
        }
        // Interdire la création directe en PUBLISHED sauf pour ADMIN/SUPPORT
        if (projectSubmissionDTO.getStatus() != null && "PUBLISHED".equalsIgnoreCase(projectSubmissionDTO.getStatus().toString())) {
            return SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN", "ROLE_SUPPORT").flatMap(isAdminOrSupport -> {
                if (Boolean.FALSE.equals(isAdminOrSupport)) {
                    return Mono.error(
                        new BadRequestAlertException(
                            "Seuls les admins/support peuvent publier un projet directement",
                            ENTITY_NAME,
                            "forbidden"
                        )
                    );
                }
                // On laisse continuer la soumission (status PUBLISHED autorisé si ADMIN/SUPPORT)
                return SecurityUtils.getCurrentUserLogin()
                    .switchIfEmpty(
                        Mono.error(new BadRequestAlertException("[ERREUR 1] Utilisateur courant non trouvé", ENTITY_NAME, "usernotfound"))
                    )
                    .flatMap(currentUserLogin ->
                        projectService
                            .processAllImages(projectSubmissionDTO, currentUserLogin)
                            .flatMap(finalProjectData -> projectService.submitProject(finalProjectData, currentUserLogin))
                            .handle((result, sink) -> {
                                try {
                                    sink.next(ResponseEntity.created(new URI("/api/projects/" + result.getId())).body(result));
                                } catch (URISyntaxException e) {
                                    sink.error(new RuntimeException(e));
                                }
                            })
                    );
            });
        }

        // Cas normal de soumission (status != PUBLISHED ou utilisateur non ADMIN/SUPPORT)
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(
                Mono.error(new BadRequestAlertException("[ERREUR 1] Utilisateur courant non trouvé", ENTITY_NAME, "usernotfound"))
            )
            .flatMap(currentUserLogin ->
                projectService
                    .processAllImages(projectSubmissionDTO, currentUserLogin)
                    .flatMap(finalProjectData -> projectService.submitProject(finalProjectData, currentUserLogin))
                    .handle((result, sink) -> {
                        try {
                            sink.next(ResponseEntity.created(new URI("/api/projects/" + result.getId())).body(result));
                        } catch (URISyntaxException e) {
                            sink.error(new RuntimeException(e));
                        }
                    })
            );
    }

    /**
     * {@code POST  /projects/submit-update} : Met à jour un projet (logique soumission, avec vérification des droits).
     *
     * @param projectSubmissionDTO le projet à mettre à jour.
     * @return le {@link ResponseEntity} avec le projet mis à jour.
     */
    @PostMapping("/submit-update")
    public Mono<ResponseEntity<ProjectDTO>> updateSubmittedProject(@Valid @RequestBody ProjectSubmissionDTO projectSubmissionDTO) {
        LOG.debug("[DEBUG] Début de la méthode updateSubmittedProject");
        // Valider les données d'entrée ici
        if (projectSubmissionDTO.getId() == null) {
            return Mono.error(new BadRequestAlertException("ID du projet manquant pour la mise à jour", ENTITY_NAME, "idmissing"));
        }
        if (projectSubmissionDTO.getTitle() == null || projectSubmissionDTO.getTitle().trim().length() < 3) {
            return Mono.error(
                new BadRequestAlertException("Le titre du projet doit contenir au moins 3 caractères", ENTITY_NAME, "titleinvalid")
            );
        }
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(
                Mono.error(new BadRequestAlertException("[ERREUR 1] Utilisateur courant non trouvé", ENTITY_NAME, "usernotfound"))
            )
            .flatMap(currentUserLogin ->
                projectService
                    .processAllImages(projectSubmissionDTO, currentUserLogin)
                    .flatMap(finalProjectData -> projectService.updateSubmittedProject(finalProjectData, currentUserLogin))
                    .map(ResponseEntity::ok)
            );
    }

    /**
     * {@code GET /projects/paginated} : Récupère une page de projets.
     *
     * @param pageable the pagination information.
     * @param categories liste des catégories (tags) à filtrer (optionnel, union)
     * @return the response with status {@code 200 (OK)} and the list of projects in body
     */
    @GetMapping("/paginated")
    public Mono<ResponseEntity<Flux<ProjectDTO>>> getPaginatedProjects(
        @ParameterObject Pageable pageable,
        @RequestParam(value = "category", required = false) List<String> categories
    ) {
        LOG.debug("REST request to get paginated Projects - pageable: {}, categories: {}", pageable, categories);
        Mono<Long> totalMono;
        Flux<ProjectDTO> flux;

        ProjectCriteria criteria = new ProjectCriteria();
        criteria.setStatus(
            (ProjectCriteria.ProjectStatusFilter) new ProjectCriteria.ProjectStatusFilter().setEquals(ProjectStatus.PUBLISHED)
        );

        if (categories == null || categories.isEmpty()) {
            totalMono = projectService.countByCriteria(criteria);
            flux = projectService.findByCriteria(criteria, pageable);
        } else {
            totalMono = projectService.countProjectsByCategories(categories);
            flux = projectService.getPaginatedProjects(pageable, categories); // This service method still needs the filtering logic inside for the category case
        }

        return totalMono.map(total -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", String.valueOf(total));
            return ResponseEntity.ok().headers(headers).body(flux);
        });
    }

    /**
     * {@code GET /projects/popular} : Récupère les projets les plus populaires (paginé).
     *
     * @param pageable the pagination information.
     * @return the response with status {@code 200 (OK)} and the list of popular projects in body
     */
    @GetMapping("/popular")
    public Mono<ResponseEntity<Flux<ProjectDTO>>> getTopPopularProjects(@ParameterObject Pageable pageable, ServerHttpRequest request) {
        LOG.debug("REST request to get top popular projects - pageable: {}", pageable);
        Mono<Long> totalMono = projectService.countPopularProjects();
        Flux<ProjectDTO> flux = projectService.getTopPopularProjects(pageable);
        return totalMono.map(total -> {
            HttpHeaders headers = new HttpHeaders();
            headers.add("X-Total-Count", String.valueOf(total));
            return ResponseEntity.ok().headers(headers).body(flux);
        });
    }

    /**
     * {@code GET /projects/check-title} : Vérifie si un titre de projet existe déjà.
     *
     * @param title le titre à vérifier
     * @param edit l'ID du projet en cours d'édition (optionnel)
     * @return {@code true} si le titre existe déjà, {@code false} sinon
     */
    @GetMapping("/check-title")
    public Mono<ResponseEntity<Boolean>> checkProjectTitle(@RequestParam String title, @RequestParam(required = false) Long edit) {
        LOG.debug("REST request to check if Project title exists : {}, edit: {}", title, edit);
        return projectService
            .checkProjectTitleExists(title, edit)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                if (e instanceof ProjectBusinessException) {
                    return Mono.just(ResponseEntity.badRequest().body(false));
                }
                return Mono.just(ResponseEntity.internalServerError().body(false));
            });
    }
}
