package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.criteria.TagCriteria;
import com.senprojectbackend1.repository.TagRepository;
import com.senprojectbackend1.security.AuthoritiesConstants;
import com.senprojectbackend1.service.TagService;
import com.senprojectbackend1.service.dto.PageDTO;
import com.senprojectbackend1.service.dto.TagDTO;
import com.senprojectbackend1.service.dto.TagWithCountDTO;
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
 * REST controller for managing {@link com.senprojectbackend1.domain.Tag}.
 */
@RestController
@RequestMapping("/api")
public class TagResource {

    private static final Logger LOG = LoggerFactory.getLogger(TagResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1Tag";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TagService tagService;

    private final TagRepository tagRepository;

    public TagResource(TagService tagService, TagRepository tagRepository) {
        this.tagService = tagService;
        this.tagRepository = tagRepository;
    }

    /**
     * {@code POST  /tags} : Create a new tag.
     *
     * @param tagDTO the tagDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new tagDTO, or with status {@code 400 (Bad Request)} if the tag has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PostMapping("/tags")
    public Mono<ResponseEntity<TagDTO>> createTag(@Valid @RequestBody TagDTO tagDTO) throws URISyntaxException {
        LOG.debug("REST request to save Tag : {}", tagDTO);
        if (tagDTO.getId() != null) {
            throw new BadRequestAlertException("A new tag cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return tagService
            .save(tagDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/tags/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /tags/:id} : Updates an existing tag.
     *
     * @param id the id of the tagDTO to save.
     * @param tagDTO the tagDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagDTO,
     * or with status {@code 400 (Bad Request)} if the tagDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the tagDTO couldn't be updated.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PutMapping("/tags/{id}")
    public Mono<ResponseEntity<TagDTO>> updateTag(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TagDTO tagDTO
    ) {
        LOG.debug("REST request to update Tag : {}, {}", id, tagDTO);
        if (tagDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tagRepository
            .existsById(id)
            .flatMap(exists -> {
                if (Boolean.FALSE.equals(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return tagService
                    .update(tagDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /tags/:id} : Partial updates given fields of an existing tag, field will ignore if it is null
     *
     * @param id the id of the tagDTO to save.
     * @param tagDTO the tagDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated tagDTO,
     * or with status {@code 400 (Bad Request)} if the tagDTO is not valid,
     * or with status {@code 404 (Not Found)} if the tagDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the tagDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @PatchMapping(value = "/tags/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TagDTO>> partialUpdateTag(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TagDTO tagDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Tag partially : {}, {}", id, tagDTO);
        if (tagDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, tagDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return tagRepository
            .existsById(id)
            .flatMap(exists -> {
                if (Boolean.FALSE.equals(exists)) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TagDTO> result = tagService.partialUpdate(tagDTO);

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
     * {@code GET  /tags} : get all the tags.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping(value = "/tags", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TagDTO>>> getAllTags(
        TagCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Tags by criteria: {}", criteria);
        return tagService
            .countByCriteria(criteria)
            .zipWith(tagService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /tags/count} : count all the tags.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/tags/count")
    public Mono<ResponseEntity<Long>> countTags(TagCriteria criteria) {
        LOG.debug("REST request to count Tags by criteria: {}", criteria);
        return tagService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /tags/:id} : get the "id" tag.
     *
     * @param id the id of the tagDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the tagDTO, or with status {@code 404 (Not Found)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/tags/{id}")
    public Mono<ResponseEntity<TagDTO>> getTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Tag : {}", id);
        Mono<TagDTO> tagDTO = tagService.findOne(id);
        return ResponseUtil.wrapOrNotFound(tagDTO);
    }

    /**
     * {@code DELETE  /tags/:id} : delete the "id" tag.
     *
     * @param id the id of the tagDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @DeleteMapping("/tags/{id}")
    public Mono<ResponseEntity<Void>> deleteTag(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Tag : {}", id);
        return tagService
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
     * {@code GET /tags/project/:id} : get all tags for a project.
     *
     * @param id the id of the project
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body
     */
    @Secured({ AuthoritiesConstants.ADMIN, AuthoritiesConstants.SUPPORT })
    @GetMapping("/tags/project/{id}")
    public Mono<ResponseEntity<List<Tag>>> getProjectTags(@PathVariable Long id) {
        LOG.debug("REST request to get tags for Project : {}", id);
        return tagRepository
            .findByProjectId(id)
            .collectList()
            .map(tags -> ResponseEntity.ok().body(tags))
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /tags/paginated} : get all tags with their project count.
     *
     * @param page the page number (0-based)
     * @param size the page size
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body.
     */
    @GetMapping("/tags/paginated")
    public Mono<ResponseEntity<PageDTO<TagWithCountDTO>>> getPaginatedTags(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        LOG.debug("REST request to get paginated Tags with count");
        return tagService.getPaginatedTags(page, size).map(ResponseEntity::ok);
    }

    /**
     * {@code GET  /tags/search} : search tags by name (only non-forbidden tags).
     *
     * @param name the name to search for
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of tags in body
     */
    @GetMapping("/tags/search")
    public Mono<ResponseEntity<List<TagDTO>>> searchTags(@RequestParam String name) {
        LOG.debug("REST request to search Tags by name: {}", name);
        return tagService.searchByName(name).collectList().map(ResponseEntity::ok);
    }
}
