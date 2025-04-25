package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.TeamAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.enumeration.TeamVisibility;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.TeamRepository;
import com.senprojectbackend1.service.TeamService;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.mapper.TeamMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

/**
 * Integration tests for the {@link TeamResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TeamResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_LOGO = "AAAAAAAAAA";
    private static final String UPDATED_LOGO = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final TeamVisibility DEFAULT_VISIBILITY = TeamVisibility.VISIBLE;
    private static final TeamVisibility UPDATED_VISIBILITY = TeamVisibility.ARCHIVED;

    private static final Integer DEFAULT_TOTAL_LIKES = 1;
    private static final Integer UPDATED_TOTAL_LIKES = 2;
    private static final Integer SMALLER_TOTAL_LIKES = 1 - 1;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_UPDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/teams";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TeamRepository teamRepository;

    @Mock
    private TeamRepository teamRepositoryMock;

    @Autowired
    private TeamMapper teamMapper;

    @Mock
    private TeamService teamServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Team team;

    private Team insertedTeam;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createEntity() {
        return new Team()
            .name(DEFAULT_NAME)
            .description(DEFAULT_DESCRIPTION)
            .logo(DEFAULT_LOGO)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .visibility(DEFAULT_VISIBILITY)
            .totalLikes(DEFAULT_TOTAL_LIKES)
            .isDeleted(DEFAULT_IS_DELETED)
            .createdBy(DEFAULT_CREATED_BY)
            .lastUpdatedBy(DEFAULT_LAST_UPDATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Team createUpdatedEntity() {
        return new Team()
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .logo(UPDATED_LOGO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .visibility(UPDATED_VISIBILITY)
            .totalLikes(UPDATED_TOTAL_LIKES)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_team__members").block();
            em.deleteAll(Team.class).block();
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
        team = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTeam != null) {
            teamRepository.delete(insertedTeam).block();
            insertedTeam = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTeam() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);
        var returnedTeamDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TeamDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Team in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTeam = teamMapper.toEntity(returnedTeamDTO);
        assertTeamUpdatableFieldsEquals(returnedTeam, getPersistedTeam(returnedTeam));

        insertedTeam = returnedTeam;
    }

    @Test
    void createTeamWithExistingId() throws Exception {
        // Create the Team with an existing ID
        team.setId(1L);
        TeamDTO teamDTO = teamMapper.toDto(team);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        team.setName(null);

        // Create the Team, which fails.
        TeamDTO teamDTO = teamMapper.toDto(team);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        team.setCreatedAt(null);

        // Create the Team, which fails.
        TeamDTO teamDTO = teamMapper.toDto(team);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTeams() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList
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
            .value(hasItem(team.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].logo")
            .value(hasItem(DEFAULT_LOGO))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].visibility")
            .value(hasItem(DEFAULT_VISIBILITY.toString()))
            .jsonPath("$.[*].totalLikes")
            .value(hasItem(DEFAULT_TOTAL_LIKES))
            .jsonPath("$.[*].isDeleted")
            .value(hasItem(DEFAULT_IS_DELETED))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastUpdatedBy")
            .value(hasItem(DEFAULT_LAST_UPDATED_BY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamsWithEagerRelationshipsIsEnabled() {
        when(teamServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(teamServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllTeamsWithEagerRelationshipsIsNotEnabled() {
        when(teamServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(teamRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getTeam() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get the team
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, team.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(team.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.logo")
            .value(is(DEFAULT_LOGO))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.visibility")
            .value(is(DEFAULT_VISIBILITY.toString()))
            .jsonPath("$.totalLikes")
            .value(is(DEFAULT_TOTAL_LIKES))
            .jsonPath("$.isDeleted")
            .value(is(DEFAULT_IS_DELETED))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.lastUpdatedBy")
            .value(is(DEFAULT_LAST_UPDATED_BY));
    }

    @Test
    void getTeamsByIdFiltering() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        Long id = team.getId();

        defaultTeamFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTeamFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTeamFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTeamsByNameIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where name equals to
        defaultTeamFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllTeamsByNameIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where name in
        defaultTeamFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllTeamsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where name is not null
        defaultTeamFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllTeamsByNameContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where name contains
        defaultTeamFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllTeamsByNameNotContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where name does not contain
        defaultTeamFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllTeamsByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where description equals to
        defaultTeamFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllTeamsByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where description in
        defaultTeamFiltering("description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION, "description.in=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllTeamsByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where description is not null
        defaultTeamFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllTeamsByDescriptionContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where description contains
        defaultTeamFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllTeamsByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where description does not contain
        defaultTeamFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllTeamsByLogoIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where logo equals to
        defaultTeamFiltering("logo.equals=" + DEFAULT_LOGO, "logo.equals=" + UPDATED_LOGO);
    }

    @Test
    void getAllTeamsByLogoIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where logo in
        defaultTeamFiltering("logo.in=" + DEFAULT_LOGO + "," + UPDATED_LOGO, "logo.in=" + UPDATED_LOGO);
    }

    @Test
    void getAllTeamsByLogoIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where logo is not null
        defaultTeamFiltering("logo.specified=true", "logo.specified=false");
    }

    @Test
    void getAllTeamsByLogoContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where logo contains
        defaultTeamFiltering("logo.contains=" + DEFAULT_LOGO, "logo.contains=" + UPDATED_LOGO);
    }

    @Test
    void getAllTeamsByLogoNotContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where logo does not contain
        defaultTeamFiltering("logo.doesNotContain=" + UPDATED_LOGO, "logo.doesNotContain=" + DEFAULT_LOGO);
    }

    @Test
    void getAllTeamsByCreatedAtIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdAt equals to
        defaultTeamFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllTeamsByCreatedAtIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdAt in
        defaultTeamFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllTeamsByCreatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdAt is not null
        defaultTeamFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    void getAllTeamsByUpdatedAtIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where updatedAt equals to
        defaultTeamFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    void getAllTeamsByUpdatedAtIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where updatedAt in
        defaultTeamFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    void getAllTeamsByUpdatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where updatedAt is not null
        defaultTeamFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    void getAllTeamsByVisibilityIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where visibility equals to
        defaultTeamFiltering("visibility.equals=" + DEFAULT_VISIBILITY, "visibility.equals=" + UPDATED_VISIBILITY);
    }

    @Test
    void getAllTeamsByVisibilityIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where visibility in
        defaultTeamFiltering("visibility.in=" + DEFAULT_VISIBILITY + "," + UPDATED_VISIBILITY, "visibility.in=" + UPDATED_VISIBILITY);
    }

    @Test
    void getAllTeamsByVisibilityIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where visibility is not null
        defaultTeamFiltering("visibility.specified=true", "visibility.specified=false");
    }

    @Test
    void getAllTeamsByTotalLikesIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where totalLikes equals to
        defaultTeamFiltering("totalLikes.equals=" + DEFAULT_TOTAL_LIKES, "totalLikes.equals=" + UPDATED_TOTAL_LIKES);
    }

    @Test
    void getAllTeamsByTotalLikesIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where totalLikes in
        defaultTeamFiltering("totalLikes.in=" + DEFAULT_TOTAL_LIKES + "," + UPDATED_TOTAL_LIKES, "totalLikes.in=" + UPDATED_TOTAL_LIKES);
    }

    @Test
    void getAllTeamsByTotalLikesIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where totalLikes is not null
        defaultTeamFiltering("totalLikes.specified=true", "totalLikes.specified=false");
    }

    @Test
    void getAllTeamsByTotalLikesIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where totalLikes is greater than or equal to
        defaultTeamFiltering(
            "totalLikes.greaterThanOrEqual=" + DEFAULT_TOTAL_LIKES,
            "totalLikes.greaterThanOrEqual=" + UPDATED_TOTAL_LIKES
        );
    }

    @Test
    void getAllTeamsByTotalLikesIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where totalLikes is less than or equal to
        defaultTeamFiltering("totalLikes.lessThanOrEqual=" + DEFAULT_TOTAL_LIKES, "totalLikes.lessThanOrEqual=" + SMALLER_TOTAL_LIKES);
    }

    @Test
    void getAllTeamsByTotalLikesIsLessThanSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where totalLikes is less than
        defaultTeamFiltering("totalLikes.lessThan=" + UPDATED_TOTAL_LIKES, "totalLikes.lessThan=" + DEFAULT_TOTAL_LIKES);
    }

    @Test
    void getAllTeamsByTotalLikesIsGreaterThanSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where totalLikes is greater than
        defaultTeamFiltering("totalLikes.greaterThan=" + SMALLER_TOTAL_LIKES, "totalLikes.greaterThan=" + DEFAULT_TOTAL_LIKES);
    }

    @Test
    void getAllTeamsByIsDeletedIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where isDeleted equals to
        defaultTeamFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    void getAllTeamsByIsDeletedIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where isDeleted in
        defaultTeamFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    void getAllTeamsByIsDeletedIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where isDeleted is not null
        defaultTeamFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    void getAllTeamsByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdBy equals to
        defaultTeamFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllTeamsByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdBy in
        defaultTeamFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllTeamsByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdBy is not null
        defaultTeamFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllTeamsByCreatedByContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdBy contains
        defaultTeamFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllTeamsByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where createdBy does not contain
        defaultTeamFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllTeamsByLastUpdatedByIsEqualToSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where lastUpdatedBy equals to
        defaultTeamFiltering("lastUpdatedBy.equals=" + DEFAULT_LAST_UPDATED_BY, "lastUpdatedBy.equals=" + UPDATED_LAST_UPDATED_BY);
    }

    @Test
    void getAllTeamsByLastUpdatedByIsInShouldWork() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where lastUpdatedBy in
        defaultTeamFiltering(
            "lastUpdatedBy.in=" + DEFAULT_LAST_UPDATED_BY + "," + UPDATED_LAST_UPDATED_BY,
            "lastUpdatedBy.in=" + UPDATED_LAST_UPDATED_BY
        );
    }

    @Test
    void getAllTeamsByLastUpdatedByIsNullOrNotNull() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where lastUpdatedBy is not null
        defaultTeamFiltering("lastUpdatedBy.specified=true", "lastUpdatedBy.specified=false");
    }

    @Test
    void getAllTeamsByLastUpdatedByContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where lastUpdatedBy contains
        defaultTeamFiltering("lastUpdatedBy.contains=" + DEFAULT_LAST_UPDATED_BY, "lastUpdatedBy.contains=" + UPDATED_LAST_UPDATED_BY);
    }

    @Test
    void getAllTeamsByLastUpdatedByNotContainsSomething() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        // Get all the teamList where lastUpdatedBy does not contain
        defaultTeamFiltering(
            "lastUpdatedBy.doesNotContain=" + UPDATED_LAST_UPDATED_BY,
            "lastUpdatedBy.doesNotContain=" + DEFAULT_LAST_UPDATED_BY
        );
    }

    private void defaultTeamFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTeamShouldBeFound(shouldBeFound);
        defaultTeamShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTeamShouldBeFound(String filter) {
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
            .value(hasItem(team.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].logo")
            .value(hasItem(DEFAULT_LOGO))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].visibility")
            .value(hasItem(DEFAULT_VISIBILITY.toString()))
            .jsonPath("$.[*].totalLikes")
            .value(hasItem(DEFAULT_TOTAL_LIKES))
            .jsonPath("$.[*].isDeleted")
            .value(hasItem(DEFAULT_IS_DELETED))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastUpdatedBy")
            .value(hasItem(DEFAULT_LAST_UPDATED_BY));

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
    private void defaultTeamShouldNotBeFound(String filter) {
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
    void getNonExistingTeam() {
        // Get the team
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTeam() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team
        Team updatedTeam = teamRepository.findById(team.getId()).block();
        updatedTeam
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .logo(UPDATED_LOGO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .visibility(UPDATED_VISIBILITY)
            .totalLikes(UPDATED_TOTAL_LIKES)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);
        TeamDTO teamDTO = teamMapper.toDto(updatedTeam);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTeamToMatchAllProperties(updatedTeam);
    }

    @Test
    void putNonExistingTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, teamDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam
            .description(UPDATED_DESCRIPTION)
            .updatedAt(UPDATED_UPDATED_AT)
            .visibility(UPDATED_VISIBILITY)
            .totalLikes(UPDATED_TOTAL_LIKES)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Team in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTeam, team), getPersistedTeam(team));
    }

    @Test
    void fullUpdateTeamWithPatch() throws Exception {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the team using partial update
        Team partialUpdatedTeam = new Team();
        partialUpdatedTeam.setId(team.getId());

        partialUpdatedTeam
            .name(UPDATED_NAME)
            .description(UPDATED_DESCRIPTION)
            .logo(UPDATED_LOGO)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .visibility(UPDATED_VISIBILITY)
            .totalLikes(UPDATED_TOTAL_LIKES)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTeam.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTeam))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Team in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTeamUpdatableFieldsEquals(partialUpdatedTeam, getPersistedTeam(partialUpdatedTeam));
    }

    @Test
    void patchNonExistingTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, teamDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTeam() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        team.setId(longCount.incrementAndGet());

        // Create the Team
        TeamDTO teamDTO = teamMapper.toDto(team);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(teamDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Team in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTeam() {
        // Initialize the database
        insertedTeam = teamRepository.save(team).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the team
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, team.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return teamRepository.count().block();
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

    protected Team getPersistedTeam(Team team) {
        return teamRepository.findById(team.getId()).block();
    }

    protected void assertPersistedTeamToMatchAllProperties(Team expectedTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamAllPropertiesEquals(expectedTeam, getPersistedTeam(expectedTeam));
        assertTeamUpdatableFieldsEquals(expectedTeam, getPersistedTeam(expectedTeam));
    }

    protected void assertPersistedTeamToMatchUpdatableProperties(Team expectedTeam) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTeamAllUpdatablePropertiesEquals(expectedTeam, getPersistedTeam(expectedTeam));
        assertTeamUpdatableFieldsEquals(expectedTeam, getPersistedTeam(expectedTeam));
    }
}
