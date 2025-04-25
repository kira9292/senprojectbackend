package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.EngagementProjectAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import com.senprojectbackend1.repository.EngagementProjectRepository;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.dto.EngagementProjectDTO;
import com.senprojectbackend1.service.mapper.EngagementProjectMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link EngagementProjectResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EngagementProjectResourceIT {

    private static final EngagementType DEFAULT_TYPE = EngagementType.LIKE;
    private static final EngagementType UPDATED_TYPE = EngagementType.FAVORITE;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/engagement-projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EngagementProjectRepository engagementProjectRepository;

    @Autowired
    private EngagementProjectMapper engagementProjectMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private EngagementProject engagementProject;

    private EngagementProject insertedEngagementProject;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EngagementProject createEntity() {
        return new EngagementProject().type(DEFAULT_TYPE).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EngagementProject createUpdatedEntity() {
        return new EngagementProject().type(UPDATED_TYPE).createdAt(UPDATED_CREATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(EngagementProject.class).block();
        } catch (Exception e) {
            // It can fail, if other entities are still referring this - it will be removed later.
        }
    }

    @BeforeEach
    public void setupCsrf() {
        webTestClient = webTestClient.mutateWith(csrf());
    }

    @BeforeEach
    public void initTest() {
        engagementProject = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEngagementProject != null) {
            engagementProjectRepository.delete(insertedEngagementProject).block();
            insertedEngagementProject = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEngagementProject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EngagementProject
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);
        var returnedEngagementProjectDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EngagementProjectDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EngagementProject in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEngagementProject = engagementProjectMapper.toEntity(returnedEngagementProjectDTO);
        assertEngagementProjectUpdatableFieldsEquals(returnedEngagementProject, getPersistedEngagementProject(returnedEngagementProject));

        insertedEngagementProject = returnedEngagementProject;
    }

    @Test
    void createEngagementProjectWithExistingId() throws Exception {
        // Create the EngagementProject with an existing ID
        engagementProject.setId(1L);
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        engagementProject.setType(null);

        // Create the EngagementProject, which fails.
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        engagementProject.setCreatedAt(null);

        // Create the EngagementProject, which fails.
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEngagementProjects() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get all the engagementProjectList
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
            .value(hasItem(engagementProject.getId().intValue()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getEngagementProject() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get the engagementProject
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, engagementProject.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(engagementProject.getId().intValue()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getEngagementProjectsByIdFiltering() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        Long id = engagementProject.getId();

        defaultEngagementProjectFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEngagementProjectFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEngagementProjectFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllEngagementProjectsByTypeIsEqualToSomething() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get all the engagementProjectList where type equals to
        defaultEngagementProjectFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    void getAllEngagementProjectsByTypeIsInShouldWork() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get all the engagementProjectList where type in
        defaultEngagementProjectFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    void getAllEngagementProjectsByTypeIsNullOrNotNull() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get all the engagementProjectList where type is not null
        defaultEngagementProjectFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    void getAllEngagementProjectsByCreatedAtIsEqualToSomething() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get all the engagementProjectList where createdAt equals to
        defaultEngagementProjectFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllEngagementProjectsByCreatedAtIsInShouldWork() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get all the engagementProjectList where createdAt in
        defaultEngagementProjectFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    void getAllEngagementProjectsByCreatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        // Get all the engagementProjectList where createdAt is not null
        defaultEngagementProjectFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    void getAllEngagementProjectsByUserIsEqualToSomething() {
        UserProfile user = UserProfileResourceIT.createEntity(em);
        userProfileRepository.save(user).block();
        String userId = user.getId();
        engagementProject.setUserId(userId);
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();
        // Get all the engagementProjectList where user equals to userId
        defaultEngagementProjectShouldBeFound("userId.equals=" + userId);

        // Get all the engagementProjectList where user equals to "invalid-id"
        defaultEngagementProjectShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    void getAllEngagementProjectsByProjectIsEqualToSomething() {
        Project project = ProjectResourceIT.createEntity();
        projectRepository.save(project).block();
        Long projectId = project.getId();
        engagementProject.setProjectId(projectId);
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();
        // Get all the engagementProjectList where project equals to projectId
        defaultEngagementProjectShouldBeFound("projectId.equals=" + projectId);

        // Get all the engagementProjectList where project equals to (projectId + 1)
        defaultEngagementProjectShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultEngagementProjectFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultEngagementProjectShouldBeFound(shouldBeFound);
        defaultEngagementProjectShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEngagementProjectShouldBeFound(String filter) {
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
            .value(hasItem(engagementProject.getId().intValue()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));

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
    private void defaultEngagementProjectShouldNotBeFound(String filter) {
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
    void getNonExistingEngagementProject() {
        // Get the engagementProject
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEngagementProject() throws Exception {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the engagementProject
        EngagementProject updatedEngagementProject = engagementProjectRepository.findById(engagementProject.getId()).block();
        updatedEngagementProject.type(UPDATED_TYPE).createdAt(UPDATED_CREATED_AT);
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(updatedEngagementProject);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, engagementProjectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEngagementProjectToMatchAllProperties(updatedEngagementProject);
    }

    @Test
    void putNonExistingEngagementProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementProject.setId(longCount.incrementAndGet());

        // Create the EngagementProject
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, engagementProjectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEngagementProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementProject.setId(longCount.incrementAndGet());

        // Create the EngagementProject
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEngagementProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementProject.setId(longCount.incrementAndGet());

        // Create the EngagementProject
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEngagementProjectWithPatch() throws Exception {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the engagementProject using partial update
        EngagementProject partialUpdatedEngagementProject = new EngagementProject();
        partialUpdatedEngagementProject.setId(engagementProject.getId());

        partialUpdatedEngagementProject.type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEngagementProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEngagementProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EngagementProject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEngagementProjectUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEngagementProject, engagementProject),
            getPersistedEngagementProject(engagementProject)
        );
    }

    @Test
    void fullUpdateEngagementProjectWithPatch() throws Exception {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the engagementProject using partial update
        EngagementProject partialUpdatedEngagementProject = new EngagementProject();
        partialUpdatedEngagementProject.setId(engagementProject.getId());

        partialUpdatedEngagementProject.type(UPDATED_TYPE).createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEngagementProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEngagementProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EngagementProject in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEngagementProjectUpdatableFieldsEquals(
            partialUpdatedEngagementProject,
            getPersistedEngagementProject(partialUpdatedEngagementProject)
        );
    }

    @Test
    void patchNonExistingEngagementProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementProject.setId(longCount.incrementAndGet());

        // Create the EngagementProject
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, engagementProjectDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEngagementProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementProject.setId(longCount.incrementAndGet());

        // Create the EngagementProject
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEngagementProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementProject.setId(longCount.incrementAndGet());

        // Create the EngagementProject
        EngagementProjectDTO engagementProjectDTO = engagementProjectMapper.toDto(engagementProject);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(engagementProjectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EngagementProject in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEngagementProject() {
        // Initialize the database
        insertedEngagementProject = engagementProjectRepository.save(engagementProject).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the engagementProject
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, engagementProject.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return engagementProjectRepository.count().block();
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

    protected EngagementProject getPersistedEngagementProject(EngagementProject engagementProject) {
        return engagementProjectRepository.findById(engagementProject.getId()).block();
    }

    protected void assertPersistedEngagementProjectToMatchAllProperties(EngagementProject expectedEngagementProject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEngagementProjectAllPropertiesEquals(expectedEngagementProject, getPersistedEngagementProject(expectedEngagementProject));
        assertEngagementProjectUpdatableFieldsEquals(expectedEngagementProject, getPersistedEngagementProject(expectedEngagementProject));
    }

    protected void assertPersistedEngagementProjectToMatchUpdatableProperties(EngagementProject expectedEngagementProject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEngagementProjectAllUpdatablePropertiesEquals(expectedEngagementProject, getPersistedEngagementProject(expectedEngagementProject));
        assertEngagementProjectUpdatableFieldsEquals(expectedEngagementProject, getPersistedEngagementProject(expectedEngagementProject));
    }
}
