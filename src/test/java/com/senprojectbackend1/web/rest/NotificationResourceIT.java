package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.NotificationAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.NotificationRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.dto.NotificationDTO;
import com.senprojectbackend1.service.mapper.NotificationMapper;
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
 * Integration tests for the {@link NotificationResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class NotificationResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_READ_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_READ_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final NotificationType DEFAULT_TYPE = NotificationType.TEAM_INVITATION;
    private static final NotificationType UPDATED_TYPE = NotificationType.TEAM_JOINED;

    private static final String DEFAULT_ENTITY_ID = "AAAAAAAAAA";
    private static final String UPDATED_ENTITY_ID = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/notifications";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private NotificationRepository notificationRepository;

    @Autowired
    private NotificationMapper notificationMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Notification notification;

    private Notification insertedNotification;

    @Autowired
    private UserProfileRepository userProfileRepository;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createEntity() {
        return new Notification()
            .content(DEFAULT_CONTENT)
            .createdAt(DEFAULT_CREATED_AT)
            .readAt(DEFAULT_READ_AT)
            .type(DEFAULT_TYPE)
            .entityId(DEFAULT_ENTITY_ID);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Notification createUpdatedEntity() {
        return new Notification()
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT)
            .readAt(UPDATED_READ_AT)
            .type(UPDATED_TYPE)
            .entityId(UPDATED_ENTITY_ID);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Notification.class).block();
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
        notification = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedNotification != null) {
            notificationRepository.delete(insertedNotification).block();
            insertedNotification = null;
        }
        deleteEntities(em);
    }

    @Test
    void createNotification() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);
        var returnedNotificationDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(NotificationDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Notification in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedNotification = notificationMapper.toEntity(returnedNotificationDTO);
        assertNotificationUpdatableFieldsEquals(returnedNotification, getPersistedNotification(returnedNotification));

        insertedNotification = returnedNotification;
    }

    @Test
    void createNotificationWithExistingId() throws Exception {
        // Create the Notification with an existing ID
        notification.setId(1L);
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setContent(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setCreatedAt(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkTypeIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        notification.setType(null);

        // Create the Notification, which fails.
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllNotifications() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList
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
            .value(hasItem(notification.getId().intValue()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].readAt")
            .value(hasItem(DEFAULT_READ_AT.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].entityId")
            .value(hasItem(DEFAULT_ENTITY_ID));
    }

    @Test
    void getNotification() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(notification.getId().intValue()))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.readAt")
            .value(is(DEFAULT_READ_AT.toString()))
            .jsonPath("$.type")
            .value(is(DEFAULT_TYPE.toString()))
            .jsonPath("$.entityId")
            .value(is(DEFAULT_ENTITY_ID));
    }

    @Test
    void getNotificationsByIdFiltering() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        Long id = notification.getId();

        defaultNotificationFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultNotificationFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultNotificationFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllNotificationsByContentIsEqualToSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where content equals to
        defaultNotificationFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    void getAllNotificationsByContentIsInShouldWork() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where content in
        defaultNotificationFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    void getAllNotificationsByContentIsNullOrNotNull() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where content is not null
        defaultNotificationFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    void getAllNotificationsByContentContainsSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where content contains
        defaultNotificationFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    void getAllNotificationsByContentNotContainsSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where content does not contain
        defaultNotificationFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    void getAllNotificationsByCreatedAtIsEqualToSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where createdAt equals to
        defaultNotificationFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllNotificationsByCreatedAtIsInShouldWork() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where createdAt in
        defaultNotificationFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllNotificationsByCreatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where createdAt is not null
        defaultNotificationFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    void getAllNotificationsByReadAtIsEqualToSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where readAt equals to
        defaultNotificationFiltering("readAt.equals=" + DEFAULT_READ_AT, "readAt.equals=" + UPDATED_READ_AT);
    }

    @Test
    void getAllNotificationsByReadAtIsInShouldWork() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where readAt in
        defaultNotificationFiltering("readAt.in=" + DEFAULT_READ_AT + "," + UPDATED_READ_AT, "readAt.in=" + UPDATED_READ_AT);
    }

    @Test
    void getAllNotificationsByReadAtIsNullOrNotNull() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where readAt is not null
        defaultNotificationFiltering("readAt.specified=true", "readAt.specified=false");
    }

    @Test
    void getAllNotificationsByTypeIsEqualToSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where type equals to
        defaultNotificationFiltering("type.equals=" + DEFAULT_TYPE, "type.equals=" + UPDATED_TYPE);
    }

    @Test
    void getAllNotificationsByTypeIsInShouldWork() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where type in
        defaultNotificationFiltering("type.in=" + DEFAULT_TYPE + "," + UPDATED_TYPE, "type.in=" + UPDATED_TYPE);
    }

    @Test
    void getAllNotificationsByTypeIsNullOrNotNull() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where type is not null
        defaultNotificationFiltering("type.specified=true", "type.specified=false");
    }

    @Test
    void getAllNotificationsByEntityIdIsEqualToSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where entityId equals to
        defaultNotificationFiltering("entityId.equals=" + DEFAULT_ENTITY_ID, "entityId.equals=" + UPDATED_ENTITY_ID);
    }

    @Test
    void getAllNotificationsByEntityIdIsInShouldWork() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where entityId in
        defaultNotificationFiltering("entityId.in=" + DEFAULT_ENTITY_ID + "," + UPDATED_ENTITY_ID, "entityId.in=" + UPDATED_ENTITY_ID);
    }

    @Test
    void getAllNotificationsByEntityIdIsNullOrNotNull() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where entityId is not null
        defaultNotificationFiltering("entityId.specified=true", "entityId.specified=false");
    }

    @Test
    void getAllNotificationsByEntityIdContainsSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where entityId contains
        defaultNotificationFiltering("entityId.contains=" + DEFAULT_ENTITY_ID, "entityId.contains=" + UPDATED_ENTITY_ID);
    }

    @Test
    void getAllNotificationsByEntityIdNotContainsSomething() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        // Get all the notificationList where entityId does not contain
        defaultNotificationFiltering("entityId.doesNotContain=" + UPDATED_ENTITY_ID, "entityId.doesNotContain=" + DEFAULT_ENTITY_ID);
    }

    @Test
    void getAllNotificationsByUserIsEqualToSomething() {
        UserProfile user = UserProfileResourceIT.createEntity(em);
        userProfileRepository.save(user).block();
        String userId = user.getId();
        notification.setUserId(userId);
        insertedNotification = notificationRepository.save(notification).block();
        // Get all the notificationList where user equals to userId
        defaultNotificationShouldBeFound("userId.equals=" + userId);

        // Get all the notificationList where user equals to "invalid-id"
        defaultNotificationShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    private void defaultNotificationFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultNotificationShouldBeFound(shouldBeFound);
        defaultNotificationShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultNotificationShouldBeFound(String filter) {
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
            .value(hasItem(notification.getId().intValue()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].readAt")
            .value(hasItem(DEFAULT_READ_AT.toString()))
            .jsonPath("$.[*].type")
            .value(hasItem(DEFAULT_TYPE.toString()))
            .jsonPath("$.[*].entityId")
            .value(hasItem(DEFAULT_ENTITY_ID));

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
    private void defaultNotificationShouldNotBeFound(String filter) {
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
    void getNonExistingNotification() {
        // Get the notification
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingNotification() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification
        Notification updatedNotification = notificationRepository.findById(notification.getId()).block();
        updatedNotification
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT)
            .readAt(UPDATED_READ_AT)
            .type(UPDATED_TYPE)
            .entityId(UPDATED_ENTITY_ID);
        NotificationDTO notificationDTO = notificationMapper.toDto(updatedNotification);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedNotificationToMatchAllProperties(updatedNotification);
    }

    @Test
    void putNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).readAt(UPDATED_READ_AT).type(UPDATED_TYPE);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedNotification, notification),
            getPersistedNotification(notification)
        );
    }

    @Test
    void fullUpdateNotificationWithPatch() throws Exception {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the notification using partial update
        Notification partialUpdatedNotification = new Notification();
        partialUpdatedNotification.setId(notification.getId());

        partialUpdatedNotification
            .content(UPDATED_CONTENT)
            .createdAt(UPDATED_CREATED_AT)
            .readAt(UPDATED_READ_AT)
            .type(UPDATED_TYPE)
            .entityId(UPDATED_ENTITY_ID);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedNotification.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedNotification))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Notification in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertNotificationUpdatableFieldsEquals(partialUpdatedNotification, getPersistedNotification(partialUpdatedNotification));
    }

    @Test
    void patchNonExistingNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, notificationDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamNotification() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        notification.setId(longCount.incrementAndGet());

        // Create the Notification
        NotificationDTO notificationDTO = notificationMapper.toDto(notification);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(notificationDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Notification in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteNotification() {
        // Initialize the database
        insertedNotification = notificationRepository.save(notification).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the notification
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, notification.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return notificationRepository.count().block();
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

    protected Notification getPersistedNotification(Notification notification) {
        return notificationRepository.findById(notification.getId()).block();
    }

    protected void assertPersistedNotificationToMatchAllProperties(Notification expectedNotification) {
        // Test fails because reactive api returns an empty object instead of null
        // assertNotificationAllPropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
        assertNotificationUpdatableFieldsEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }

    protected void assertPersistedNotificationToMatchUpdatableProperties(Notification expectedNotification) {
        // Test fails because reactive api returns an empty object instead of null
        // assertNotificationAllUpdatablePropertiesEquals(expectedNotification, getPersistedNotification(expectedNotification));
        assertNotificationUpdatableFieldsEquals(expectedNotification, getPersistedNotification(expectedNotification));
    }
}
