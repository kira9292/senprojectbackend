package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.ProjectGalleryAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.ProjectGallery;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.ProjectGalleryRepository;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.service.dto.ProjectGalleryDTO;
import com.senprojectbackend1.service.mapper.ProjectGalleryMapper;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;

/**
 * Integration tests for the {@link ProjectGalleryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectGalleryResourceIT {

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;
    private static final Integer SMALLER_ORDER = 1 - 1;

    private static final String ENTITY_API_URL = "/api/project-galleries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectGalleryRepository projectGalleryRepository;

    @Autowired
    private ProjectGalleryMapper projectGalleryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProjectGallery projectGallery;

    private ProjectGallery insertedProjectGallery;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectGallery createEntity(EntityManager em) {
        ProjectGallery projectGallery = new ProjectGallery()
            .imageUrl(DEFAULT_IMAGE_URL)
            .description(DEFAULT_DESCRIPTION)
            .order(DEFAULT_ORDER);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createEntity()).block();
        projectGallery.setProject(project);
        return projectGallery;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectGallery createUpdatedEntity(EntityManager em) {
        ProjectGallery updatedProjectGallery = new ProjectGallery()
            .imageUrl(UPDATED_IMAGE_URL)
            .description(UPDATED_DESCRIPTION)
            .order(UPDATED_ORDER);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createUpdatedEntity()).block();
        updatedProjectGallery.setProject(project);
        return updatedProjectGallery;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProjectGallery.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
        ProjectResourceIT.deleteEntities(em);
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        projectGallery = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProjectGallery != null) {
            projectGalleryRepository.delete(insertedProjectGallery).block();
            insertedProjectGallery = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProjectGallery() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProjectGallery
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);
        var returnedProjectGalleryDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProjectGalleryDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ProjectGallery in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProjectGallery = projectGalleryMapper.toEntity(returnedProjectGalleryDTO);
        assertProjectGalleryUpdatableFieldsEquals(returnedProjectGallery, getPersistedProjectGallery(returnedProjectGallery));

        insertedProjectGallery = returnedProjectGallery;
    }

    @Test
    void createProjectGalleryWithExistingId() throws Exception {
        // Create the ProjectGallery with an existing ID
        projectGallery.setId(1L);
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkImageUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectGallery.setImageUrl(null);

        // Create the ProjectGallery, which fails.
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectGallery.setOrder(null);

        // Create the ProjectGallery, which fails.
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProjectGalleries() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(projectGallery.getId().intValue()))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].order")
            .value(hasItem(DEFAULT_ORDER));
    }

    @Test
    void getProjectGallery() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get the projectGallery
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projectGallery.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projectGallery.getId().intValue()))
            .jsonPath("$.imageUrl")
            .value(is(DEFAULT_IMAGE_URL))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.order")
            .value(is(DEFAULT_ORDER));
    }

    @Test
    void getProjectGalleriesByIdFiltering() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        Long id = projectGallery.getId();

        defaultProjectGalleryFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProjectGalleryFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProjectGalleryFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllProjectGalleriesByImageUrlIsEqualToSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where imageUrl equals to
        defaultProjectGalleryFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllProjectGalleriesByImageUrlIsInShouldWork() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where imageUrl in
        defaultProjectGalleryFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllProjectGalleriesByImageUrlIsNullOrNotNull() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where imageUrl is not null
        defaultProjectGalleryFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    void getAllProjectGalleriesByImageUrlContainsSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where imageUrl contains
        defaultProjectGalleryFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllProjectGalleriesByImageUrlNotContainsSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where imageUrl does not contain
        defaultProjectGalleryFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    void getAllProjectGalleriesByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where description equals to
        defaultProjectGalleryFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllProjectGalleriesByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where description in
        defaultProjectGalleryFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllProjectGalleriesByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where description is not null
        defaultProjectGalleryFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllProjectGalleriesByDescriptionContainsSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where description contains
        defaultProjectGalleryFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllProjectGalleriesByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where description does not contain
        defaultProjectGalleryFiltering(
            "description.doesNotContain=" + UPDATED_DESCRIPTION,
            "description.doesNotContain=" + DEFAULT_DESCRIPTION
        );
    }

    @Test
    void getAllProjectGalleriesByOrderIsEqualToSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where order equals to
        defaultProjectGalleryFiltering("order.equals=" + DEFAULT_ORDER, "order.equals=" + UPDATED_ORDER);
    }

    @Test
    void getAllProjectGalleriesByOrderIsInShouldWork() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where order in
        defaultProjectGalleryFiltering("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER, "order.in=" + UPDATED_ORDER);
    }

    @Test
    void getAllProjectGalleriesByOrderIsNullOrNotNull() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where order is not null
        defaultProjectGalleryFiltering("order.specified=true", "order.specified=false");
    }

    @Test
    void getAllProjectGalleriesByOrderIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where order is greater than or equal to
        defaultProjectGalleryFiltering("order.greaterThanOrEqual=" + DEFAULT_ORDER, "order.greaterThanOrEqual=" + UPDATED_ORDER);
    }

    @Test
    void getAllProjectGalleriesByOrderIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where order is less than or equal to
        defaultProjectGalleryFiltering("order.lessThanOrEqual=" + DEFAULT_ORDER, "order.lessThanOrEqual=" + SMALLER_ORDER);
    }

    @Test
    void getAllProjectGalleriesByOrderIsLessThanSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where order is less than
        defaultProjectGalleryFiltering("order.lessThan=" + UPDATED_ORDER, "order.lessThan=" + DEFAULT_ORDER);
    }

    @Test
    void getAllProjectGalleriesByOrderIsGreaterThanSomething() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        // Get all the projectGalleryList where order is greater than
        defaultProjectGalleryFiltering("order.greaterThan=" + SMALLER_ORDER, "order.greaterThan=" + DEFAULT_ORDER);
    }

    @Test
    void getAllProjectGalleriesByProjectIsEqualToSomething() {
        Project project = ProjectResourceIT.createEntity();
        projectRepository.save(project).block();
        Long projectId = project.getId();
        projectGallery.setProjectId(projectId);
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();
        // Get all the projectGalleryList where project equals to projectId
        defaultProjectGalleryShouldBeFound("projectId.equals=" + projectId);

        // Get all the projectGalleryList where project equals to (projectId + 1)
        defaultProjectGalleryShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultProjectGalleryFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultProjectGalleryShouldBeFound(shouldBeFound);
        defaultProjectGalleryShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectGalleryShouldBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[*].id")
            .value(hasItem(projectGallery.getId().intValue()))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].order")
            .value(hasItem(DEFAULT_ORDER));

        // Check, that the count call also returns 1
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(1));
    }

    /**
     * Executes the search, and checks that the default entity is not returned.
     */
    private void defaultProjectGalleryShouldNotBeFound(String filter) {
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .isArray()
            .jsonPath("$")
            .isEmpty();

        // Check, that the count call also returns 0
        webTestClient
            .get()
            .uri(ENTITY_API_URL + "/count?sort=id,desc&" + filter)
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$")
            .value(is(0));
    }

    @Test
    void getNonExistingProjectGallery() {
        // Get the projectGallery
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProjectGallery() throws Exception {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectGallery
        ProjectGallery updatedProjectGallery = projectGalleryRepository.findById(projectGallery.getId()).block();
        updatedProjectGallery.imageUrl(UPDATED_IMAGE_URL).description(UPDATED_DESCRIPTION).order(UPDATED_ORDER);
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(updatedProjectGallery);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectGalleryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectGalleryToMatchAllProperties(updatedProjectGallery);
    }

    @Test
    void putNonExistingProjectGallery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectGallery.setId(longCount.incrementAndGet());

        // Create the ProjectGallery
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectGalleryDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProjectGallery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectGallery.setId(longCount.incrementAndGet());

        // Create the ProjectGallery
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProjectGallery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectGallery.setId(longCount.incrementAndGet());

        // Create the ProjectGallery
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProjectGalleryWithPatch() throws Exception {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectGallery using partial update
        ProjectGallery partialUpdatedProjectGallery = new ProjectGallery();
        partialUpdatedProjectGallery.setId(projectGallery.getId());

        partialUpdatedProjectGallery.description(UPDATED_DESCRIPTION);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectGallery.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProjectGallery))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectGallery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectGalleryUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProjectGallery, projectGallery),
            getPersistedProjectGallery(projectGallery)
        );
    }

    @Test
    void fullUpdateProjectGalleryWithPatch() throws Exception {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectGallery using partial update
        ProjectGallery partialUpdatedProjectGallery = new ProjectGallery();
        partialUpdatedProjectGallery.setId(projectGallery.getId());

        partialUpdatedProjectGallery.imageUrl(UPDATED_IMAGE_URL).description(UPDATED_DESCRIPTION).order(UPDATED_ORDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectGallery.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProjectGallery))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectGallery in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectGalleryUpdatableFieldsEquals(partialUpdatedProjectGallery, getPersistedProjectGallery(partialUpdatedProjectGallery));
    }

    @Test
    void patchNonExistingProjectGallery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectGallery.setId(longCount.incrementAndGet());

        // Create the ProjectGallery
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectGalleryDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProjectGallery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectGallery.setId(longCount.incrementAndGet());

        // Create the ProjectGallery
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProjectGallery() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectGallery.setId(longCount.incrementAndGet());

        // Create the ProjectGallery
        ProjectGalleryDTO projectGalleryDTO = projectGalleryMapper.toDto(projectGallery);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectGalleryDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectGallery in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProjectGallery() {
        // Initialize the database
        insertedProjectGallery = projectGalleryRepository.save(projectGallery).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the projectGallery
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projectGallery.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectGalleryRepository.count().block();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected ProjectGallery getPersistedProjectGallery(ProjectGallery projectGallery) {
        return projectGalleryRepository.findById(projectGallery.getId()).block();
    }

    protected void assertPersistedProjectGalleryToMatchAllProperties(ProjectGallery expectedProjectGallery) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectGalleryAllPropertiesEquals(expectedProjectGallery, getPersistedProjectGallery(expectedProjectGallery));
        assertProjectGalleryUpdatableFieldsEquals(expectedProjectGallery, getPersistedProjectGallery(expectedProjectGallery));
    }

    protected void assertPersistedProjectGalleryToMatchUpdatableProperties(ProjectGallery expectedProjectGallery) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectGalleryAllUpdatablePropertiesEquals(expectedProjectGallery, getPersistedProjectGallery(expectedProjectGallery));
        assertProjectGalleryUpdatableFieldsEquals(expectedProjectGallery, getPersistedProjectGallery(expectedProjectGallery));
    }
}
