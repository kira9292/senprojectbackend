package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.ProjectGallery;
import com.senprojectbackend1.domain.criteria.ProjectGalleryCriteria;
import com.senprojectbackend1.repository.ProjectGalleryRepository;
import com.senprojectbackend1.security.AuthoritiesConstants;
import com.senprojectbackend1.service.ProjectGalleryService;
import com.senprojectbackend1.service.dto.ProjectGalleryDTO;
import com.senprojectbackend1.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.senprojectbackend1.domain.ProjectGallery}.
 */
@RestController
@RequestMapping("/api/project-galleries")
public class ProjectGalleryResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectGalleryResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1ProjectGallery";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectGalleryService projectGalleryService;

    private final ProjectGalleryRepository projectGalleryRepository;

    public ProjectGalleryResource(ProjectGalleryService projectGalleryService, ProjectGalleryRepository projectGalleryRepository) {
        this.projectGalleryService = projectGalleryService;
        this.projectGalleryRepository = projectGalleryRepository;
    }

    /**
     * {@code POST  /project-galleries} : Create a new projectGallery.
     *
     * @param projectGalleryDTO the projectGalleryDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectGalleryDTO, or with status {@code 400 (Bad Request)} if the projectGallery has already an ID.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PostMapping("")
    public Mono<ResponseEntity<ProjectGalleryDTO>> createProjectGallery(@Valid @RequestBody ProjectGalleryDTO projectGalleryDTO) {
        LOG.debug("REST request to save ProjectGallery : {}", projectGalleryDTO);
        if (projectGalleryDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectGallery cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectGalleryService
            .save(projectGalleryDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/project-galleries/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /project-galleries/:id} : Updates an existing projectGallery.
     *
     * @param id the id of the projectGalleryDTO to save.
     * @param projectGalleryDTO the projectGalleryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectGalleryDTO,
     * or with status {@code 400 (Bad Request)} if the projectGalleryDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectGalleryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProjectGalleryDTO>> updateProjectGallery(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectGalleryDTO projectGalleryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ProjectGallery : {}, {}", id, projectGalleryDTO);
        if (projectGalleryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectGalleryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectGalleryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projectGalleryService
                    .update(projectGalleryDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /project-galleries/:id} : Partial updates given fields of an existing projectGallery, field will ignore if it is null
     *
     * @param id the id of the projectGalleryDTO to save.
     * @param projectGalleryDTO the projectGalleryDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectGalleryDTO,
     * or with status {@code 400 (Bad Request)} if the projectGalleryDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectGalleryDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectGalleryDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectGalleryDTO>> partialUpdateProjectGallery(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectGalleryDTO projectGalleryDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ProjectGallery partially : {}, {}", id, projectGalleryDTO);
        if (projectGalleryDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectGalleryDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectGalleryRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectGalleryDTO> result = projectGalleryService.partialUpdate(projectGalleryDTO);

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
     * {@code GET  /project-galleries} : get all the projectGalleries.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectGalleries in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProjectGalleryDTO>>> getAllProjectGalleries(
        ProjectGalleryCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get ProjectGalleries by criteria: {}", criteria);
        return projectGalleryService
            .countByCriteria(criteria)
            .zipWith(projectGalleryService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /project-galleries/count} : count all the projectGalleries.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countProjectGalleries(ProjectGalleryCriteria criteria) {
        LOG.debug("REST request to count ProjectGalleries by criteria: {}", criteria);
        return projectGalleryService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /project-galleries/:id} : get the "id" projectGallery.
     *
     * @param id the id of the projectGalleryDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectGalleryDTO, or with status {@code 404 (Not Found)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProjectGalleryDTO>> getProjectGallery(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProjectGallery : {}", id);
        Mono<ProjectGalleryDTO> projectGalleryDTO = projectGalleryService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectGalleryDTO);
    }

    /**
     * {@code DELETE  /project-galleries/:id} : delete the "id" projectGallery.
     *
     * @param id the id of the projectGalleryDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProjectGallery(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProjectGallery : {}", id);
        return projectGalleryService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code GET /project/{id}} : Récupère toutes les images de la galerie d'un projet.
     *
     * @param id l'id du projet dont on veut récupérer la galerie
     * @return la liste des images de la galerie avec le statut {@code 200 (OK)}
     */
    @GetMapping("/project/{id}")
    public Mono<ResponseEntity<List<ProjectGallery>>> getProjectGalleryByProjectId(@PathVariable Long id) {
        LOG.debug("REST request to get gallery for Project : {}", id);
        return projectGalleryRepository
            .findByProject(id)
            .collectList()
            .map(gallery -> ResponseEntity.ok().body(gallery))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
