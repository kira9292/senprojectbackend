package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.CommentAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.Comment;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.enumeration.CommentStatus;
import com.senprojectbackend1.repository.CommentRepository;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.dto.CommentDTO;
import com.senprojectbackend1.service.mapper.CommentMapper;
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
 * Integration tests for the {@link CommentResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class CommentResourceIT {

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final Instant DEFAULT_CREATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_UPDATED_AT = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_UPDATED_AT = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final CommentStatus DEFAULT_STATUS = CommentStatus.ACTIVE;
    private static final CommentStatus UPDATED_STATUS = CommentStatus.EDITED;

    private static final String ENTITY_API_URL = "/api/comments";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Comment comment;

    private Comment insertedComment;

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
    public static Comment createEntity() {
        return new Comment().content(DEFAULT_CONTENT).createdAt(DEFAULT_CREATED_AT).updatedAt(DEFAULT_UPDATED_AT).status(DEFAULT_STATUS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Comment createUpdatedEntity() {
        return new Comment().content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT).status(UPDATED_STATUS);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Comment.class).block();
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
        comment = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedComment != null) {
            commentRepository.delete(insertedComment).block();
            insertedComment = null;
        }
        deleteEntities(em);
    }

    @Test
    void createComment() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);
        var returnedCommentDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(CommentDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Comment in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedComment = commentMapper.toEntity(returnedCommentDTO);
        assertCommentUpdatableFieldsEquals(returnedComment, getPersistedComment(returnedComment));

        insertedComment = returnedComment;
    }

    @Test
    void createCommentWithExistingId() throws Exception {
        // Create the Comment with an existing ID
        comment.setId(1L);
        CommentDTO commentDTO = commentMapper.toDto(comment);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkContentIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        comment.setContent(null);

        // Create the Comment, which fails.
        CommentDTO commentDTO = commentMapper.toDto(comment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkCreatedAtIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        comment.setCreatedAt(null);

        // Create the Comment, which fails.
        CommentDTO commentDTO = commentMapper.toDto(comment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void checkStatusIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        comment.setStatus(null);

        // Create the Comment, which fails.
        CommentDTO commentDTO = commentMapper.toDto(comment);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllComments() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList
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
            .value(hasItem(comment.getId().intValue()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));
    }

    @Test
    void getComment() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get the comment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, comment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(comment.getId().intValue()))
            .jsonPath("$.content")
            .value(is(DEFAULT_CONTENT))
            .jsonPath("$.createdAt")
            .value(is(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.updatedAt")
            .value(is(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.status")
            .value(is(DEFAULT_STATUS.toString()));
    }

    @Test
    void getCommentsByIdFiltering() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        Long id = comment.getId();

        defaultCommentFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultCommentFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultCommentFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllCommentsByContentIsEqualToSomething() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where content equals to
        defaultCommentFiltering("content.equals=" + DEFAULT_CONTENT, "content.equals=" + UPDATED_CONTENT);
    }

    @Test
    void getAllCommentsByContentIsInShouldWork() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where content in
        defaultCommentFiltering("content.in=" + DEFAULT_CONTENT + "," + UPDATED_CONTENT, "content.in=" + UPDATED_CONTENT);
    }

    @Test
    void getAllCommentsByContentIsNullOrNotNull() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where content is not null
        defaultCommentFiltering("content.specified=true", "content.specified=false");
    }

    @Test
    void getAllCommentsByContentContainsSomething() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where content contains
        defaultCommentFiltering("content.contains=" + DEFAULT_CONTENT, "content.contains=" + UPDATED_CONTENT);
    }

    @Test
    void getAllCommentsByContentNotContainsSomething() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where content does not contain
        defaultCommentFiltering("content.doesNotContain=" + UPDATED_CONTENT, "content.doesNotContain=" + DEFAULT_CONTENT);
    }

    @Test
    void getAllCommentsByCreatedAtIsEqualToSomething() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where createdAt equals to
        defaultCommentFiltering("createdAt.equals=" + DEFAULT_CREATED_AT, "createdAt.equals=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllCommentsByCreatedAtIsInShouldWork() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where createdAt in
        defaultCommentFiltering("createdAt.in=" + DEFAULT_CREATED_AT + "," + UPDATED_CREATED_AT, "createdAt.in=" + UPDATED_CREATED_AT);
    }

    @Test
    void getAllCommentsByCreatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where createdAt is not null
        defaultCommentFiltering("createdAt.specified=true", "createdAt.specified=false");
    }

    @Test
    void getAllCommentsByUpdatedAtIsEqualToSomething() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where updatedAt equals to
        defaultCommentFiltering("updatedAt.equals=" + DEFAULT_UPDATED_AT, "updatedAt.equals=" + UPDATED_UPDATED_AT);
    }

    @Test
    void getAllCommentsByUpdatedAtIsInShouldWork() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where updatedAt in
        defaultCommentFiltering("updatedAt.in=" + DEFAULT_UPDATED_AT + "," + UPDATED_UPDATED_AT, "updatedAt.in=" + UPDATED_UPDATED_AT);
    }

    @Test
    void getAllCommentsByUpdatedAtIsNullOrNotNull() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where updatedAt is not null
        defaultCommentFiltering("updatedAt.specified=true", "updatedAt.specified=false");
    }

    @Test
    void getAllCommentsByStatusIsEqualToSomething() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where status equals to
        defaultCommentFiltering("status.equals=" + DEFAULT_STATUS, "status.equals=" + UPDATED_STATUS);
    }

    @Test
    void getAllCommentsByStatusIsInShouldWork() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where status in
        defaultCommentFiltering("status.in=" + DEFAULT_STATUS + "," + UPDATED_STATUS, "status.in=" + UPDATED_STATUS);
    }

    @Test
    void getAllCommentsByStatusIsNullOrNotNull() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        // Get all the commentList where status is not null
        defaultCommentFiltering("status.specified=true", "status.specified=false");
    }

    @Test
    void getAllCommentsByUserIsEqualToSomething() {
        UserProfile user = UserProfileResourceIT.createEntity(em);
        userProfileRepository.save(user).block();
        String userId = user.getId();
        comment.setUserId(userId);
        insertedComment = commentRepository.save(comment).block();
        // Get all the commentList where user equals to userId
        defaultCommentShouldBeFound("userId.equals=" + userId);

        // Get all the commentList where user equals to "invalid-id"
        defaultCommentShouldNotBeFound("userId.equals=" + "invalid-id");
    }

    @Test
    void getAllCommentsByProjectIsEqualToSomething() {
        Project project = ProjectResourceIT.createEntity();
        projectRepository.save(project).block();
        Long projectId = project.getId();
        comment.setProjectId(projectId);
        insertedComment = commentRepository.save(comment).block();
        // Get all the commentList where project equals to projectId
        defaultCommentShouldBeFound("projectId.equals=" + projectId);

        // Get all the commentList where project equals to (projectId + 1)
        defaultCommentShouldNotBeFound("projectId.equals=" + (projectId + 1));
    }

    private void defaultCommentFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultCommentShouldBeFound(shouldBeFound);
        defaultCommentShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultCommentShouldBeFound(String filter) {
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
            .value(hasItem(comment.getId().intValue()))
            .jsonPath("$.[*].content")
            .value(hasItem(DEFAULT_CONTENT))
            .jsonPath("$.[*].createdAt")
            .value(hasItem(DEFAULT_CREATED_AT.toString()))
            .jsonPath("$.[*].updatedAt")
            .value(hasItem(DEFAULT_UPDATED_AT.toString()))
            .jsonPath("$.[*].status")
            .value(hasItem(DEFAULT_STATUS.toString()));

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
    private void defaultCommentShouldNotBeFound(String filter) {
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
    void getNonExistingComment() {
        // Get the comment
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingComment() throws Exception {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comment
        Comment updatedComment = commentRepository.findById(comment.getId()).block();
        updatedComment.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT).status(UPDATED_STATUS);
        CommentDTO commentDTO = commentMapper.toDto(updatedComment);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedCommentToMatchAllProperties(updatedComment);
    }

    @Test
    void putNonExistingComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comment.setId(longCount.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comment.setId(longCount.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comment.setId(longCount.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment.status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommentUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedComment, comment), getPersistedComment(comment));
    }

    @Test
    void fullUpdateCommentWithPatch() throws Exception {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the comment using partial update
        Comment partialUpdatedComment = new Comment();
        partialUpdatedComment.setId(comment.getId());

        partialUpdatedComment.content(UPDATED_CONTENT).createdAt(UPDATED_CREATED_AT).updatedAt(UPDATED_UPDATED_AT).status(UPDATED_STATUS);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedComment.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedComment))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Comment in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertCommentUpdatableFieldsEquals(partialUpdatedComment, getPersistedComment(partialUpdatedComment));
    }

    @Test
    void patchNonExistingComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comment.setId(longCount.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, commentDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comment.setId(longCount.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamComment() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        comment.setId(longCount.incrementAndGet());

        // Create the Comment
        CommentDTO commentDTO = commentMapper.toDto(comment);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(commentDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Comment in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteComment() {
        // Initialize the database
        insertedComment = commentRepository.save(comment).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the comment
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, comment.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return commentRepository.count().block();
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

    protected Comment getPersistedComment(Comment comment) {
        return commentRepository.findById(comment.getId()).block();
    }

    protected void assertPersistedCommentToMatchAllProperties(Comment expectedComment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCommentAllPropertiesEquals(expectedComment, getPersistedComment(expectedComment));
        assertCommentUpdatableFieldsEquals(expectedComment, getPersistedComment(expectedComment));
    }

    protected void assertPersistedCommentToMatchUpdatableProperties(Comment expectedComment) {
        // Test fails because reactive api returns an empty object instead of null
        // assertCommentAllUpdatablePropertiesEquals(expectedComment, getPersistedComment(expectedComment));
        assertCommentUpdatableFieldsEquals(expectedComment, getPersistedComment(expectedComment));
    }
}
