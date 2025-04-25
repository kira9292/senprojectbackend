package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.ExternalLinkAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.ExternalLink;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.enumeration.LinkType;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.ExternalLinkRepository;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.service.dto.ExternalLinkDTO;
import com.senprojectbackend1.service.mapper.ExternalLinkMapper;
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
 * Integration tests for the {@link ExternalLinkResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ExternalLinkResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final LinkType DEFAULT_TYPE = LinkType.DOCUMENTATION;
    private static final LinkType UPDATED_TYPE = LinkType.DEMO;

    private static final String ENTITY_API_URL = "/api/external-links";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ExternalLinkRepository externalLinkRepository;

    @Autowired
    private ExternalLinkMapper externalLinkMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private ExternalLink externalLink;

    private ExternalLink insertedExternalLink;

    @Autowired
    private ProjectRepository projectRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExternalLink createEntity(EntityManager em) {
        ExternalLink externalLink = new ExternalLink().title(DEFAULT_TITLE).url(DEFAULT_URL).type(DEFAULT_TYPE);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createEntity()).block();
        externalLink.setProject(project);
        return externalLink;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static ExternalLink createUpdatedEntity(EntityManager em) {
        ExternalLink updatedExternalLink = new ExternalLink().title(UPDATED_TITLE).url(UPDATED_URL).type(UPDATED_TYPE);
        // Add required entity
        Project project;
        project = em.insert(ProjectResourceIT.createUpdatedEntity()).block();
        updatedExternalLink.setProject(project);
        return updatedExternalLink;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(ExternalLink.class).block();
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
        externalLink = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedExternalLink != null) {
            externalLinkRepository.delete(insertedExternalLink).block();
            insertedExternalLink = null;
        }
        deleteEntities(em);
    }

    @Test
    void createExternalLink() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the ExternalLink
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);
        var returnedExternalLinkDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ExternalLinkDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the ExternalLink in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedExternalLink = externalLinkMapper.toEntity(returnedExternalLinkDTO);
        assertExternalLinkUpdatableFieldsEquals(returnedExternalLink, getPersistedExternalLink(returnedExternalLink));

        insertedExternalLink = returnedExternalLink;
    }

    @Test
    void createExternalLinkWithExistingId() throws Exception {
        // Create the ExternalLink with an existing ID
        externalLink.setId(1L);
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        externalLink.setTitle(null);

        // Create the ExternalLink, which fails.
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        externalLink.setUrl(null);

        // Create the ExternalLink, which fails.
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        externalLink.setType(null);

        // Create the ExternalLink, which fails.
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllExternalLinks() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList
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
            .value(hasItem(externalLink.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()));
    }

    @Test
    void getExternalLink() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get the externalLink
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, externalLink.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(externalLink.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.url")
            .value(is(DEFAULT_URL))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()));
    }

    @Test
    void getExternalLinksByIdFiltering() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        Long id = externalLink.getId();

        defaultExternalLinkFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultExternalLinkFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultExternalLinkFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllExternalLinksByTitleIsEqualToSomething() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where title equals to
        defaultExternalLinkFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    void getAllExternalLinksByTitleIsInShouldWork() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where title in
        defaultExternalLinkFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    void getAllExternalLinksByTitleIsNullOrNotNull() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where title is not null
        defaultExternalLinkFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    void getAllExternalLinksByTitleContainsSomething() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where title contains
        defaultExternalLinkFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    void getAllExternalLinksByTitleNotContainsSomething() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where title does not contain
        defaultExternalLinkFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    void getAllExternalLinksByUrlIsEqualToSomething() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where url equals to
        defaultExternalLinkFiltering("url.equals=" + DEFAULT_URL, "url.equals=" + UPDATED_URL);
    }

    @Test
    void getAllExternalLinksByUrlIsInShouldWork() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where url in
        defaultExternalLinkFiltering("url.in=" + DEFAULT_URL + "," + UPDATED_URL, "url.in=" + UPDATED_URL);
    }

    @Test
    void getAllExternalLinksByUrlIsNullOrNotNull() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where url is not null
        defaultExternalLinkFiltering("url.specified=true", "url.specified=false");
    }

    @Test
    void getAllExternalLinksByUrlContainsSomething() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where url contains
        defaultExternalLinkFiltering("url.contains=" + DEFAULT_URL, "url.contains=" + UPDATED_URL);
    }

    @Test
    void getAllExternalLinksByUrlNotContainsSomething() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where url does not contain
        defaultExternalLinkFiltering("url.doesNotContain=" + UPDATED_URL, "url.doesNotContain=" + DEFAULT_URL);
    }

    @Test
    void getAllExternalLinksByTypeIsEqualToSomething() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where type equals to
        defaultExternalLinkFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    void getAllExternalLinksByTypeIsInShouldWork() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where type in
        defaultExternalLinkFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    void getAllExternalLinksByTypeIsNullOrNotNull() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        // Get all the externalLinkList where type is not null
        defaultExternalLinkFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    void getAllExternalLinksByProjectIsEqualToSomething() {
        Project project = ProjectResourceIT.createEntity();
        projectRepository.save(project).block();
        Long projectId = project.getId();
        externalLink.setProjectId(projectId);
        insertedExternalLink = externalLinkRepository.save(externalLink).block();
        // Get all the externalLinkList where project equals to projectId
        defaultExternalLinkShouldBeFound("projectId.equals=" + projectId);

        // Get all the externalLinkList where project equals to (projectId + 1)
        defaultExternalLinkShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultExternalLinkFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultExternalLinkShouldBeFound(shouldBeFound);
        defaultExternalLinkShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultExternalLinkShouldBeFound(String filter) {
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
            .value(hasItem(externalLink.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].url")
            .value(hasItem(DEFAULT_URL))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()));

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
    private void defaultExternalLinkShouldNotBeFound(String filter) {
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
    void getNonExistingExternalLink() {
        // Get the externalLink
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingExternalLink() throws Exception {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the externalLink
        ExternalLink updatedExternalLink = externalLinkRepository.findById(externalLink.getId()).block();
        updatedExternalLink.title(UPDATED_TITLE).url(UPDATED_URL).type(UPDATED_TYPE);
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(updatedExternalLink);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, externalLinkDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedExternalLinkToMatchAllProperties(updatedExternalLink);
    }

    @Test
    void putNonExistingExternalLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalLink.setId(longCount.incrementAndGet());

        // Create the ExternalLink
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, externalLinkDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchExternalLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalLink.setId(longCount.incrementAndGet());

        // Create the ExternalLink
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamExternalLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalLink.setId(longCount.incrementAndGet());

        // Create the ExternalLink
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateExternalLinkWithPatch() throws Exception {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the externalLink using partial update
        ExternalLink partialUpdatedExternalLink = new ExternalLink();
        partialUpdatedExternalLink.setId(externalLink.getId());

        partialUpdatedExternalLink.title(UPDATED_TITLE).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExternalLink.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedExternalLink))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExternalLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExternalLinkUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedExternalLink, externalLink),
            getPersistedExternalLink(externalLink)
        );
    }

    @Test
    void fullUpdateExternalLinkWithPatch() throws Exception {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the externalLink using partial update
        ExternalLink partialUpdatedExternalLink = new ExternalLink();
        partialUpdatedExternalLink.setId(externalLink.getId());

        partialUpdatedExternalLink.title(UPDATED_TITLE).url(UPDATED_URL).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedExternalLink.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedExternalLink))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the ExternalLink in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertExternalLinkUpdatableFieldsEquals(partialUpdatedExternalLink, getPersistedExternalLink(partialUpdatedExternalLink));
    }

    @Test
    void patchNonExistingExternalLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalLink.setId(longCount.incrementAndGet());

        // Create the ExternalLink
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, externalLinkDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchExternalLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalLink.setId(longCount.incrementAndGet());

        // Create the ExternalLink
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamExternalLink() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        externalLink.setId(longCount.incrementAndGet());

        // Create the ExternalLink
        ExternalLinkDTO externalLinkDTO = externalLinkMapper.toDto(externalLink);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(externalLinkDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the ExternalLink in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteExternalLink() {
        // Initialize the database
        insertedExternalLink = externalLinkRepository.save(externalLink).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the externalLink
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, externalLink.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return externalLinkRepository.count().block();
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

    protected ExternalLink getPersistedExternalLink(ExternalLink externalLink) {
        return externalLinkRepository.findById(externalLink.getId()).block();
    }

    protected void assertPersistedExternalLinkToMatchAllProperties(ExternalLink expectedExternalLink) {
        // Test fails because reactive api returns an empty object instead of null
        // assertExternalLinkAllPropertiesEquals(expectedExternalLink, getPersistedExternalLink(expectedExternalLink));
        assertExternalLinkUpdatableFieldsEquals(expectedExternalLink, getPersistedExternalLink(expectedExternalLink));
    }

    protected void assertPersistedExternalLinkToMatchUpdatableProperties(ExternalLink expectedExternalLink) {
        // Test fails because reactive api returns an empty object instead of null
        // assertExternalLinkAllUpdatablePropertiesEquals(expectedExternalLink, getPersistedExternalLink(expectedExternalLink));
        assertExternalLinkUpdatableFieldsEquals(expectedExternalLink, getPersistedExternalLink(expectedExternalLink));
    }
}
