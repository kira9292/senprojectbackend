package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.EngagementTeamAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.EngagementTeam;
import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.repository.EngagementTeamRepository;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.TeamRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.dto.EngagementTeamDTO;
import com.senprojectbackend1.service.mapper.EngagementTeamMapper;
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
 * Integration tests for the {@link EngagementTeamResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class EngagementTeamResourceIT {

    private static final Integer DEFAULT_LIKE = 1;
    private static final Integer UPDATED_LIKE = 2;
    private static final Integer SMALLER_LIKE = 1 - 1;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/engagement-teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private EngagementTeamRepository engagementTeamRepository;

    @Autowired
    private EngagementTeamMapper engagementTeamMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private EngagementTeam engagementTeam;

    private EngagementTeam insertedEngagementTeam;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EngagementTeam createEntity() {
        return new EngagementTeam().like(DEFAULT_LIKE).createdAt(DEFAULT_CREATED_AT);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static EngagementTeam createUpdatedEntity() {
        return new EngagementTeam().like(UPDATED_LIKE).createdAt(UPDATED_CREATED_AT);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(EngagementTeam.class).block();
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
        engagementTeam = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedEngagementTeam != null) {
            engagementTeamRepository.delete(insertedEngagementTeam).block();
            insertedEngagementTeam = null;
        }
        deleteEntities(em);
    }

    @Test
    void createEngagementTeam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the EngagementTeam
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);
        var returnedEngagementTeamDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(EngagementTeamDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the EngagementTeam in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedEngagementTeam = engagementTeamMapper.toEntity(returnedEngagementTeamDTO);
        assertEngagementTeamUpdatableFieldsEquals(returnedEngagementTeam, getPersistedEngagementTeam(returnedEngagementTeam));

        insertedEngagementTeam = returnedEngagementTeam;
    }

    @Test
    void createEngagementTeamWithExistingId() throws Exception {
        // Create the EngagementTeam with an existing ID
        engagementTeam.setId(1L);
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkLikeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        engagementTeam.setLike(null);

        // Create the EngagementTeam, which fails.
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        engagementTeam.setCreatedAt(null);

        // Create the EngagementTeam, which fails.
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllEngagementTeams() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList
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
            .value(hasItem(engagementTeam.getId().intValue()))
            .jsonPath("$.[*].like")
            .value(hasItem(DEFAULT_LIKE))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getEngagementTeam() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get the engagementTeam
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, engagementTeam.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(engagementTeam.getId().intValue()))
            .jsonPath("$.like")
            .value(is(DEFAULT_LIKE))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()));
    }

    @Test
    void getEngagementTeamsByIdFiltering() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        Long id = engagementTeam.getId();

        defaultEngagementTeamFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultEngagementTeamFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultEngagementTeamFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllEngagementTeamsByLikeIsEqualToSomething() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where like equals to
        defaultEngagementTeamFiltering("like.equals=" + DEFAULT_LIKE, "like.equals=" + UPDATED_LIKE);
    }

    @Test
    void getAllEngagementTeamsByLikeIsInShouldWork() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where like in
        defaultEngagementTeamFiltering("like.in=" + DEFAULT_LIKE + "," + UPDATED_LIKE, "like.in=" + UPDATED_LIKE);
    }

    @Test
    void getAllEngagementTeamsByLikeIsNullOrNotNull() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where like is not null
        defaultEngagementTeamFiltering("like.specified=true", "like.specified=false");
    }

    @Test
    void getAllEngagementTeamsByLikeIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where like is greater than or equal to
        defaultEngagementTeamFiltering("like.greaterThanOrEqual=" + DEFAULT_LIKE, "like.greaterThanOrEqual=" + UPDATED_LIKE);
    }

    @Test
    void getAllEngagementTeamsByLikeIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where like is less than or equal to
        defaultEngagementTeamFiltering("like.lessThanOrEqual=" + DEFAULT_LIKE, "like.lessThanOrEqual=" + SMALLER_LIKE);
    }

    @Test
    void getAllEngagementTeamsByLikeIsLessThanSomething() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where like is less than
        defaultEngagementTeamFiltering("like.lessThan=" + UPDATED_LIKE, "like.lessThan=" + DEFAULT_LIKE);
    }

    @Test
    void getAllEngagementTeamsByLikeIsGreaterThanSomething() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where like is greater than
        defaultEngagementTeamFiltering("like.greaterThan=" + SMALLER_LIKE, "like.greaterThan=" + DEFAULT_LIKE);
    }

    @Test
    void getAllEngagementTeamsByCreatedAtIsEqualToSomething() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where createdAt equals to
        defaultEngagementTeamFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllEngagementTeamsByCreatedAtIsInShouldWork() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where createdAt in
        defaultEngagementTeamFiltering(
            "createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT,
            "createdAt.in=" + UPDATED_CREATED_AT
        );
    }

    @Test
    void getAllEngagementTeamsByCreatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        // Get all the engagementTeamList where createdAt is not null
        defaultEngagementTeamFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    void getAllEngagementTeamsByTeamIsEqualToSomething() {
        Team team = TeamResourceIT.createEntity();
        teamRepository.save(team).block();
        Long teamId = team.getId();
        engagementTeam.setTeamId(teamId);
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();
        // Get all the engagementTeamList where team equals to teamId
        defaultEngagementTeamShouldBeFound("teamId.equals=" + teamId);

        // Get all the engagementTeamList where team equals to (teamId + 1)
        defaultEngagementTeamShouldNotBeFound("teamId.equals=" + (teamId + 1));
    }

    @Test
    void getAllEngagementTeamsByUserIsEqualToSomething() {
        UserProfile user = UserProfileResourceIT.createEntity(em);
        userProfileRepository.save(user).block();
        String userId = user.getId();
        engagementTeam.setUserId(userId);
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();
        // Get all the engagementTeamList where user equals to userId
        defaultEngagementTeamShouldBeFound("userId.equals=" + userId);

        // Get all the engagementTeamList where user equals to "invalid-id"
        defaultEngagementTeamShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    private void defaultEngagementTeamFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultEngagementTeamShouldBeFound(shouldBeFound);
        defaultEngagementTeamShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultEngagementTeamShouldBeFound(String filter) {
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
            .value(hasItem(engagementTeam.getId().intValue()))
            .jsonPath("$.[*].like")
            .value(hasItem(DEFAULT_LIKE))
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
    private void defaultEngagementTeamShouldNotBeFound(String filter) {
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
    void getNonExistingEngagementTeam() {
        // Get the engagementTeam
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingEngagementTeam() throws Exception {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the engagementTeam
        EngagementTeam updatedEngagementTeam = engagementTeamRepository.findById(engagementTeam.getId()).block();
        updatedEngagementTeam.like(UPDATED_LIKE).createdAt(UPDATED_CREATED_AT);
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(updatedEngagementTeam);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, engagementTeamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedEngagementTeamToMatchAllProperties(updatedEngagementTeam);
    }

    @Test
    void putNonExistingEngagementTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementTeam.setId(longCount.incrementAndGet());

        // Create the EngagementTeam
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, engagementTeamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchEngagementTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementTeam.setId(longCount.incrementAndGet());

        // Create the EngagementTeam
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamEngagementTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementTeam.setId(longCount.incrementAndGet());

        // Create the EngagementTeam
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateEngagementTeamWithPatch() throws Exception {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the engagementTeam using partial update
        EngagementTeam partialUpdatedEngagementTeam = new EngagementTeam();
        partialUpdatedEngagementTeam.setId(engagementTeam.getId());

        partialUpdatedEngagementTeam.createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEngagementTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEngagementTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EngagementTeam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEngagementTeamUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedEngagementTeam, engagementTeam),
            getPersistedEngagementTeam(engagementTeam)
        );
    }

    @Test
    void fullUpdateEngagementTeamWithPatch() throws Exception {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the engagementTeam using partial update
        EngagementTeam partialUpdatedEngagementTeam = new EngagementTeam();
        partialUpdatedEngagementTeam.setId(engagementTeam.getId());

        partialUpdatedEngagementTeam.like(UPDATED_LIKE).createdAt(UPDATED_CREATED_AT);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedEngagementTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedEngagementTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the EngagementTeam in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertEngagementTeamUpdatableFieldsEquals(partialUpdatedEngagementTeam, getPersistedEngagementTeam(partialUpdatedEngagementTeam));
    }

    @Test
    void patchNonExistingEngagementTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementTeam.setId(longCount.incrementAndGet());

        // Create the EngagementTeam
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, engagementTeamDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchEngagementTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementTeam.setId(longCount.incrementAndGet());

        // Create the EngagementTeam
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamEngagementTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        engagementTeam.setId(longCount.incrementAndGet());

        // Create the EngagementTeam
        EngagementTeamDTO engagementTeamDTO = engagementTeamMapper.toDto(engagementTeam);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(engagementTeamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the EngagementTeam in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteEngagementTeam() {
        // Initialize the database
        insertedEngagementTeam = engagementTeamRepository.save(engagementTeam).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the engagementTeam
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, engagementTeam.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return engagementTeamRepository.count().block();
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

    protected EngagementTeam getPersistedEngagementTeam(EngagementTeam engagementTeam) {
        return engagementTeamRepository.findById(engagementTeam.getId()).block();
    }

    protected void assertPersistedEngagementTeamToMatchAllProperties(EngagementTeam expectedEngagementTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEngagementTeamAllPropertiesEquals(expectedEngagementTeam, getPersistedEngagementTeam(expectedEngagementTeam));
        assertEngagementTeamUpdatableFieldsEquals(expectedEngagementTeam, getPersistedEngagementTeam(expectedEngagementTeam));
    }

    protected void assertPersistedEngagementTeamToMatchUpdatableProperties(EngagementTeam expectedEngagementTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertEngagementTeamAllUpdatablePropertiesEquals(expectedEngagementTeam, getPersistedEngagementTeam(expectedEngagementTeam));
        assertEngagementTeamUpdatableFieldsEquals(expectedEngagementTeam, getPersistedEngagementTeam(expectedEngagementTeam));
    }
}
