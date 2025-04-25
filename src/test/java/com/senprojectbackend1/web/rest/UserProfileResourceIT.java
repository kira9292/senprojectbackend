package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.UserProfileAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.enumeration.Genre;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import com.senprojectbackend1.service.mapper.UserProfileMapper;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
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
 * Integration tests for the {@link UserProfileResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class UserProfileResourceIT {

    private static final String DEFAULT_LOGIN = "AAAAAAAAAA";
    private static final String UPDATED_LOGIN = "BBBBBBBBBB";

    private static final String DEFAULT_FIRST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_FIRST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_LAST_NAME = "AAAAAAAAAA";
    private static final String UPDATED_LAST_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_IMAGE_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVATED = false;
    private static final Boolean UPDATED_ACTIVATED = true;

    private static final String DEFAULT_LANG_KEY = "AAAAAA";
    private static final String UPDATED_LANG_KEY = "BBBBBB";

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_LAST_MODIFIED_BY = "AAAAAAAAAA";
    private static final String UPDATED_LAST_MODIFIED_BY = "BBBBBBBBBB";

    private static final Instant DEFAULT_LAST_MODIFIED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_MODIFIED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_PROFILE_LINK = "AAAAAAAAAA";
    private static final String UPDATED_PROFILE_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_BIOGRAPHY = "AAAAAAAAAA";
    private static final String UPDATED_BIOGRAPHY = "BBBBBBBBBB";

    private static final Instant DEFAULT_BIRTH_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_BIRTH_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String DEFAULT_JOB = "AAAAAAAAAA";
    private static final String UPDATED_JOB = "BBBBBBBBBB";

    private static final Genre DEFAULT_SEXE = Genre.HOMME;
    private static final Genre UPDATED_SEXE = Genre.FEMME;

    private static final String ENTITY_API_URL = "/api/user-profiles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    @Autowired
    private ObjectMapper om;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Mock
    private UserProfileRepository userProfileRepositoryMock;

    @Autowired
    private UserProfileMapper userProfileMapper;

    @Mock
    private UserProfileService userProfileServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private UserProfile userProfile;

    private UserProfile insertedUserProfile;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfile createEntity(EntityManager em) {
        UserProfile userProfile = new UserProfile()
            .login(DEFAULT_LOGIN)
            .firstName(DEFAULT_FIRST_NAME)
            .lastName(DEFAULT_LAST_NAME)
            .email(DEFAULT_EMAIL)
            .imageUrl(DEFAULT_IMAGE_URL)
            .activated(DEFAULT_ACTIVATED)
            .langKey(DEFAULT_LANG_KEY)
            .createdBy(DEFAULT_CREATED_BY)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastModifiedBy(DEFAULT_LAST_MODIFIED_BY)
            .lastModifiedDate(DEFAULT_LAST_MODIFIED_DATE)
            .profileLink(DEFAULT_PROFILE_LINK)
            .biography(DEFAULT_BIOGRAPHY)
            .birthDate(DEFAULT_BIRTH_DATE)
            .job(DEFAULT_JOB)
            .sexe(DEFAULT_SEXE);
        return userProfile;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static UserProfile createUpdatedEntity(EntityManager em) {
        UserProfile updatedUserProfile = new UserProfile()
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .imageUrl(UPDATED_IMAGE_URL)
            .activated(UPDATED_ACTIVATED)
            .langKey(UPDATED_LANG_KEY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .profileLink(UPDATED_PROFILE_LINK)
            .biography(UPDATED_BIOGRAPHY)
            .birthDate(UPDATED_BIRTH_DATE)
            .job(UPDATED_JOB)
            .sexe(UPDATED_SEXE);
        return updatedUserProfile;
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll("rel_user_profile__role").block();
            em.deleteAll(UserProfile.class).block();
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
        userProfile = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedUserProfile != null) {
            userProfileRepository.delete(insertedUserProfile).block();
            insertedUserProfile = null;
        }
        deleteEntities(em);
    }

    @Test
    void createUserProfile() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);
        var returnedUserProfileDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(UserProfileDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the UserProfile in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedUserProfile = userProfileMapper.toEntity(returnedUserProfileDTO);
        assertUserProfileUpdatableFieldsEquals(returnedUserProfile, getPersistedUserProfile(returnedUserProfile));

        insertedUserProfile = returnedUserProfile;
    }

    @Test
    void createUserProfileWithExistingId() throws Exception {
        // Create the UserProfile with an existing ID
        userProfile.setId("existing_id");
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkLoginIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userProfile.setLogin(null);

        // Create the UserProfile, which fails.
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkEmailIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userProfile.setEmail(null);

        // Create the UserProfile, which fails.
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkActivatedIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userProfile.setActivated(null);

        // Create the UserProfile, which fails.
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedDateIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        userProfile.setCreatedDate(null);

        // Create the UserProfile, which fails.
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllUserProfiles() {
        // Initialize the database
        userProfile.setId(UUID.randomUUID().toString());
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList
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
            .value(hasItem(userProfile.getId()))
            .jsonPath("$.[*].login")
            .value(hasItem(DEFAULT_LOGIN))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].activated")
            .value(hasItem(DEFAULT_ACTIVATED))
            .jsonPath("$.[*].langKey")
            .value(hasItem(DEFAULT_LANG_KEY))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].profileLink")
            .value(hasItem(DEFAULT_PROFILE_LINK))
            .jsonPath("$.[*].biography")
            .value(hasItem(DEFAULT_BIOGRAPHY))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].job")
            .value(hasItem(DEFAULT_JOB))
            .jsonPath("$.[*].sexe")
            .value(hasItem(DEFAULT_SEXE.toString()));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserProfilesWithEagerRelationshipsIsEnabled() {
        when(userProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=true").exchange().expectStatus().isOk();

        verify(userProfileServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllUserProfilesWithEagerRelationshipsIsNotEnabled() {
        when(userProfileServiceMock.findAllWithEagerRelationships(any())).thenReturn(Flux.empty());

        webTestClient.get().uri(ENTITY_API_URL + "?eagerload=false").exchange().expectStatus().isOk();
        verify(userProfileRepositoryMock, times(1)).findAllWithEagerRelationships(any());
    }

    @Test
    void getUserProfile() {
        // Initialize the database
        userProfile.setId(UUID.randomUUID().toString());
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get the userProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, userProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(userProfile.getId()))
            .jsonPath("$.login")
            .value(is(DEFAULT_LOGIN))
            .jsonPath("$.firstName")
            .value(is(DEFAULT_FIRST_NAME))
            .jsonPath("$.lastName")
            .value(is(DEFAULT_LAST_NAME))
            .jsonPath("$.email")
            .value(is(DEFAULT_EMAIL))
            .jsonPath("$.imageUrl")
            .value(is(DEFAULT_IMAGE_URL))
            .jsonPath("$.activated")
            .value(is(DEFAULT_ACTIVATED))
            .jsonPath("$.langKey")
            .value(is(DEFAULT_LANG_KEY))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY))
            .jsonPath("$.createdDate")
            .value(is(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.lastModifiedBy")
            .value(is(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.lastModifiedDate")
            .value(is(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.profileLink")
            .value(is(DEFAULT_PROFILE_LINK))
            .jsonPath("$.biography")
            .value(is(DEFAULT_BIOGRAPHY))
            .jsonPath("$.birthDate")
            .value(is(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.job")
            .value(is(DEFAULT_JOB))
            .jsonPath("$.sexe")
            .value(is(DEFAULT_SEXE.toString()));
    }

    @Test
    void getUserProfilesByIdFiltering() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        String id = userProfile.getId();

        defaultUserProfileFiltering("id.equals=" + id, "id.notEquals=" + id);
    }

    @Test
    void getAllUserProfilesByLoginIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where login equals to
        defaultUserProfileFiltering("login.equals=" + DEFAULT_LOGIN, "login.equals=" + UPDATED_LOGIN);
    }

    @Test
    void getAllUserProfilesByLoginIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where login in
        defaultUserProfileFiltering("login.in=" + DEFAULT_LOGIN + "," + UPDATED_LOGIN, "login.in=" + UPDATED_LOGIN);
    }

    @Test
    void getAllUserProfilesByLoginIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where login is not null
        defaultUserProfileFiltering("login.specified=true", "login.specified=false");
    }

    @Test
    void getAllUserProfilesByLoginContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where login contains
        defaultUserProfileFiltering("login.contains=" + DEFAULT_LOGIN, "login.contains=" + UPDATED_LOGIN);
    }

    @Test
    void getAllUserProfilesByLoginNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where login does not contain
        defaultUserProfileFiltering("login.doesNotContain=" + UPDATED_LOGIN, "login.doesNotContain=" + DEFAULT_LOGIN);
    }

    @Test
    void getAllUserProfilesByFirstNameIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where firstName equals to
        defaultUserProfileFiltering("firstName.equals=" + DEFAULT_FIRST_NAME, "firstName.equals=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllUserProfilesByFirstNameIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where firstName in
        defaultUserProfileFiltering("firstName.in=" + DEFAULT_FIRST_NAME + "," + UPDATED_FIRST_NAME, "firstName.in=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllUserProfilesByFirstNameIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where firstName is not null
        defaultUserProfileFiltering("firstName.specified=true", "firstName.specified=false");
    }

    @Test
    void getAllUserProfilesByFirstNameContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where firstName contains
        defaultUserProfileFiltering("firstName.contains=" + DEFAULT_FIRST_NAME, "firstName.contains=" + UPDATED_FIRST_NAME);
    }

    @Test
    void getAllUserProfilesByFirstNameNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where firstName does not contain
        defaultUserProfileFiltering("firstName.doesNotContain=" + UPDATED_FIRST_NAME, "firstName.doesNotContain=" + DEFAULT_FIRST_NAME);
    }

    @Test
    void getAllUserProfilesByLastNameIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastName equals to
        defaultUserProfileFiltering("lastName.equals=" + DEFAULT_LAST_NAME, "lastName.equals=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllUserProfilesByLastNameIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastName in
        defaultUserProfileFiltering("lastName.in=" + DEFAULT_LAST_NAME + "," + UPDATED_LAST_NAME, "lastName.in=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllUserProfilesByLastNameIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastName is not null
        defaultUserProfileFiltering("lastName.specified=true", "lastName.specified=false");
    }

    @Test
    void getAllUserProfilesByLastNameContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastName contains
        defaultUserProfileFiltering("lastName.contains=" + DEFAULT_LAST_NAME, "lastName.contains=" + UPDATED_LAST_NAME);
    }

    @Test
    void getAllUserProfilesByLastNameNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastName does not contain
        defaultUserProfileFiltering("lastName.doesNotContain=" + UPDATED_LAST_NAME, "lastName.doesNotContain=" + DEFAULT_LAST_NAME);
    }

    @Test
    void getAllUserProfilesByEmailIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where email equals to
        defaultUserProfileFiltering("email.equals=" + DEFAULT_EMAIL, "email.equals=" + UPDATED_EMAIL);
    }

    @Test
    void getAllUserProfilesByEmailIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where email in
        defaultUserProfileFiltering("email.in=" + DEFAULT_EMAIL + "," + UPDATED_EMAIL, "email.in=" + UPDATED_EMAIL);
    }

    @Test
    void getAllUserProfilesByEmailIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where email is not null
        defaultUserProfileFiltering("email.specified=true", "email.specified=false");
    }

    @Test
    void getAllUserProfilesByEmailContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where email contains
        defaultUserProfileFiltering("email.contains=" + DEFAULT_EMAIL, "email.contains=" + UPDATED_EMAIL);
    }

    @Test
    void getAllUserProfilesByEmailNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where email does not contain
        defaultUserProfileFiltering("email.doesNotContain=" + UPDATED_EMAIL, "email.doesNotContain=" + DEFAULT_EMAIL);
    }

    @Test
    void getAllUserProfilesByImageUrlIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where imageUrl equals to
        defaultUserProfileFiltering("imageUrl.equals=" + DEFAULT_IMAGE_URL, "imageUrl.equals=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllUserProfilesByImageUrlIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where imageUrl in
        defaultUserProfileFiltering("imageUrl.in=" + DEFAULT_IMAGE_URL + "," + UPDATED_IMAGE_URL, "imageUrl.in=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllUserProfilesByImageUrlIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where imageUrl is not null
        defaultUserProfileFiltering("imageUrl.specified=true", "imageUrl.specified=false");
    }

    @Test
    void getAllUserProfilesByImageUrlContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where imageUrl contains
        defaultUserProfileFiltering("imageUrl.contains=" + DEFAULT_IMAGE_URL, "imageUrl.contains=" + UPDATED_IMAGE_URL);
    }

    @Test
    void getAllUserProfilesByImageUrlNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where imageUrl does not contain
        defaultUserProfileFiltering("imageUrl.doesNotContain=" + UPDATED_IMAGE_URL, "imageUrl.doesNotContain=" + DEFAULT_IMAGE_URL);
    }

    @Test
    void getAllUserProfilesByActivatedIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where activated equals to
        defaultUserProfileFiltering("activated.equals=" + DEFAULT_ACTIVATED, "activated.equals=" + UPDATED_ACTIVATED);
    }

    @Test
    void getAllUserProfilesByActivatedIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where activated in
        defaultUserProfileFiltering("activated.in=" + DEFAULT_ACTIVATED + "," + UPDATED_ACTIVATED, "activated.in=" + UPDATED_ACTIVATED);
    }

    @Test
    void getAllUserProfilesByActivatedIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where activated is not null
        defaultUserProfileFiltering("activated.specified=true", "activated.specified=false");
    }

    @Test
    void getAllUserProfilesByLangKeyIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where langKey equals to
        defaultUserProfileFiltering("langKey.equals=" + DEFAULT_LANG_KEY, "langKey.equals=" + UPDATED_LANG_KEY);
    }

    @Test
    void getAllUserProfilesByLangKeyIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where langKey in
        defaultUserProfileFiltering("langKey.in=" + DEFAULT_LANG_KEY + "," + UPDATED_LANG_KEY, "langKey.in=" + UPDATED_LANG_KEY);
    }

    @Test
    void getAllUserProfilesByLangKeyIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where langKey is not null
        defaultUserProfileFiltering("langKey.specified=true", "langKey.specified=false");
    }

    @Test
    void getAllUserProfilesByLangKeyContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where langKey contains
        defaultUserProfileFiltering("langKey.contains=" + DEFAULT_LANG_KEY, "langKey.contains=" + UPDATED_LANG_KEY);
    }

    @Test
    void getAllUserProfilesByLangKeyNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where langKey does not contain
        defaultUserProfileFiltering("langKey.doesNotContain=" + UPDATED_LANG_KEY, "langKey.doesNotContain=" + DEFAULT_LANG_KEY);
    }

    @Test
    void getAllUserProfilesByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdBy equals to
        defaultUserProfileFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllUserProfilesByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdBy in
        defaultUserProfileFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllUserProfilesByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdBy is not null
        defaultUserProfileFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllUserProfilesByCreatedByContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdBy contains
        defaultUserProfileFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllUserProfilesByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdBy does not contain
        defaultUserProfileFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    @Test
    void getAllUserProfilesByCreatedDateIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdDate equals to
        defaultUserProfileFiltering("createdDate.equals=" + DEFAULT_CREATED_DATE, "createdDate.equals=" + UPDATED_CREATED_DATE);
    }

    @Test
    void getAllUserProfilesByCreatedDateIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdDate in
        defaultUserProfileFiltering(
            "createdDate.in=" + DEFAULT_CREATED_DATE + "," + UPDATED_CREATED_DATE,
            "createdDate.in=" + UPDATED_CREATED_DATE
        );
    }

    @Test
    void getAllUserProfilesByCreatedDateIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where createdDate is not null
        defaultUserProfileFiltering("createdDate.specified=true", "createdDate.specified=false");
    }

    @Test
    void getAllUserProfilesByLastModifiedByIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedBy equals to
        defaultUserProfileFiltering(
            "lastModifiedBy.equals=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.equals=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllUserProfilesByLastModifiedByIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedBy in
        defaultUserProfileFiltering(
            "lastModifiedBy.in=" + DEFAULT_LAST_MODIFIED_BY + "," + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.in=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllUserProfilesByLastModifiedByIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedBy is not null
        defaultUserProfileFiltering("lastModifiedBy.specified=true", "lastModifiedBy.specified=false");
    }

    @Test
    void getAllUserProfilesByLastModifiedByContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedBy contains
        defaultUserProfileFiltering(
            "lastModifiedBy.contains=" + DEFAULT_LAST_MODIFIED_BY,
            "lastModifiedBy.contains=" + UPDATED_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllUserProfilesByLastModifiedByNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedBy does not contain
        defaultUserProfileFiltering(
            "lastModifiedBy.doesNotContain=" + UPDATED_LAST_MODIFIED_BY,
            "lastModifiedBy.doesNotContain=" + DEFAULT_LAST_MODIFIED_BY
        );
    }

    @Test
    void getAllUserProfilesByLastModifiedDateIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedDate equals to
        defaultUserProfileFiltering(
            "lastModifiedDate.equals=" + DEFAULT_LAST_MODIFIED_DATE,
            "lastModifiedDate.equals=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllUserProfilesByLastModifiedDateIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedDate in
        defaultUserProfileFiltering(
            "lastModifiedDate.in=" + DEFAULT_LAST_MODIFIED_DATE + "," + UPDATED_LAST_MODIFIED_DATE,
            "lastModifiedDate.in=" + UPDATED_LAST_MODIFIED_DATE
        );
    }

    @Test
    void getAllUserProfilesByLastModifiedDateIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where lastModifiedDate is not null
        defaultUserProfileFiltering("lastModifiedDate.specified=true", "lastModifiedDate.specified=false");
    }

    @Test
    void getAllUserProfilesByProfileLinkIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where profileLink equals to
        defaultUserProfileFiltering("profileLink.equals=" + DEFAULT_PROFILE_LINK, "profileLink.equals=" + UPDATED_PROFILE_LINK);
    }

    @Test
    void getAllUserProfilesByProfileLinkIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where profileLink in
        defaultUserProfileFiltering(
            "profileLink.in=" + DEFAULT_PROFILE_LINK + "," + UPDATED_PROFILE_LINK,
            "profileLink.in=" + UPDATED_PROFILE_LINK
        );
    }

    @Test
    void getAllUserProfilesByProfileLinkIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where profileLink is not null
        defaultUserProfileFiltering("profileLink.specified=true", "profileLink.specified=false");
    }

    @Test
    void getAllUserProfilesByProfileLinkContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where profileLink contains
        defaultUserProfileFiltering("profileLink.contains=" + DEFAULT_PROFILE_LINK, "profileLink.contains=" + UPDATED_PROFILE_LINK);
    }

    @Test
    void getAllUserProfilesByProfileLinkNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where profileLink does not contain
        defaultUserProfileFiltering(
            "profileLink.doesNotContain=" + UPDATED_PROFILE_LINK,
            "profileLink.doesNotContain=" + DEFAULT_PROFILE_LINK
        );
    }

    @Test
    void getAllUserProfilesByBiographyIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where biography equals to
        defaultUserProfileFiltering("biography.equals=" + DEFAULT_BIOGRAPHY, "biography.equals=" + UPDATED_BIOGRAPHY);
    }

    @Test
    void getAllUserProfilesByBiographyIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where biography in
        defaultUserProfileFiltering("biography.in=" + DEFAULT_BIOGRAPHY + "," + UPDATED_BIOGRAPHY, "biography.in=" + UPDATED_BIOGRAPHY);
    }

    @Test
    void getAllUserProfilesByBiographyIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where biography is not null
        defaultUserProfileFiltering("biography.specified=true", "biography.specified=false");
    }

    @Test
    void getAllUserProfilesByBiographyContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where biography contains
        defaultUserProfileFiltering("biography.contains=" + DEFAULT_BIOGRAPHY, "biography.contains=" + UPDATED_BIOGRAPHY);
    }

    @Test
    void getAllUserProfilesByBiographyNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where biography does not contain
        defaultUserProfileFiltering("biography.doesNotContain=" + UPDATED_BIOGRAPHY, "biography.doesNotContain=" + DEFAULT_BIOGRAPHY);
    }

    @Test
    void getAllUserProfilesByBirthDateIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where birthDate equals to
        defaultUserProfileFiltering("birthDate.equals=" + DEFAULT_BIRTH_DATE, "birthDate.equals=" + UPDATED_BIRTH_DATE);
    }

    @Test
    void getAllUserProfilesByBirthDateIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where birthDate in
        defaultUserProfileFiltering("birthDate.in=" + DEFAULT_BIRTH_DATE + "," + UPDATED_BIRTH_DATE, "birthDate.in=" + UPDATED_BIRTH_DATE);
    }

    @Test
    void getAllUserProfilesByBirthDateIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where birthDate is not null
        defaultUserProfileFiltering("birthDate.specified=true", "birthDate.specified=false");
    }

    @Test
    void getAllUserProfilesByJobIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where job equals to
        defaultUserProfileFiltering("job.equals=" + DEFAULT_JOB, "job.equals=" + UPDATED_JOB);
    }

    @Test
    void getAllUserProfilesByJobIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where job in
        defaultUserProfileFiltering("job.in=" + DEFAULT_JOB + "," + UPDATED_JOB, "job.in=" + UPDATED_JOB);
    }

    @Test
    void getAllUserProfilesByJobIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where job is not null
        defaultUserProfileFiltering("job.specified=true", "job.specified=false");
    }

    @Test
    void getAllUserProfilesByJobContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where job contains
        defaultUserProfileFiltering("job.contains=" + DEFAULT_JOB, "job.contains=" + UPDATED_JOB);
    }

    @Test
    void getAllUserProfilesByJobNotContainsSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where job does not contain
        defaultUserProfileFiltering("job.doesNotContain=" + UPDATED_JOB, "job.doesNotContain=" + DEFAULT_JOB);
    }

    @Test
    void getAllUserProfilesBySexeIsEqualToSomething() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where sexe equals to
        defaultUserProfileFiltering("sexe.equals=" + DEFAULT_SEXE, "sexe.equals=" + UPDATED_SEXE);
    }

    @Test
    void getAllUserProfilesBySexeIsInShouldWork() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where sexe in
        defaultUserProfileFiltering("sexe.in=" + DEFAULT_SEXE + "," + UPDATED_SEXE, "sexe.in=" + UPDATED_SEXE);
    }

    @Test
    void getAllUserProfilesBySexeIsNullOrNotNull() {
        // Initialize the database
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        // Get all the userProfileList where sexe is not null
        defaultUserProfileFiltering("sexe.specified=true", "sexe.specified=false");
    }

    private void defaultUserProfileFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultUserProfileShouldBeFound(shouldBeFound);
        defaultUserProfileShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultUserProfileShouldBeFound(String filter) {
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
            .value(hasItem(userProfile.getId()))
            .jsonPath("$.[*].login")
            .value(hasItem(DEFAULT_LOGIN))
            .jsonPath("$.[*].firstName")
            .value(hasItem(DEFAULT_FIRST_NAME))
            .jsonPath("$.[*].lastName")
            .value(hasItem(DEFAULT_LAST_NAME))
            .jsonPath("$.[*].email")
            .value(hasItem(DEFAULT_EMAIL))
            .jsonPath("$.[*].imageUrl")
            .value(hasItem(DEFAULT_IMAGE_URL))
            .jsonPath("$.[*].activated")
            .value(hasItem(DEFAULT_ACTIVATED))
            .jsonPath("$.[*].langKey")
            .value(hasItem(DEFAULT_LANG_KEY))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY))
            .jsonPath("$.[*].createdDate")
            .value(hasItem(DEFAULT_CREATED_DATE.toString()))
            .jsonPath("$.[*].lastModifiedBy")
            .value(hasItem(DEFAULT_LAST_MODIFIED_BY))
            .jsonPath("$.[*].lastModifiedDate")
            .value(hasItem(DEFAULT_LAST_MODIFIED_DATE.toString()))
            .jsonPath("$.[*].profileLink")
            .value(hasItem(DEFAULT_PROFILE_LINK))
            .jsonPath("$.[*].biography")
            .value(hasItem(DEFAULT_BIOGRAPHY))
            .jsonPath("$.[*].birthDate")
            .value(hasItem(DEFAULT_BIRTH_DATE.toString()))
            .jsonPath("$.[*].job")
            .value(hasItem(DEFAULT_JOB))
            .jsonPath("$.[*].sexe")
            .value(hasItem(DEFAULT_SEXE.toString()));

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
    private void defaultUserProfileShouldNotBeFound(String filter) {
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
    void getNonExistingUserProfile() {
        // Get the userProfile
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingUserProfile() throws Exception {
        // Initialize the database
        userProfile.setId(UUID.randomUUID().toString());
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userProfile
        UserProfile updatedUserProfile = userProfileRepository.findById(userProfile.getId()).block();
        updatedUserProfile
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .imageUrl(UPDATED_IMAGE_URL)
            .activated(UPDATED_ACTIVATED)
            .langKey(UPDATED_LANG_KEY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .profileLink(UPDATED_PROFILE_LINK)
            .biography(UPDATED_BIOGRAPHY)
            .birthDate(UPDATED_BIRTH_DATE)
            .job(UPDATED_JOB)
            .sexe(UPDATED_SEXE);
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(updatedUserProfile);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedUserProfileToMatchAllProperties(updatedUserProfile);
    }

    @Test
    void putNonExistingUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(UUID.randomUUID().toString());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, userProfileDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(UUID.randomUUID().toString());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(UUID.randomUUID().toString());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateUserProfileWithPatch() throws Exception {
        // Initialize the database
        userProfile.setId(UUID.randomUUID().toString());
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userProfile using partial update
        UserProfile partialUpdatedUserProfile = new UserProfile();
        partialUpdatedUserProfile.setId(userProfile.getId());

        partialUpdatedUserProfile
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .biography(UPDATED_BIOGRAPHY)
            .job(UPDATED_JOB);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserProfileUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedUserProfile, userProfile),
            getPersistedUserProfile(userProfile)
        );
    }

    @Test
    void fullUpdateUserProfileWithPatch() throws Exception {
        // Initialize the database
        userProfile.setId(UUID.randomUUID().toString());
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the userProfile using partial update
        UserProfile partialUpdatedUserProfile = new UserProfile();
        partialUpdatedUserProfile.setId(userProfile.getId());

        partialUpdatedUserProfile
            .login(UPDATED_LOGIN)
            .firstName(UPDATED_FIRST_NAME)
            .lastName(UPDATED_LAST_NAME)
            .email(UPDATED_EMAIL)
            .imageUrl(UPDATED_IMAGE_URL)
            .activated(UPDATED_ACTIVATED)
            .langKey(UPDATED_LANG_KEY)
            .createdBy(UPDATED_CREATED_BY)
            .createdDate(UPDATED_CREATED_DATE)
            .lastModifiedBy(UPDATED_LAST_MODIFIED_BY)
            .lastModifiedDate(UPDATED_LAST_MODIFIED_DATE)
            .profileLink(UPDATED_PROFILE_LINK)
            .biography(UPDATED_BIOGRAPHY)
            .birthDate(UPDATED_BIRTH_DATE)
            .job(UPDATED_JOB)
            .sexe(UPDATED_SEXE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedUserProfile.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedUserProfile))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the UserProfile in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertUserProfileUpdatableFieldsEquals(partialUpdatedUserProfile, getPersistedUserProfile(partialUpdatedUserProfile));
    }

    @Test
    void patchNonExistingUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(UUID.randomUUID().toString());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, userProfileDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(UUID.randomUUID().toString());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, UUID.randomUUID().toString())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamUserProfile() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        userProfile.setId(UUID.randomUUID().toString());

        // Create the UserProfile
        UserProfileDTO userProfileDTO = userProfileMapper.toDto(userProfile);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(userProfileDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the UserProfile in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteUserProfile() {
        // Initialize the database
        userProfile.setId(UUID.randomUUID().toString());
        insertedUserProfile = userProfileRepository.save(userProfile).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the userProfile
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, userProfile.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return userProfileRepository.count().block();
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

    protected UserProfile getPersistedUserProfile(UserProfile userProfile) {
        return userProfileRepository.findById(userProfile.getId()).block();
    }

    protected void assertPersistedUserProfileToMatchAllProperties(UserProfile expectedUserProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserProfileAllPropertiesEquals(expectedUserProfile, getPersistedUserProfile(expectedUserProfile));
        assertUserProfileUpdatableFieldsEquals(expectedUserProfile, getPersistedUserProfile(expectedUserProfile));
    }

    protected void assertPersistedUserProfileToMatchUpdatableProperties(UserProfile expectedUserProfile) {
        // Test fails because reactive api returns an empty object instead of null
        // assertUserProfileAllUpdatablePropertiesEquals(expectedUserProfile, getPersistedUserProfile(expectedUserProfile));
        assertUserProfileUpdatableFieldsEquals(expectedUserProfile, getPersistedUserProfile(expectedUserProfile));
    }
}
