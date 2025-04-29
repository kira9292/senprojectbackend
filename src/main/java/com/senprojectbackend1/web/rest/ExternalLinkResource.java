package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.criteria.ExternalLinkCriteria;
import com.senprojectbackend1.repository.ExternalLinkRepository;
import com.senprojectbackend1.service.ExternalLinkService;
import com.senprojectbackend1.service.dto.ExternalLinkDTO;
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
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.senprojectbackend1.domain.ExternalLink}.
 */
@RestController
@RequestMapping("/api/external-links")
public class ExternalLinkResource {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalLinkResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1ExternalLink";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ExternalLinkService externalLinkService;

    private final ExternalLinkRepository externalLinkRepository;

    public ExternalLinkResource(ExternalLinkService externalLinkService, ExternalLinkRepository externalLinkRepository) {
        this.externalLinkService = externalLinkService;
        this.externalLinkRepository = externalLinkRepository;
    }

    /**
     * {@code POST  /external-links} : Create a new externalLink.
     *
     * @param externalLinkDTO the externalLinkDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new externalLinkDTO, or with status {@code 400 (Bad Request)} if the externalLink has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<ExternalLinkDTO>> createExternalLink(@Valid @RequestBody ExternalLinkDTO externalLinkDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save ExternalLink : {}", externalLinkDTO);
        if (externalLinkDTO.getId() != null) {
            throw new BadRequestAlertException("A new externalLink cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return externalLinkService
            .save(externalLinkDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/external-links/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /external-links/:id} : Updates an existing externalLink.
     *
     * @param id the id of the externalLinkDTO to save.
     * @param externalLinkDTO the externalLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated externalLinkDTO,
     * or with status {@code 400 (Bad Request)} if the externalLinkDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the externalLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<ExternalLinkDTO>> updateExternalLink(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody ExternalLinkDTO externalLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update ExternalLink : {}, {}", id, externalLinkDTO);
        if (externalLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, externalLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return externalLinkRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return externalLinkService
                    .update(externalLinkDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /external-links/:id} : Partial updates given fields of an existing externalLink, field will ignore if it is null
     *
     * @param id the id of the externalLinkDTO to save.
     * @param externalLinkDTO the externalLinkDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated externalLinkDTO,
     * or with status {@code 400 (Bad Request)} if the externalLinkDTO is not valid,
     * or with status {@code 404 (Not Found)} if the externalLinkDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the externalLinkDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<ExternalLinkDTO>> partialUpdateExternalLink(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody ExternalLinkDTO externalLinkDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update ExternalLink partially : {}, {}", id, externalLinkDTO);
        if (externalLinkDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, externalLinkDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return externalLinkRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<ExternalLinkDTO> result = externalLinkService.partialUpdate(externalLinkDTO);

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
     * {@code GET  /external-links} : get all the externalLinks.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of externalLinks in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<ExternalLinkDTO>>> getAllExternalLinks(
        ExternalLinkCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get ExternalLinks by criteria: {}", criteria);
        return externalLinkService
            .countByCriteria(criteria)
            .zipWith(externalLinkService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /external-links/count} : count all the externalLinks.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countExternalLinks(ExternalLinkCriteria criteria) {
        LOG.debug("REST request to count ExternalLinks by criteria: {}", criteria);
        return externalLinkService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /external-links/:id} : get the "id" externalLink.
     *
     * @param id the id of the externalLinkDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the externalLinkDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<ExternalLinkDTO>> getExternalLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to get ExternalLink : {}", id);
        Mono<ExternalLinkDTO> externalLinkDTO = externalLinkService.findOne(id);
        return ResponseUtil.wrapOrNotFound(externalLinkDTO);
    }

    /**
     * {@code DELETE  /external-links/:id} : delete the "id" externalLink.
     *
     * @param id the id of the externalLinkDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteExternalLink(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete ExternalLink : {}", id);
        return externalLinkService
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
     * {@code GET  /external-links/project/:id} : get all the externalLinks for a project.
     *
     * @param id the id of the project.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of externalLinks in body.
     */
    @GetMapping("/project/{id}")
    public Mono<ResponseEntity<List<ExternalLinkDTO>>> getCommentsByProjectId(@PathVariable Long id) {
        LOG.debug("REST request to get Comments for Project : {}", id);
        return externalLinkService.findByProject(id).collectList().map(comments -> ResponseEntity.ok().body(comments));
    }
}
