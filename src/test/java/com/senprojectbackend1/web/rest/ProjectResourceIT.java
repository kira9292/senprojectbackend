package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.ProjectAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.domain.enumeration.ProjectType;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.repository.TeamRepository;
import com.senprojectbackend1.service.ProjectService;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.mapper.ProjectMapper;
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
 * Integration tests for the {@link ProjectResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class ProjectResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_SHOWCASE = "AAAAAAAAAA";
    private static final String UPDATED_SHOWCASE = "BBBBBBBBBB";

    private static final ProjectStatus DEFAULT_STATUS = ProjectStatus.PLANNING;
    private static final ProjectStatus UPDATED_STATUS = ProjectStatus.WAITING_VALIDATION;

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_GITHUB_URL = "AAAAAAAAAA";
    private static final String UPDATED_GITHUB_URL = "BBBBBBBBBB";

    private static final String DEFAULT_WEBSITE_URL = "AAAAAAAAAA";
    private static final String UPDATED_WEBSITE_URL = "BBBBBBBBBB";

    private static final String DEFAULT_DEMO_URL = "AAAAAAAAAA";
    private static final String UPDATED_DEMO_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_OPEN_TO_COLLABORATION = false;
    private static final Boolean UPDATED_OPEN_TO_COLLABORATION = true;

    private static final Boolean DEFAULT_OPEN_TO_FUNDING = false;
    private static final Boolean UPDATED_OPEN_TO_FUNDING = true;

    private static final ProjectType DEFAULT_TYPE = ProjectType.ENTREPRISE;
    private static final ProjectType UPDATED_TYPE = ProjectType.STARTUP;

    private static final Integer DEFAULT_TOTAL_LIKES = 1;
    private static final Integer UPDATED_TOTAL_LIKES = 2;
    private static final Integer SMALLER_TOTAL_LIKES = 1 - 1;

    private static final Integer DEFAULT_TOTAL_SHARES = 1;
    private static final Integer UPDATED_TOTAL_SHARES = 2;
    private static final Integer SMALLER_TOTAL_SHARES = 1 - 1;

    private static final Integer DEFAULT_TOTAL_VIEWS = 1;
    private static final Integer UPDATED_TOTAL_VIEWS = 2;
    private static final Integer SMALLER_TOTAL_VIEWS = 1 - 1;

    private static final Integer DEFAULT_TOTAL_COMMENTS = 1;
    private static final Integer UPDATED_TOTAL_COMMENTS = 2;
    private static final Integer SMALLER_TOTAL_COMMENTS = 1 - 1;

    private static final Integer DEFAULT_TOTAL_FAVORITES = 1;
    private static final Integer UPDATED_TOTAL_FAVORITES = 2;
    private static final Integer SMALLER_TOTAL_FAVORITES = 1 - 1;

    private static final Boolean DEFAULT_IS_DELETED = false;
    private static final Boolean UPDATED_IS_DELETED = true;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_UPDATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_UPDATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/projects";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ProjectRepository projectRepository;

    @Mock
    private ProjectRepository projectRepositoryMock;

    @Autowired
    private ProjectMapper projectMapper;

    @Mock
    private ProjectService projectServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Project project;

    private Project insertedProject;

    @Autowired
    private TeamRepository teamRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Project createEntity() {
        return new Project()
            .title(DEFAULT_TITLE)
            .description(DEFAULT_DESCRIPTION)
            .showcase(DEFAULT_SHOWCASE)
            .status(DEFAULT_STATUS)
            .createdAt(DEFAULT_CREATED_AT)
            .updatedAt(DEFAULT_UPDATED_AT)
            .githubUrl(DEFAULT_GITHUB_URL)
            .websiteUrl(DEFAULT_WEBSITE_URL)
            .demoUrl(DEFAULT_DEMO_URL)
            .openToCollaboration(DEFAULT_OPEN_TO_COLLABORATION)
            .openToFunding(DEFAULT_OPEN_TO_FUNDING)
            .type(DEFAULT_TYPE)
            .totalLikes(DEFAULT_TOTAL_LIKES)
            .totalShares(DEFAULT_TOTAL_SHARES)
            .totalViews(DEFAULT_TOTAL_VIEWS)
            .totalComments(DEFAULT_TOTAL_COMMENTS)
            .totalFavorites(DEFAULT_TOTAL_FAVORITES)
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
    public static Project createUpdatedEntity() {
        return new Project()
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .showcase(UPDATED_SHOWCASE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .githubUrl(UPDATED_GITHUB_URL)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .demoUrl(UPDATED_DEMO_URL)
            .openToCollaboration(UPDATED_OPEN_TO_COLLABORATION)
            .openToFunding(UPDATED_OPEN_TO_FUNDING)
            .type(UPDATED_TYPE)
            .totalLikes(UPDATED_TOTAL_LIKES)
            .totalShares(UPDATED_TOTAL_SHARES)
            .totalViews(UPDATED_TOTAL_VIEWS)
            .totalComments(UPDATED_TOTAL_COMMENTS)
            .totalFavorites(UPDATED_TOTAL_FAVORITES)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_project__favoritedby").block();
            em.deleteAll("rel_project__tags").block();
            em.deleteAll(Project.class).block();
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
        project = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedProject != null) {
            projectRepository.delete(insertedProject).block();
            insertedProject = null;
        }
        deleteEntities(em);
    }

    @Test
    void createProject() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);
        var returnedProjectDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(ProjectDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Project in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedProject = projectMapper.toEntity(returnedProjectDTO);
        assertProjectUpdatableFieldsEquals(returnedProject, getPersistedProject(returnedProject));

        insertedProject = returnedProject;
    }

    @Test
    void createProjectWithExistingId() throws Exception {
        // Create the Project with an existing ID
        project.setId(1L);
        ProjectDTO projectDTO = projectMapper.toDto(project);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setTitle(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkDescriptionIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setDescription(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setStatus(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        project.setCreatedAt(null);

        // Create the Project, which fails.
        ProjectDTO projectDTO = projectMapper.toDto(project);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllProjects() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList
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
            .value(hasItem(project.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].showcase")
            .value(hasItem(DEFAULT_SHOWCASE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].githubUrl")
            .value(hasItem(DEFAULT_GITHUB_URL))
            .jsonPath("$.[*].websiteUrl")
            .value(hasItem(DEFAULT_WEBSITE_URL))
            .jsonPath("$.[*].demoUrl")
            .value(hasItem(DEFAULT_DEMO_URL))
            .jsonPath("$.[*].openToCollaboration")
            .value(hasItem(DEFAULT_OPEN_TO_COLLABORATION))
            .jsonPath("$.[*].openToFunding")
            .value(hasItem(DEFAULT_OPEN_TO_FUNDING))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].totalLikes")
            .value(hasItem(DEFAULT_TOTAL_LIKES))
            .jsonPath("$.[*].totalShares")
            .value(hasItem(DEFAULT_TOTAL_SHARES))
            .jsonPath("$.[*].totalViews")
            .value(hasItem(DEFAULT_TOTAL_VIEWS))
            .jsonPath("$.[*].totalComments")
            .value(hasItem(DEFAULT_TOTAL_COMMENTS))
            .jsonPath("$.[*].totalFavorites")
            .value(hasItem(DEFAULT_TOTAL_FAVORITES))
            .jsonPath("$.[*].isDeleted")
            .value(hasItem(DEFAULT_IS_DELETED))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].lastUpdatedBy")
            .value(hasItem(DEFAULT_LAST_UPDATED_BY));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectsWithEagerRelationshipsIsEnabled() {
        when(projectServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(projectServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllProjectsWithEagerRelationshipsIsNotEnabled() {
        when(projectServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(projectRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getProject() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get the project
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, project.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(project.getId().intValue()))
            .jsonPath("$.title")
            .value(is(DEFAULT_TITLE))
            .jsonPath("$.description")
            .value(is(DEFAULT_DESCRIPTION))
            .jsonPath("$.showcase")
            .value(is(DEFAULT_SHOWCASE))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.githubUrl")
            .value(is(DEFAULT_GITHUB_URL))
            .jsonPath("$.websiteUrl")
            .value(is(DEFAULT_WEBSITE_URL))
            .jsonPath("$.demoUrl")
            .value(is(DEFAULT_DEMO_URL))
            .jsonPath("$.openToCollaboration")
            .value(is(DEFAULT_OPEN_TO_COLLABORATION))
            .jsonPath("$.openToFunding")
            .value(is(DEFAULT_OPEN_TO_FUNDING))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.totalLikes")
            .value(is(DEFAULT_TOTAL_LIKES))
            .jsonPath("$.totalShares")
            .value(is(DEFAULT_TOTAL_SHARES))
            .jsonPath("$.totalViews")
            .value(is(DEFAULT_TOTAL_VIEWS))
            .jsonPath("$.totalComments")
            .value(is(DEFAULT_TOTAL_COMMENTS))
            .jsonPath("$.totalFavorites")
            .value(is(DEFAULT_TOTAL_FAVORITES))
            .jsonPath("$.isDeleted")
            .value(is(DEFAULT_IS_DELETED))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.lastUpdatedBy")
            .value(is(DEFAULT_LAST_UPDATED_BY));
    }

    @Test
    void getProjectsByIdFiltering() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        Long id = project.getId();

        defaultProjectFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultProjectFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultProjectFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllProjectsByTitleIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where title equals to
        defaultProjectFiltering("title.equals=" + DEFAULT_TITLE, "title.equals=" + UPDATED_TITLE);
    }

    @Test
    void getAllProjectsByTitleIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where title in
        defaultProjectFiltering("title.in=" + DEFAULT_TITLE + "," + UPDATED_TITLE, "title.in=" + UPDATED_TITLE);
    }

    @Test
    void getAllProjectsByTitleIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where title is not null
        defaultProjectFiltering("title.specified=true", "title.specified=false");
    }

    @Test
    void getAllProjectsByTitleContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where title contains
        defaultProjectFiltering("title.contains=" + DEFAULT_TITLE, "title.contains=" + UPDATED_TITLE);
    }

    @Test
    void getAllProjectsByTitleNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where title does not contain
        defaultProjectFiltering("title.doesNotContain=" + UPDATED_TITLE, "title.doesNotContain=" + DEFAULT_TITLE);
    }

    @Test
    void getAllProjectsByDescriptionIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where description equals to
        defaultProjectFiltering("description.equals=" + DEFAULT_DESCRIPTION, "description.equals=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllProjectsByDescriptionIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where description in
        defaultProjectFiltering(
            "description.in=" + DEFAULT_DESCRIPTION + "," + UPDATED_DESCRIPTION,
            "description.in=" + UPDATED_DESCRIPTION
        );
    }

    @Test
    void getAllProjectsByDescriptionIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where description is not null
        defaultProjectFiltering("description.specified=true", "description.specified=false");
    }

    @Test
    void getAllProjectsByDescriptionContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where description contains
        defaultProjectFiltering("description.contains=" + DEFAULT_DESCRIPTION, "description.contains=" + UPDATED_DESCRIPTION);
    }

    @Test
    void getAllProjectsByDescriptionNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where description does not contain
        defaultProjectFiltering("description.doesNotContain=" + UPDATED_DESCRIPTION, "description.doesNotContain=" + DEFAULT_DESCRIPTION);
    }

    @Test
    void getAllProjectsByShowcaseIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where showcase equals to
        defaultProjectFiltering("showcase.equals=" + DEFAULT_SHOWCASE, "showcase.equals=" + UPDATED_SHOWCASE);
    }

    @Test
    void getAllProjectsByShowcaseIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where showcase in
        defaultProjectFiltering("showcase.in=" + DEFAULT_SHOWCASE + "," + UPDATED_SHOWCASE, "showcase.in=" + UPDATED_SHOWCASE);
    }

    @Test
    void getAllProjectsByShowcaseIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where showcase is not null
        defaultProjectFiltering("showcase.specified=true", "showcase.specified=false");
    }

    @Test
    void getAllProjectsByShowcaseContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where showcase contains
        defaultProjectFiltering("showcase.contains=" + DEFAULT_SHOWCASE, "showcase.contains=" + UPDATED_SHOWCASE);
    }

    @Test
    void getAllProjectsByShowcaseNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where showcase does not contain
        defaultProjectFiltering("showcase.doesNotContain=" + UPDATED_SHOWCASE, "showcase.doesNotContain=" + DEFAULT_SHOWCASE);
    }

    @Test
    void getAllProjectsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where status equals to
        defaultProjectFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllProjectsByStatusIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where status in
        defaultProjectFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllProjectsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where status is not null
        defaultProjectFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllProjectsByCreatedAtIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdAt equals to
        defaultProjectFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllProjectsByCreatedAtIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdAt in
        defaultProjectFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllProjectsByCreatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdAt is not null
        defaultProjectFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    void getAllProjectsByUpdatedAtIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where updatedAt equals to
        defaultProjectFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    void getAllProjectsByUpdatedAtIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where updatedAt in
        defaultProjectFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    void getAllProjectsByUpdatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where updatedAt is not null
        defaultProjectFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    void getAllProjectsByGithubUrlIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where githubUrl equals to
        defaultProjectFiltering("githubUrl.equals=" + DEFAULT_GITHUB_URL, "githubUrl.equals=" + UPDATED_GITHUB_URL);
    }

    @Test
    void getAllProjectsByGithubUrlIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where githubUrl in
        defaultProjectFiltering("githubUrl.in=" + DEFAULT_GITHUB_URL + "," + UPDATED_GITHUB_URL, "githubUrl.in=" + UPDATED_GITHUB_URL);
    }

    @Test
    void getAllProjectsByGithubUrlIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where githubUrl is not null
        defaultProjectFiltering("githubUrl.specified=true", "githubUrl.specified=false");
    }

    @Test
    void getAllProjectsByGithubUrlContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where githubUrl contains
        defaultProjectFiltering("githubUrl.contains=" + DEFAULT_GITHUB_URL, "githubUrl.contains=" + UPDATED_GITHUB_URL);
    }

    @Test
    void getAllProjectsByGithubUrlNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where githubUrl does not contain
        defaultProjectFiltering("githubUrl.doesNotContain=" + UPDATED_GITHUB_URL, "githubUrl.doesNotContain=" + DEFAULT_GITHUB_URL);
    }

    @Test
    void getAllProjectsByWebsiteUrlIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where websiteUrl equals to
        defaultProjectFiltering("websiteUrl.equals=" + DEFAULT_WEBSITE_URL, "websiteUrl.equals=" + UPDATED_WEBSITE_URL);
    }

    @Test
    void getAllProjectsByWebsiteUrlIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where websiteUrl in
        defaultProjectFiltering("websiteUrl.in=" + DEFAULT_WEBSITE_URL + "," + UPDATED_WEBSITE_URL, "websiteUrl.in=" + UPDATED_WEBSITE_URL);
    }

    @Test
    void getAllProjectsByWebsiteUrlIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where websiteUrl is not null
        defaultProjectFiltering("websiteUrl.specified=true", "websiteUrl.specified=false");
    }

    @Test
    void getAllProjectsByWebsiteUrlContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where websiteUrl contains
        defaultProjectFiltering("websiteUrl.contains=" + DEFAULT_WEBSITE_URL, "websiteUrl.contains=" + UPDATED_WEBSITE_URL);
    }

    @Test
    void getAllProjectsByWebsiteUrlNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where websiteUrl does not contain
        defaultProjectFiltering("websiteUrl.doesNotContain=" + UPDATED_WEBSITE_URL, "websiteUrl.doesNotContain=" + DEFAULT_WEBSITE_URL);
    }

    @Test
    void getAllProjectsByDemoUrlIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where demoUrl equals to
        defaultProjectFiltering("demoUrl.equals=" + DEFAULT_DEMO_URL, "demoUrl.equals=" + UPDATED_DEMO_URL);
    }

    @Test
    void getAllProjectsByDemoUrlIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where demoUrl in
        defaultProjectFiltering("demoUrl.in=" + DEFAULT_DEMO_URL + "," + UPDATED_DEMO_URL, "demoUrl.in=" + UPDATED_DEMO_URL);
    }

    @Test
    void getAllProjectsByDemoUrlIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where demoUrl is not null
        defaultProjectFiltering("demoUrl.specified=true", "demoUrl.specified=false");
    }

    @Test
    void getAllProjectsByDemoUrlContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where demoUrl contains
        defaultProjectFiltering("demoUrl.contains=" + DEFAULT_DEMO_URL, "demoUrl.contains=" + UPDATED_DEMO_URL);
    }

    @Test
    void getAllProjectsByDemoUrlNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where demoUrl does not contain
        defaultProjectFiltering("demoUrl.doesNotContain=" + UPDATED_DEMO_URL, "demoUrl.doesNotContain=" + DEFAULT_DEMO_URL);
    }

    @Test
    void getAllProjectsByOpenToCollaborationIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where openToCollaboration equals to
        defaultProjectFiltering(
            "openToCollaboration.equals=" + DEFAULT_OPEN_TO_COLLABORATION,
            "openToCollaboration.equals=" + UPDATED_OPEN_TO_COLLABORATION
        );
    }

    @Test
    void getAllProjectsByOpenToCollaborationIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where openToCollaboration in
        defaultProjectFiltering(
            "openToCollaboration.in=" + DEFAULT_OPEN_TO_COLLABORATION + "," + UPDATED_OPEN_TO_COLLABORATION,
            "openToCollaboration.in=" + UPDATED_OPEN_TO_COLLABORATION
        );
    }

    @Test
    void getAllProjectsByOpenToCollaborationIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where openToCollaboration is not null
        defaultProjectFiltering("openToCollaboration.specified=true", "openToCollaboration.specified=false");
    }

    @Test
    void getAllProjectsByOpenToFundingIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where openToFunding equals to
        defaultProjectFiltering("openToFunding.equals=" + DEFAULT_OPEN_TO_FUNDING, "openToFunding.equals=" + UPDATED_OPEN_TO_FUNDING);
    }

    @Test
    void getAllProjectsByOpenToFundingIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where openToFunding in
        defaultProjectFiltering(
            "openToFunding.in=" + DEFAULT_OPEN_TO_FUNDING + "," + UPDATED_OPEN_TO_FUNDING,
            "openToFunding.in=" + UPDATED_OPEN_TO_FUNDING
        );
    }

    @Test
    void getAllProjectsByOpenToFundingIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where openToFunding is not null
        defaultProjectFiltering("openToFunding.specified=true", "openToFunding.specified=false");
    }

    @Test
    void getAllProjectsByTypeIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where type equals to
        defaultProjectFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    void getAllProjectsByTypeIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where type in
        defaultProjectFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    void getAllProjectsByTypeIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where type is not null
        defaultProjectFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    void getAllProjectsByTotalLikesIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalLikes equals to
        defaultProjectFiltering("totalLikes.equals=" + DEFAULT_TOTAL_LIKES, "totalLikes.equals=" + UPDATED_TOTAL_LIKES);
    }

    @Test
    void getAllProjectsByTotalLikesIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalLikes in
        defaultProjectFiltering("totalLikes.in=" + DEFAULT_TOTAL_LIKES + "," + UPDATED_TOTAL_LIKES, "totalLikes.in=" + UPDATED_TOTAL_LIKES);
    }

    @Test
    void getAllProjectsByTotalLikesIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalLikes is not null
        defaultProjectFiltering("totalLikes.specified=true", "totalLikes.specified=false");
    }

    @Test
    void getAllProjectsByTotalLikesIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalLikes is greater than or equal to
        defaultProjectFiltering(
            "totalLikes.greaterThanOrEqual=" + DEFAULT_TOTAL_LIKES,
            "totalLikes.greaterThanOrEqual=" + UPDATED_TOTAL_LIKES
        );
    }

    @Test
    void getAllProjectsByTotalLikesIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalLikes is less than or equal to
        defaultProjectFiltering("totalLikes.lessThanOrEqual=" + DEFAULT_TOTAL_LIKES, "totalLikes.lessThanOrEqual=" + SMALLER_TOTAL_LIKES);
    }

    @Test
    void getAllProjectsByTotalLikesIsLessThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalLikes is less than
        defaultProjectFiltering("totalLikes.lessThan=" + UPDATED_TOTAL_LIKES, "totalLikes.lessThan=" + DEFAULT_TOTAL_LIKES);
    }

    @Test
    void getAllProjectsByTotalLikesIsGreaterThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalLikes is greater than
        defaultProjectFiltering("totalLikes.greaterThan=" + SMALLER_TOTAL_LIKES, "totalLikes.greaterThan=" + DEFAULT_TOTAL_LIKES);
    }

    @Test
    void getAllProjectsByTotalSharesIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalShares equals to
        defaultProjectFiltering("totalShares.equals=" + DEFAULT_TOTAL_SHARES, "totalShares.equals=" + UPDATED_TOTAL_SHARES);
    }

    @Test
    void getAllProjectsByTotalSharesIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalShares in
        defaultProjectFiltering(
            "totalShares.in=" + DEFAULT_TOTAL_SHARES + "," + UPDATED_TOTAL_SHARES,
            "totalShares.in=" + UPDATED_TOTAL_SHARES
        );
    }

    @Test
    void getAllProjectsByTotalSharesIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalShares is not null
        defaultProjectFiltering("totalShares.specified=true", "totalShares.specified=false");
    }

    @Test
    void getAllProjectsByTotalSharesIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalShares is greater than or equal to
        defaultProjectFiltering(
            "totalShares.greaterThanOrEqual=" + DEFAULT_TOTAL_SHARES,
            "totalShares.greaterThanOrEqual=" + UPDATED_TOTAL_SHARES
        );
    }

    @Test
    void getAllProjectsByTotalSharesIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalShares is less than or equal to
        defaultProjectFiltering(
            "totalShares.lessThanOrEqual=" + DEFAULT_TOTAL_SHARES,
            "totalShares.lessThanOrEqual=" + SMALLER_TOTAL_SHARES
        );
    }

    @Test
    void getAllProjectsByTotalSharesIsLessThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalShares is less than
        defaultProjectFiltering("totalShares.lessThan=" + UPDATED_TOTAL_SHARES, "totalShares.lessThan=" + DEFAULT_TOTAL_SHARES);
    }

    @Test
    void getAllProjectsByTotalSharesIsGreaterThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalShares is greater than
        defaultProjectFiltering("totalShares.greaterThan=" + SMALLER_TOTAL_SHARES, "totalShares.greaterThan=" + DEFAULT_TOTAL_SHARES);
    }

    @Test
    void getAllProjectsByTotalViewsIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalViews equals to
        defaultProjectFiltering("totalViews.equals=" + DEFAULT_TOTAL_VIEWS, "totalViews.equals=" + UPDATED_TOTAL_VIEWS);
    }

    @Test
    void getAllProjectsByTotalViewsIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalViews in
        defaultProjectFiltering("totalViews.in=" + DEFAULT_TOTAL_VIEWS + "," + UPDATED_TOTAL_VIEWS, "totalViews.in=" + UPDATED_TOTAL_VIEWS);
    }

    @Test
    void getAllProjectsByTotalViewsIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalViews is not null
        defaultProjectFiltering("totalViews.specified=true", "totalViews.specified=false");
    }

    @Test
    void getAllProjectsByTotalViewsIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalViews is greater than or equal to
        defaultProjectFiltering(
            "totalViews.greaterThanOrEqual=" + DEFAULT_TOTAL_VIEWS,
            "totalViews.greaterThanOrEqual=" + UPDATED_TOTAL_VIEWS
        );
    }

    @Test
    void getAllProjectsByTotalViewsIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalViews is less than or equal to
        defaultProjectFiltering("totalViews.lessThanOrEqual=" + DEFAULT_TOTAL_VIEWS, "totalViews.lessThanOrEqual=" + SMALLER_TOTAL_VIEWS);
    }

    @Test
    void getAllProjectsByTotalViewsIsLessThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalViews is less than
        defaultProjectFiltering("totalViews.lessThan=" + UPDATED_TOTAL_VIEWS, "totalViews.lessThan=" + DEFAULT_TOTAL_VIEWS);
    }

    @Test
    void getAllProjectsByTotalViewsIsGreaterThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalViews is greater than
        defaultProjectFiltering("totalViews.greaterThan=" + SMALLER_TOTAL_VIEWS, "totalViews.greaterThan=" + DEFAULT_TOTAL_VIEWS);
    }

    @Test
    void getAllProjectsByTotalCommentsIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalComments equals to
        defaultProjectFiltering("totalComments.equals=" + DEFAULT_TOTAL_COMMENTS, "totalComments.equals=" + UPDATED_TOTAL_COMMENTS);
    }

    @Test
    void getAllProjectsByTotalCommentsIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalComments in
        defaultProjectFiltering(
            "totalComments.in=" + DEFAULT_TOTAL_COMMENTS + "," + UPDATED_TOTAL_COMMENTS,
            "totalComments.in=" + UPDATED_TOTAL_COMMENTS
        );
    }

    @Test
    void getAllProjectsByTotalCommentsIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalComments is not null
        defaultProjectFiltering("totalComments.specified=true", "totalComments.specified=false");
    }

    @Test
    void getAllProjectsByTotalCommentsIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalComments is greater than or equal to
        defaultProjectFiltering(
            "totalComments.greaterThanOrEqual=" + DEFAULT_TOTAL_COMMENTS,
            "totalComments.greaterThanOrEqual=" + UPDATED_TOTAL_COMMENTS
        );
    }

    @Test
    void getAllProjectsByTotalCommentsIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalComments is less than or equal to
        defaultProjectFiltering(
            "totalComments.lessThanOrEqual=" + DEFAULT_TOTAL_COMMENTS,
            "totalComments.lessThanOrEqual=" + SMALLER_TOTAL_COMMENTS
        );
    }

    @Test
    void getAllProjectsByTotalCommentsIsLessThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalComments is less than
        defaultProjectFiltering("totalComments.lessThan=" + UPDATED_TOTAL_COMMENTS, "totalComments.lessThan=" + DEFAULT_TOTAL_COMMENTS);
    }

    @Test
    void getAllProjectsByTotalCommentsIsGreaterThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalComments is greater than
        defaultProjectFiltering(
            "totalComments.greaterThan=" + SMALLER_TOTAL_COMMENTS,
            "totalComments.greaterThan=" + DEFAULT_TOTAL_COMMENTS
        );
    }

    @Test
    void getAllProjectsByTotalFavoritesIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalFavorites equals to
        defaultProjectFiltering("totalFavorites.equals=" + DEFAULT_TOTAL_FAVORITES, "totalFavorites.equals=" + UPDATED_TOTAL_FAVORITES);
    }

    @Test
    void getAllProjectsByTotalFavoritesIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalFavorites in
        defaultProjectFiltering(
            "totalFavorites.in=" + DEFAULT_TOTAL_FAVORITES + "," + UPDATED_TOTAL_FAVORITES,
            "totalFavorites.in=" + UPDATED_TOTAL_FAVORITES
        );
    }

    @Test
    void getAllProjectsByTotalFavoritesIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalFavorites is not null
        defaultProjectFiltering("totalFavorites.specified=true", "totalFavorites.specified=false");
    }

    @Test
    void getAllProjectsByTotalFavoritesIsGreaterThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalFavorites is greater than or equal to
        defaultProjectFiltering(
            "totalFavorites.greaterThanOrEqual=" + DEFAULT_TOTAL_FAVORITES,
            "totalFavorites.greaterThanOrEqual=" + UPDATED_TOTAL_FAVORITES
        );
    }

    @Test
    void getAllProjectsByTotalFavoritesIsLessThanOrEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalFavorites is less than or equal to
        defaultProjectFiltering(
            "totalFavorites.lessThanOrEqual=" + DEFAULT_TOTAL_FAVORITES,
            "totalFavorites.lessThanOrEqual=" + SMALLER_TOTAL_FAVORITES
        );
    }

    @Test
    void getAllProjectsByTotalFavoritesIsLessThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalFavorites is less than
        defaultProjectFiltering("totalFavorites.lessThan=" + UPDATED_TOTAL_FAVORITES, "totalFavorites.lessThan=" + DEFAULT_TOTAL_FAVORITES);
    }

    @Test
    void getAllProjectsByTotalFavoritesIsGreaterThanSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where totalFavorites is greater than
        defaultProjectFiltering(
            "totalFavorites.greaterThan=" + SMALLER_TOTAL_FAVORITES,
            "totalFavorites.greaterThan=" + DEFAULT_TOTAL_FAVORITES
        );
    }

    @Test
    void getAllProjectsByIsDeletedIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where isDeleted equals to
        defaultProjectFiltering("isDeleted.equals=" + DEFAULT_IS_DELETED, "isDeleted.equals=" + UPDATED_IS_DELETED);
    }

    @Test
    void getAllProjectsByIsDeletedIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where isDeleted in
        defaultProjectFiltering("isDeleted.in=" + DEFAULT_IS_DELETED + "," + UPDATED_IS_DELETED, "isDeleted.in=" + UPDATED_IS_DELETED);
    }

    @Test
    void getAllProjectsByIsDeletedIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where isDeleted is not null
        defaultProjectFiltering("isDeleted.specified=true", "isDeleted.specified=false");
    }

    @Test
    void getAllProjectsByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdBy equals to
        defaultProjectFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllProjectsByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdBy in
        defaultProjectFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllProjectsByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdBy is not null
        defaultProjectFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllProjectsByCreatedByContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdBy contains
        defaultProjectFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllProjectsByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where createdBy does not contain
        defaultProjectFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllProjectsByLastUpdatedByIsEqualToSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where lastUpdatedBy equals to
        defaultProjectFiltering("lastUpdatedBy.equals=" + DEFAULT_LAST_UPDATED_BY, "lastUpdatedBy.equals=" + UPDATED_LAST_UPDATED_BY);
    }

    @Test
    void getAllProjectsByLastUpdatedByIsInShouldWork() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where lastUpdatedBy in
        defaultProjectFiltering(
            "lastUpdatedBy.in=" + DEFAULT_LAST_UPDATED_BY + "," + UPDATED_LAST_UPDATED_BY,
            "lastUpdatedBy.in=" + UPDATED_LAST_UPDATED_BY
        );
    }

    @Test
    void getAllProjectsByLastUpdatedByIsNullOrNotNull() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where lastUpdatedBy is not null
        defaultProjectFiltering("lastUpdatedBy.specified=true", "lastUpdatedBy.specified=false");
    }

    @Test
    void getAllProjectsByLastUpdatedByContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where lastUpdatedBy contains
        defaultProjectFiltering("lastUpdatedBy.contains=" + DEFAULT_LAST_UPDATED_BY, "lastUpdatedBy.contains=" + UPDATED_LAST_UPDATED_BY);
    }

    @Test
    void getAllProjectsByLastUpdatedByNotContainsSomething() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        // Get all the projectList where lastUpdatedBy does not contain
        defaultProjectFiltering(
            "lastUpdatedBy.doesNotContain=" + UPDATED_LAST_UPDATED_BY,
            "lastUpdatedBy.doesNotContain=" + DEFAULT_LAST_UPDATED_BY
        );
    }

    @Test
    void getAllProjectsByTeamIsEqualToSomething() {
        Team team = TeamResourceIT.createEntity();
        teamRepository.save(team).block();
        Long teamId = team.getId();
        project.setTeamId(teamId);
        insertedProject = projectRepository.save(project).block();
        // Get all the projectList where team equals to teamId
        defaultProjectShouldBeFound("teamId.equals=" + teamId);

        // Get all the projectList where team equals to (teamId + 1)
        defaultProjectShouldNotBeFound("teamId.equals=" + (teamId + 1));
    }

    private void defaultProjectFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultProjectShouldBeFound(shouldBeFound);
        defaultProjectShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultProjectShouldBeFound(String filter) {
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
            .value(hasItem(project.getId().intValue()))
            .jsonPath("$.[*].title")
            .value(hasItem(DEFAULT_TITLE))
            .jsonPath("$.[*].description")
            .value(hasItem(DEFAULT_DESCRIPTION))
            .jsonPath("$.[*].showcase")
            .value(hasItem(DEFAULT_SHOWCASE))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].githubUrl")
            .value(hasItem(DEFAULT_GITHUB_URL))
            .jsonPath("$.[*].websiteUrl")
            .value(hasItem(DEFAULT_WEBSITE_URL))
            .jsonPath("$.[*].demoUrl")
            .value(hasItem(DEFAULT_DEMO_URL))
            .jsonPath("$.[*].openToCollaboration")
            .value(hasItem(DEFAULT_OPEN_TO_COLLABORATION))
            .jsonPath("$.[*].openToFunding")
            .value(hasItem(DEFAULT_OPEN_TO_FUNDING))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].totalLikes")
            .value(hasItem(DEFAULT_TOTAL_LIKES))
            .jsonPath("$.[*].totalShares")
            .value(hasItem(DEFAULT_TOTAL_SHARES))
            .jsonPath("$.[*].totalViews")
            .value(hasItem(DEFAULT_TOTAL_VIEWS))
            .jsonPath("$.[*].totalComments")
            .value(hasItem(DEFAULT_TOTAL_COMMENTS))
            .jsonPath("$.[*].totalFavorites")
            .value(hasItem(DEFAULT_TOTAL_FAVORITES))
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
    private void defaultProjectShouldNotBeFound(String filter) {
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
    void getNonExistingProject() {
        // Get the project
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingProject() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project
        Project updatedProject = projectRepository.findById(project.getId()).block();
        updatedProject
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .showcase(UPDATED_SHOWCASE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .githubUrl(UPDATED_GITHUB_URL)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .demoUrl(UPDATED_DEMO_URL)
            .openToCollaboration(UPDATED_OPEN_TO_COLLABORATION)
            .openToFunding(UPDATED_OPEN_TO_FUNDING)
            .type(UPDATED_TYPE)
            .totalLikes(UPDATED_TOTAL_LIKES)
            .totalShares(UPDATED_TOTAL_SHARES)
            .totalViews(UPDATED_TOTAL_VIEWS)
            .totalComments(UPDATED_TOTAL_COMMENTS)
            .totalFavorites(UPDATED_TOTAL_FAVORITES)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);
        ProjectDTO projectDTO = projectMapper.toDto(updatedProject);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedProjectToMatchAllProperties(updatedProject);
    }

    @Test
    void putNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .title(UPDATED_TITLE)
            .showcase(UPDATED_SHOWCASE)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .githubUrl(UPDATED_GITHUB_URL)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .demoUrl(UPDATED_DEMO_URL)
            .openToCollaboration(UPDATED_OPEN_TO_COLLABORATION)
            .openToFunding(UPDATED_OPEN_TO_FUNDING)
            .type(UPDATED_TYPE)
            .totalViews(UPDATED_TOTAL_VIEWS)
            .totalFavorites(UPDATED_TOTAL_FAVORITES)
            .isDeleted(UPDATED_IS_DELETED)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedProject, project), getPersistedProject(project));
    }

    @Test
    void fullUpdateProjectWithPatch() throws Exception {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the project using partial update
        Project partialUpdatedProject = new Project();
        partialUpdatedProject.setId(project.getId());

        partialUpdatedProject
            .title(UPDATED_TITLE)
            .description(UPDATED_DESCRIPTION)
            .showcase(UPDATED_SHOWCASE)
            .status(UPDATED_STATUS)
            .createdAt(UPDATED_CREATED_AT)
            .updatedAt(UPDATED_UPDATED_AT)
            .githubUrl(UPDATED_GITHUB_URL)
            .websiteUrl(UPDATED_WEBSITE_URL)
            .demoUrl(UPDATED_DEMO_URL)
            .openToCollaboration(UPDATED_OPEN_TO_COLLABORATION)
            .openToFunding(UPDATED_OPEN_TO_FUNDING)
            .type(UPDATED_TYPE)
            .totalLikes(UPDATED_TOTAL_LIKES)
            .totalShares(UPDATED_TOTAL_SHARES)
            .totalViews(UPDATED_TOTAL_VIEWS)
            .totalComments(UPDATED_TOTAL_COMMENTS)
            .totalFavorites(UPDATED_TOTAL_FAVORITES)
            .isDeleted(UPDATED_IS_DELETED)
            .createdBy(UPDATED_CREATED_BY)
            .lastUpdatedBy(UPDATED_LAST_UPDATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedProject.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedProject))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Project in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertProjectUpdatableFieldsEquals(partialUpdatedProject, getPersistedProject(partialUpdatedProject));
    }

    @Test
    void patchNonExistingProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, projectDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamProject() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        project.setId(longCount.incrementAndGet());

        // Create the Project
        ProjectDTO projectDTO = projectMapper.toDto(project);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(projectDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Project in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteProject() {
        // Initialize the database
        insertedProject = projectRepository.save(project).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the project
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, project.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return projectRepository.count().block();
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

    protected Project getPersistedProject(Project project) {
        return projectRepository.findById(project.getId()).block();
    }

    protected void assertPersistedProjectToMatchAllProperties(Project expectedProject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectAllPropertiesEquals(expectedProject, getPersistedProject(expectedProject));
        assertProjectUpdatableFieldsEquals(expectedProject, getPersistedProject(expectedProject));
    }

    protected void assertPersistedProjectToMatchUpdatableProperties(Project expectedProject) {
        // Test fails because reactive api returns an empty object instead of null
        // assertProjectAllUpdatablePropertiesEquals(expectedProject, getPersistedProject(expectedProject));
        assertProjectUpdatableFieldsEquals(expectedProject, getPersistedProject(expectedProject));
    }
}
