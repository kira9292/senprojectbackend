package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.criteria.EngagementProjectCriteria;
import com.senprojectbackend1.repository.EngagementProjectRepository;
import com.senprojectbackend1.security.AuthoritiesConstants;
import com.senprojectbackend1.service.EngagementProjectService;
import com.senprojectbackend1.service.dto.EngagementProjectDTO;
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
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.senprojectbackend1.domain.EngagementProject}.
 */
@RestController
@RequestMapping("/api/engagement-projects")
public class EngagementProjectResource {

    private static final Logger LOG = LoggerFactory.getLogger(EngagementProjectResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1EngagementProject";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EngagementProjectService engagementProjectService;
    private final EngagementProjectRepository engagementProjectRepository;

    public EngagementProjectResource(
        EngagementProjectService engagementProjectService,
        EngagementProjectRepository engagementProjectRepository
    ) {
        this.engagementProjectService = engagementProjectService;
        this.engagementProjectRepository = engagementProjectRepository;
    }

    /**
     * {@code POST  /engagement-projects} : Create a new engagementProject.
     *
     * @param engagementProjectDTO the engagementProjectDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new engagementProjectDTO, or with status {@code 400 (Bad Request)} if the engagementProject has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PostMapping("")
    public Mono<ResponseEntity<EngagementProjectDTO>> createEngagementProject(
        @Valid @RequestBody EngagementProjectDTO engagementProjectDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to save EngagementProject : {}", engagementProjectDTO);
        if (engagementProjectDTO.getId() != null) {
            throw new BadRequestAlertException("A new engagementProject cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return engagementProjectService
            .save(engagementProjectDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/engagement-projects/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /engagement-projects/:id} : Updates an existing engagementProject.
     *
     * @param id the id of the engagementProjectDTO to save.
     * @param engagementProjectDTO the engagementProjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engagementProjectDTO,
     * or with status {@code 400 (Bad Request)} if the engagementProjectDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the engagementProjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EngagementProjectDTO>> updateEngagementProject(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EngagementProjectDTO engagementProjectDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EngagementProject : {}, {}", id, engagementProjectDTO);
        if (engagementProjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engagementProjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return engagementProjectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return engagementProjectService
                    .update(engagementProjectDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /engagement-projects/:id} : Partial updates given fields of an existing engagementProject, field will ignore if it is null
     *
     * @param id the id of the engagementProjectDTO to save.
     * @param engagementProjectDTO the engagementProjectDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engagementProjectDTO,
     * or with status {@code 400 (Bad Request)} if the engagementProjectDTO is not valid,
     * or with status {@code 404 (Not Found)} if the engagementProjectDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the engagementProjectDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EngagementProjectDTO>> partialUpdateEngagementProject(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EngagementProjectDTO engagementProjectDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EngagementProject partially : {}, {}", id, engagementProjectDTO);
        if (engagementProjectDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engagementProjectDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return engagementProjectRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EngagementProjectDTO> result = engagementProjectService.partialUpdate(engagementProjectDTO);

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
     * {@code GET  /engagement-projects} : get all the engagementProjects.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of engagementProjects in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<EngagementProjectDTO>>> getAllEngagementProjects(
        EngagementProjectCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get EngagementProjects by criteria: {}", criteria);
        return engagementProjectService
            .countByCriteria(criteria)
            .zipWith(engagementProjectService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /engagement-projects/count} : count all the engagementProjects.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countEngagementProjects(EngagementProjectCriteria criteria) {
        LOG.debug("REST request to count EngagementProjects by criteria: {}", criteria);
        return engagementProjectService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /engagement-projects/:id} : get the "id" engagementProject.
     *
     * @param id the id of the engagementProjectDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the engagementProjectDTO, or with status {@code 404 (Not Found)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EngagementProjectDTO>> getEngagementProject(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EngagementProject : {}", id);
        Mono<EngagementProjectDTO> engagementProjectDTO = engagementProjectService.findOne(id);
        return ResponseUtil.wrapOrNotFound(engagementProjectDTO);
    }

    /**
     * {@code DELETE  /engagement-projects/:id} : delete the "id" engagementProject.
     *
     * @param id the id of the engagementProjectDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEngagementProject(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EngagementProject : {}", id);
        return engagementProjectService
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
     * {@code POST  /engagement-projects/project/{id}} : Create a new engagement (LIKE or SHARE) for a project.
     *
     * @param id the id of the project
     * @param type the type of engagement (LIKE or SHARE)
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} or {@code 200 (OK)} if engagement already exists
     */
    @PostMapping("/project/{id}")
    public Mono<ResponseEntity<EngagementProjectDTO>> createProjectEngagement(
        @PathVariable("id") Long id,
        @RequestParam("type") String type
    ) {
        LOG.debug("REST request to create Project engagement : projectId={}, type={}", id, type);

        if (!type.equals("LIKE") && !type.equals("SHARE")) {
            return Mono.error(new BadRequestAlertException("Type must be LIKE or SHARE", ENTITY_NAME, "invalidtype"));
        }

        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login ->
                engagementProjectService.createProjectEngagement(id, type, login).map(result -> ResponseEntity.ok().body(result))
            );
    }

    /**
     * {@code GET  /engagement-projects/project/{id}/status} : Récupère le statut d'engagement (like/share) de l'utilisateur courant pour un projet.
     *
     * @param id l'id du projet
     * @return le statut d'engagement sous la forme {like: true/false, share: true/false}
     */
    @GetMapping("/project/{id}/status")
    public Mono<ResponseEntity<com.senprojectbackend1.service.dto.EngagementStatusDTO>> getUserEngagementStatus(
        @PathVariable("id") Long id
    ) {
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login -> engagementProjectService.getUserEngagementStatus(id, login).map(status -> ResponseEntity.ok().body(status)));
    }
}
