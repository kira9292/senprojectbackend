package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.criteria.EngagementTeamCriteria;
import com.senprojectbackend1.repository.EngagementTeamRepository;
import com.senprojectbackend1.service.EngagementTeamService;
import com.senprojectbackend1.service.dto.EngagementTeamDTO;
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
 * REST controller for managing {@link com.senprojectbackend1.domain.EngagementTeam}.
 */
@RestController
@RequestMapping("/api/engagement-teams")
public class EngagementTeamResource {

    private static final Logger LOG = LoggerFactory.getLogger(EngagementTeamResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1EngagementTeam";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final EngagementTeamService engagementTeamService;

    private final EngagementTeamRepository engagementTeamRepository;

    public EngagementTeamResource(EngagementTeamService engagementTeamService, EngagementTeamRepository engagementTeamRepository) {
        this.engagementTeamService = engagementTeamService;
        this.engagementTeamRepository = engagementTeamRepository;
    }

    /**
     * {@code POST  /engagement-teams} : Create a new engagementTeam.
     *
     * @param engagementTeamDTO the engagementTeamDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new engagementTeamDTO, or with status {@code 400 (Bad Request)} if the engagementTeam has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<EngagementTeamDTO>> createEngagementTeam(@Valid @RequestBody EngagementTeamDTO engagementTeamDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save EngagementTeam : {}", engagementTeamDTO);
        if (engagementTeamDTO.getId() != null) {
            throw new BadRequestAlertException("A new engagementTeam cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return engagementTeamService
            .save(engagementTeamDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/engagement-teams/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /engagement-teams/:id} : Updates an existing engagementTeam.
     *
     * @param id the id of the engagementTeamDTO to save.
     * @param engagementTeamDTO the engagementTeamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engagementTeamDTO,
     * or with status {@code 400 (Bad Request)} if the engagementTeamDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the engagementTeamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<EngagementTeamDTO>> updateEngagementTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody EngagementTeamDTO engagementTeamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update EngagementTeam : {}, {}", id, engagementTeamDTO);
        if (engagementTeamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engagementTeamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return engagementTeamRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return engagementTeamService
                    .update(engagementTeamDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /engagement-teams/:id} : Partial updates given fields of an existing engagementTeam, field will ignore if it is null
     *
     * @param id the id of the engagementTeamDTO to save.
     * @param engagementTeamDTO the engagementTeamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated engagementTeamDTO,
     * or with status {@code 400 (Bad Request)} if the engagementTeamDTO is not valid,
     * or with status {@code 404 (Not Found)} if the engagementTeamDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the engagementTeamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<EngagementTeamDTO>> partialUpdateEngagementTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody EngagementTeamDTO engagementTeamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update EngagementTeam partially : {}, {}", id, engagementTeamDTO);
        if (engagementTeamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, engagementTeamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return engagementTeamRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<EngagementTeamDTO> result = engagementTeamService.partialUpdate(engagementTeamDTO);

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
     * {@code GET  /engagement-teams} : get all the engagementTeams.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of engagementTeams in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<EngagementTeamDTO>>> getAllEngagementTeams(
        EngagementTeamCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get EngagementTeams by criteria: {}", criteria);
        return engagementTeamService
            .countByCriteria(criteria)
            .zipWith(engagementTeamService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /engagement-teams/count} : count all the engagementTeams.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countEngagementTeams(EngagementTeamCriteria criteria) {
        LOG.debug("REST request to count EngagementTeams by criteria: {}", criteria);
        return engagementTeamService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /engagement-teams/:id} : get the "id" engagementTeam.
     *
     * @param id the id of the engagementTeamDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the engagementTeamDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<EngagementTeamDTO>> getEngagementTeam(@PathVariable("id") Long id) {
        LOG.debug("REST request to get EngagementTeam : {}", id);
        Mono<EngagementTeamDTO> engagementTeamDTO = engagementTeamService.findOne(id);
        return ResponseUtil.wrapOrNotFound(engagementTeamDTO);
    }

    /**
     * {@code DELETE  /engagement-teams/:id} : delete the "id" engagementTeam.
     *
     * @param id the id of the engagementTeamDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteEngagementTeam(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete EngagementTeam : {}", id);
        return engagementTeamService
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
