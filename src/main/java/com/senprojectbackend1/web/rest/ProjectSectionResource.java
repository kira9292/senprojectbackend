package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.criteria.ProjectSectionCriteria;
import com.senprojectbackend1.repository.ProjectSectionRepository;
import com.senprojectbackend1.security.AuthoritiesConstants;
import com.senprojectbackend1.service.ProjectSectionService;
import com.senprojectbackend1.service.dto.ProjectSectionDTO;
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
 * REST controller for managing {@link com.senprojectbackend1.domain.ProjectSection}.
 */
@RestController
@RequestMapping("/api/project-sections")
public class ProjectSectionResource {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectSectionResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1ProjectSection";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectSectionService projectSectionService;

    private final ProjectSectionRepository projectSectionRepository;

    public ProjectSectionResource(ProjectSectionService projectSectionService, ProjectSectionRepository projectSectionRepository) {
        this.projectSectionService = projectSectionService;
        this.projectSectionRepository = projectSectionRepository;
    }

    /**
     * {@code POST  /project-sections} : Create a new projectSection.
     *
     * @param projectSectionDTO the projectSectionDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectSectionDTO, or with status {@code 400 (Bad Request)} if the projectSection has already an ID.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PostMapping("")
    public Mono<ResponseEntity<ProjectSectionDTO>> createProjectSection(@Valid @RequestBody ProjectSectionDTO projectSectionDTO) {
        LOG.debug("REST request to save ProjectSection : {}", projectSectionDTO);
        if (projectSectionDTO.getId() != null) {
            throw new BadRequestAlertException("A new projectSection cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return projectSectionService
            .save(projectSectionDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/project-sections/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /project-sections/:id} : Updates an existing projectSection.
     *
     * @param id the id of the projectSectionDTO to save.
     * @param projectSectionDTO the projectSectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSectionDTO,
     * or with status {@code 400 (Bad Request)} if the projectSectionDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the projectSectionDTO couldn't be updated.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ProjectSectionDTO>> updateProjectSection(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ProjectSectionDTO projectSectionDTO
    ) {
        LOG.debug("REST request to update ProjectSection : {}, {}", id, projectSectionDTO);
        if (projectSectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectSectionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (Boolean.FALSE.equals(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return projectSectionService
                    .update(projectSectionDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /project-sections/:id} : Partial updates given fields of an existing projectSection, field will ignore if it is null
     *
     * @param id the id of the projectSectionDTO to save.
     * @param projectSectionDTO the projectSectionDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated projectSectionDTO,
     * or with status {@code 400 (Bad Request)} if the projectSectionDTO is not valid,
     * or with status {@code 404 (Not Found)} if the projectSectionDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the projectSectionDTO couldn't be updated.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ProjectSectionDTO>> partialUpdateProjectSection(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ProjectSectionDTO projectSectionDTO
    ) {
        LOG.debug("REST request to partial update ProjectSection partially : {}, {}", id, projectSectionDTO);
        if (projectSectionDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, projectSectionDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return projectSectionRepository
            .existsById(id)
            .flatMap(exists -> {
                if (Boolean.FALSE.equals(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ProjectSectionDTO> result = projectSectionService.partialUpdate(projectSectionDTO);

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
     * {@code GET  /project-sections} : get all the projectSections.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of projectSections in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ProjectSectionDTO>>> getAllProjectSections(
        ProjectSectionCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get ProjectSections by criteria: {}", criteria);
        return projectSectionService
            .countByCriteria(criteria)
            .zipWith(projectSectionService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /project-sections/count} : count all the projectSections.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countProjectSections(ProjectSectionCriteria criteria) {
        LOG.debug("REST request to count ProjectSections by criteria: {}", criteria);
        return projectSectionService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /project-sections/:id} : get the "id" projectSection.
     *
     * @param id the id of the projectSectionDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the projectSectionDTO, or with status {@code 404 (Not Found)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ProjectSectionDTO>> getProjectSection(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ProjectSection : {}", id);
        Mono<ProjectSectionDTO> projectSectionDTO = projectSectionService.findOne(id);
        return ResponseUtil.wrapOrNotFound(projectSectionDTO);
    }

    /**
     * {@code DELETE  /project-sections/:id} : delete the "id" projectSection.
     *
     * @param id the id of the projectSectionDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteProjectSection(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ProjectSection : {}", id);
        return projectSectionService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }
}
