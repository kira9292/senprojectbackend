package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.ProjectSectionAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.ProjectSection;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.repository.ProjectSectionRepository;
import com.senprojectbackend1.service.dto.ProjectSectionDTO;
import com.senprojectbackend1.service.mapper.ProjectSectionMapper;
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
 * Integration tests for the {@link ProjectSectionResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectSectionResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_MEDIA_URL = "AAAAAAAAAA";
    private static final String UPDATED_MEDIA_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;
    private static final Integer SMALLER_ORDER = 1 - 1;

    private static final String ENTITY_API_URL = "/api/project-sections";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectSectionRepository projectSectionRepository;

    @Autowired
    private ProjectSectionMapper projectSectionMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ProjectSection projectSection;

    private ProjectSection insertedProjectSection;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSection createEntity(EntityManager em) {
        ProjectSection projectSection = new ProjectSection()
            .title(DEFAULT_TITLE)
            .content(DEFAULT_CONTENT)
            .mediaUrl(DEFAULT_MEDIA_URL)
            .order(DEFAULT_ORDER);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createEntity()).block();
        projectSection.setProject(project);
        return projectSection;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ProjectSection createUpdatedEntity(EntityManager em) {
        ProjectSection updatedProjectSection = new ProjectSection()
            .title(UPDATED_TITLE)
            .content(UPDATED_CONTENT)
            .mediaUrl(UPDATED_MEDIA_URL)
            .order(UPDATED_ORDER);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createUpdatedEntity()).block();
        updatedProjectSection.setProject(project);
        return updatedProjectSection;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ProjectSection.class).block();
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
        projectSection = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedProjectSection != null) {
            projectSectionRepository.delete(insertedProjectSection).block();
            insertedProjectSection = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProjectSection() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ProjectSection
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);
        var returnedProjectSectionDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProjectSectionDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ProjectSection in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProjectSection = projectSectionMapper.toEntity(returnedProjectSectionDTO);
        assertProjectSectionUpdatableFieldsEquals(returnedProjectSection, getPersistedProjectSection(returnedProjectSection));

        insertedProjectSection = returnedProjectSection;
    }

    @Test
    void createProjectSectionWithExistingId() throws Exception {
        // Create the ProjectSection with an existing ID
        projectSection.setId(1L);
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectSection.setTitle(null);

        // Create the ProjectSection, which fails.
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectSection.setContent(null);

        // Create the ProjectSection, which fails.
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkOrderIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        projectSection.setOrder(null);

        // Create the ProjectSection, which fails.
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProjectSections() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList
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
            .value(hasItem(projectSection.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].mediaUrl")
            .value(hasItem(DEFAULT_MEDIA_URL))
            .jsonPath("$.[*].order")
            .value(hasItem(DEFAULT_ORDER));
    }

    @Test
    void getProjectSection() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get the projectSection
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, projectSection.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(projectSection.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.mediaUrl")
            .value(is(DEFAULT_MEDIA_URL))
            .jsonPath("$.order")
            .value(is(DEFAULT_ORDER));
    }

    @Test
    void getProjectSectionsByIdFiltering() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        Long id = projectSection.getId();

        defaultProjectSectionFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProjectSectionFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProjectSectionFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllProjectSectionsByTitleIsEqualToSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where title equals to
        defaultProjectSectionFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    void getAllProjectSectionsByTitleIsInShouldWork() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where title in
        defaultProjectSectionFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    void getAllProjectSectionsByTitleIsNullOrNotNull() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where title is not null
        defaultProjectSectionFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    void getAllProjectSectionsByTitleContainsSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where title contains
        defaultProjectSectionFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    void getAllProjectSectionsByTitleNotContainsSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where title does not contain
        defaultProjectSectionFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    void getAllProjectSectionsByContentIsEqualToSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where content equals to
        defaultProjectSectionFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    void getAllProjectSectionsByContentIsInShouldWork() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where content in
        defaultProjectSectionFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    void getAllProjectSectionsByContentIsNullOrNotNull() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where content is not null
        defaultProjectSectionFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    void getAllProjectSectionsByContentContainsSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where content contains
        defaultProjectSectionFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    void getAllProjectSectionsByContentNotContainsSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where content does not contain
        defaultProjectSectionFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    void getAllProjectSectionsByMediaUrlIsEqualToSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where mediaUrl equals to
        defaultProjectSectionFiltering("mediaUrl.equals=" + DEFAULT_MEDIA_URL, "mediaUrl.equals=" + UPDATED_MEDIA_URL);
    }

    @Test
    void getAllProjectSectionsByMediaUrlIsInShouldWork() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where mediaUrl in
        defaultProjectSectionFiltering("mediaUrl.in=" + DEFAULT_MEDIA_URL + "," + UPDATED_MEDIA_URL, "mediaUrl.in=" + UPDATED_MEDIA_URL);
    }

    @Test
    void getAllProjectSectionsByMediaUrlIsNullOrNotNull() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where mediaUrl is not null
        defaultProjectSectionFiltering("mediaUrl.specified=true", "mediaUrl.specified=false");
    }

    @Test
    void getAllProjectSectionsByMediaUrlContainsSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where mediaUrl contains
        defaultProjectSectionFiltering("mediaUrl.contains=" + DEFAULT_MEDIA_URL, "mediaUrl.contains=" + UPDATED_MEDIA_URL);
    }

    @Test
    void getAllProjectSectionsByMediaUrlNotContainsSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where mediaUrl does not contain
        defaultProjectSectionFiltering("mediaUrl.doesNotContain=" + UPDATED_MEDIA_URL, "mediaUrl.doesNotContain=" + DEFAULT_MEDIA_URL);
    }

    @Test
    void getAllProjectSectionsByOrderIsEqualToSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where order equals to
        defaultProjectSectionFiltering("order.equals=" + DEFAULT_ORDER, "order.equals=" + UPDATED_ORDER);
    }

    @Test
    void getAllProjectSectionsByOrderIsInShouldWork() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where order in
        defaultProjectSectionFiltering("order.in=" + DEFAULT_ORDER + "," + UPDATED_ORDER, "order.in=" + UPDATED_ORDER);
    }

    @Test
    void getAllProjectSectionsByOrderIsNullOrNotNull() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where order is not null
        defaultProjectSectionFiltering("order.specified=true", "order.specified=false");
    }

    @Test
    void getAllProjectSectionsByOrderIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where order is greater than or equal to
        defaultProjectSectionFiltering("order.greaterThanOrEqual=" + DEFAULT_ORDER, "order.greaterThanOrEqual=" + UPDATED_ORDER);
    }

    @Test
    void getAllProjectSectionsByOrderIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where order is less than or equal to
        defaultProjectSectionFiltering("order.lessThanOrEqual=" + DEFAULT_ORDER, "order.lessThanOrEqual=" + SMALLER_ORDER);
    }

    @Test
    void getAllProjectSectionsByOrderIsLessThanSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where order is less than
        defaultProjectSectionFiltering("order.lessThan=" + UPDATED_ORDER, "order.lessThan=" + DEFAULT_ORDER);
    }

    @Test
    void getAllProjectSectionsByOrderIsGreaterThanSomething() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        // Get all the projectSectionList where order is greater than
        defaultProjectSectionFiltering("order.greaterThan=" + SMALLER_ORDER, "order.greaterThan=" + DEFAULT_ORDER);
    }

    @Test
    void getAllProjectSectionsByProjectIsEqualToSomething() {
        Project project = ProjectResourceIT.createEntity();
        projectRepository.save(project).block();
        Long projectId = project.getId();
        projectSection.setProjectId(projectId);
        insertedProjectSection = projectSectionRepository.save(projectSection).block();
        // Get all the projectSectionList where project equals to projectId
        defaultProjectSectionShouldBeFound("projectId.equals=" + projectId);

        // Get all the projectSectionList where project equals to (projectId + 1)
        defaultProjectSectionShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultProjectSectionFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultProjectSectionShouldBeFound(shouldBeFound);
        defaultProjectSectionShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectSectionShouldBeFound(String filter) {
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
            .value(hasItem(projectSection.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].mediaUrl")
            .value(hasItem(DEFAULT_MEDIA_URL))
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
    private void defaultProjectSectionShouldNotBeFound(String filter) {
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
    void getNonExistingProjectSection() {
        // Get the projectSection
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProjectSection() throws Exception {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectSection
        ProjectSection updatedProjectSection = projectSectionRepository.findById(projectSection.getId()).block();
        updatedProjectSection.title(UPDATED_TITLE).content(UPDATED_CONTENT).mediaUrl(UPDATED_MEDIA_URL).order(UPDATED_ORDER);
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(updatedProjectSection);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectSectionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectSectionToMatchAllProperties(updatedProjectSection);
    }

    @Test
    void putNonExistingProjectSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectSection.setId(longCount.incrementAndGet());

        // Create the ProjectSection
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectSectionDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProjectSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectSection.setId(longCount.incrementAndGet());

        // Create the ProjectSection
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProjectSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectSection.setId(longCount.incrementAndGet());

        // Create the ProjectSection
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProjectSectionWithPatch() throws Exception {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectSection using partial update
        ProjectSection partialUpdatedProjectSection = new ProjectSection();
        partialUpdatedProjectSection.setId(projectSection.getId());

        partialUpdatedProjectSection.content(UPDATED_CONTENT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectSection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProjectSection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectSectionUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedProjectSection, projectSection),
            getPersistedProjectSection(projectSection)
        );
    }

    @Test
    void fullUpdateProjectSectionWithPatch() throws Exception {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the projectSection using partial update
        ProjectSection partialUpdatedProjectSection = new ProjectSection();
        partialUpdatedProjectSection.setId(projectSection.getId());

        partialUpdatedProjectSection.title(UPDATED_TITLE).content(UPDATED_CONTENT).mediaUrl(UPDATED_MEDIA_URL).order(UPDATED_ORDER);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProjectSection.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProjectSection))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ProjectSection in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectSectionUpdatableFieldsEquals(partialUpdatedProjectSection, getPersistedProjectSection(partialUpdatedProjectSection));
    }

    @Test
    void patchNonExistingProjectSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectSection.setId(longCount.incrementAndGet());

        // Create the ProjectSection
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectSectionDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProjectSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectSection.setId(longCount.incrementAndGet());

        // Create the ProjectSection
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProjectSection() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        projectSection.setId(longCount.incrementAndGet());

        // Create the ProjectSection
        ProjectSectionDTO projectSectionDTO = projectSectionMapper.toDto(projectSection);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectSectionDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ProjectSection in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProjectSection() {
        // Initialize the database
        insertedProjectSection = projectSectionRepository.save(projectSection).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the projectSection
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, projectSection.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectSectionRepository.count().block();
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

    protected ProjectSection getPersistedProjectSection(ProjectSection projectSection) {
        return projectSectionRepository.findById(projectSection.getId()).block();
    }

    protected void assertPersistedProjectSectionToMatchAllProperties(ProjectSection expectedProjectSection) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectSectionAllPropertiesEquals(expectedProjectSection, getPersistedProjectSection(expectedProjectSection));
        assertProjectSectionUpdatableFieldsEquals(expectedProjectSection, getPersistedProjectSection(expectedProjectSection));
    }

    protected void assertPersistedProjectSectionToMatchUpdatableProperties(ProjectSection expectedProjectSection) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectSectionAllUpdatablePropertiesEquals(expectedProjectSection, getPersistedProjectSection(expectedProjectSection));
        assertProjectSectionUpdatableFieldsEquals(expectedProjectSection, getPersistedProjectSection(expectedProjectSection));
    }
}
