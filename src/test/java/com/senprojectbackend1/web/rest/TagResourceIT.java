package com.senprojectbackend1.web.rest;

import static com.senprojectbackend1.domain.TagAsserts.*;
import static com.senprojectbackend1.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers.csrf;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.senprojectbackend1.IntegrationTest;
import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.repository.EntityManager;
import com.senprojectbackend1.repository.TagRepository;
import com.senprojectbackend1.service.dto.TagDTO;
import com.senprojectbackend1.service.mapper.TagMapper;
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
 * Integration tests for the {@link TagResource} REST controller.
 */
@IntegrationTest
@AutoConfigureWebTestClient(timeout = IntegrationTest.DEFAULT_ENTITY_TIMEOUT)
@WithMockUser
class TagResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_COLOR = "#61F";
    private static final String UPDATED_COLOR = "#5b87AF";

    private static final Boolean DEFAULT_IS_FORBIDDEN = false;
    private static final Boolean UPDATED_IS_FORBIDDEN = true;

    private static final String DEFAULT_CREATED_BY = "AAAAAAAAAA";
    private static final String UPDATED_CREATED_BY = "BBBBBBBBBB";

    private static final String ENTITY_API_URL = "/api/tags";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private TagRepository tagRepository;

    @Autowired
    private TagMapper tagMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private WebTestClient webTestClient;

    private Tag tag;

    private Tag insertedTag;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tag createEntity() {
        return new Tag().name(DEFAULT_NAME).color(DEFAULT_COLOR).isForbidden(DEFAULT_IS_FORBIDDEN).createdBy(DEFAULT_CREATED_BY);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Tag createUpdatedEntity() {
        return new Tag().name(UPDATED_NAME).color(UPDATED_COLOR).isForbidden(UPDATED_IS_FORBIDDEN).createdBy(UPDATED_CREATED_BY);
    }

    public static void deleteEntities(EntityManager em) {
        try {
            em.deleteAll(Tag.class).block();
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
        tag = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedTag != null) {
            tagRepository.delete(insertedTag).block();
            insertedTag = null;
        }
        deleteEntities(em);
    }

    @Test
    void createTag() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);
        var returnedTagDTO = webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isCreated()
            .expectBody(TagDTO.class)
            .returnResult()
            .getResponseBody();

        // Validate the Tag in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedTag = tagMapper.toEntity(returnedTagDTO);
        assertTagUpdatableFieldsEquals(returnedTag, getPersistedTag(returnedTag));

        insertedTag = returnedTag;
    }

    @Test
    void createTagWithExistingId() throws Exception {
        // Create the Tag with an existing ID
        tag.setId(1L);
        TagDTO tagDTO = tagMapper.toDto(tag);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        tag.setName(null);

        // Create the Tag, which fails.
        TagDTO tagDTO = tagMapper.toDto(tag);

        webTestClient
            .post()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    void getAllTags() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList
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
            .value(hasItem(tag.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].color")
            .value(hasItem(DEFAULT_COLOR))
            .jsonPath("$.[*].isForbidden")
            .value(hasItem(DEFAULT_IS_FORBIDDEN))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY));
    }

    @Test
    void getTag() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get the tag
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, tag.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isOk()
            .expectHeader()
            .contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id")
            .value(is(tag.getId().intValue()))
            .jsonPath("$.name")
            .value(is(DEFAULT_NAME))
            .jsonPath("$.color")
            .value(is(DEFAULT_COLOR))
            .jsonPath("$.isForbidden")
            .value(is(DEFAULT_IS_FORBIDDEN))
            .jsonPath("$.createdBy")
            .value(is(DEFAULT_CREATED_BY));
    }

    @Test
    void getTagsByIdFiltering() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        Long id = tag.getId();

        defaultTagFiltering("id.equals=" + id, "id.notEquals=" + id);

        defaultTagFiltering("id.greaterThanOrEqual=" + id, "id.greaterThan=" + id);

        defaultTagFiltering("id.lessThanOrEqual=" + id, "id.lessThan=" + id);
    }

    @Test
    void getAllTagsByNameIsEqualToSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where name equals to
        defaultTagFiltering("name.equals=" + DEFAULT_NAME, "name.equals=" + UPDATED_NAME);
    }

    @Test
    void getAllTagsByNameIsInShouldWork() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where name in
        defaultTagFiltering("name.in=" + DEFAULT_NAME + "," + UPDATED_NAME, "name.in=" + UPDATED_NAME);
    }

    @Test
    void getAllTagsByNameIsNullOrNotNull() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where name is not null
        defaultTagFiltering("name.specified=true", "name.specified=false");
    }

    @Test
    void getAllTagsByNameContainsSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where name contains
        defaultTagFiltering("name.contains=" + DEFAULT_NAME, "name.contains=" + UPDATED_NAME);
    }

    @Test
    void getAllTagsByNameNotContainsSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where name does not contain
        defaultTagFiltering("name.doesNotContain=" + UPDATED_NAME, "name.doesNotContain=" + DEFAULT_NAME);
    }

    @Test
    void getAllTagsByColorIsEqualToSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where color equals to
        defaultTagFiltering("color.equals=" + DEFAULT_COLOR, "color.equals=" + UPDATED_COLOR);
    }

    @Test
    void getAllTagsByColorIsInShouldWork() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where color in
        defaultTagFiltering("color.in=" + DEFAULT_COLOR + "," + UPDATED_COLOR, "color.in=" + UPDATED_COLOR);
    }

    @Test
    void getAllTagsByColorIsNullOrNotNull() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where color is not null
        defaultTagFiltering("color.specified=true", "color.specified=false");
    }

    @Test
    void getAllTagsByColorContainsSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where color contains
        defaultTagFiltering("color.contains=" + DEFAULT_COLOR, "color.contains=" + UPDATED_COLOR);
    }

    @Test
    void getAllTagsByColorNotContainsSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where color does not contain
        defaultTagFiltering("color.doesNotContain=" + UPDATED_COLOR, "color.doesNotContain=" + DEFAULT_COLOR);
    }

    @Test
    void getAllTagsByIsForbiddenIsEqualToSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where isForbidden equals to
        defaultTagFiltering("isForbidden.equals=" + DEFAULT_IS_FORBIDDEN, "isForbidden.equals=" + UPDATED_IS_FORBIDDEN);
    }

    @Test
    void getAllTagsByIsForbiddenIsInShouldWork() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where isForbidden in
        defaultTagFiltering(
            "isForbidden.in=" + DEFAULT_IS_FORBIDDEN + "," + UPDATED_IS_FORBIDDEN,
            "isForbidden.in=" + UPDATED_IS_FORBIDDEN
        );
    }

    @Test
    void getAllTagsByIsForbiddenIsNullOrNotNull() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where isForbidden is not null
        defaultTagFiltering("isForbidden.specified=true", "isForbidden.specified=false");
    }

    @Test
    void getAllTagsByCreatedByIsEqualToSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where createdBy equals to
        defaultTagFiltering("createdBy.equals=" + DEFAULT_CREATED_BY, "createdBy.equals=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllTagsByCreatedByIsInShouldWork() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where createdBy in
        defaultTagFiltering("createdBy.in=" + DEFAULT_CREATED_BY + "," + UPDATED_CREATED_BY, "createdBy.in=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllTagsByCreatedByIsNullOrNotNull() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where createdBy is not null
        defaultTagFiltering("createdBy.specified=true", "createdBy.specified=false");
    }

    @Test
    void getAllTagsByCreatedByContainsSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where createdBy contains
        defaultTagFiltering("createdBy.contains=" + DEFAULT_CREATED_BY, "createdBy.contains=" + UPDATED_CREATED_BY);
    }

    @Test
    void getAllTagsByCreatedByNotContainsSomething() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        // Get all the tagList where createdBy does not contain
        defaultTagFiltering("createdBy.doesNotContain=" + UPDATED_CREATED_BY, "createdBy.doesNotContain=" + DEFAULT_CREATED_BY);
    }

    private void defaultTagFiltering(String shouldBeFound, String shouldNotBeFound) {
        defaultTagShouldBeFound(shouldBeFound);
        defaultTagShouldNotBeFound(shouldNotBeFound);
    }

    /**
     * Executes the search, and checks that the default entity is returned.
     */
    private void defaultTagShouldBeFound(String filter) {
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
            .value(hasItem(tag.getId().intValue()))
            .jsonPath("$.[*].name")
            .value(hasItem(DEFAULT_NAME))
            .jsonPath("$.[*].color")
            .value(hasItem(DEFAULT_COLOR))
            .jsonPath("$.[*].isForbidden")
            .value(hasItem(DEFAULT_IS_FORBIDDEN))
            .jsonPath("$.[*].createdBy")
            .value(hasItem(DEFAULT_CREATED_BY));

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
    private void defaultTagShouldNotBeFound(String filter) {
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
    void getNonExistingTag() {
        // Get the tag
        webTestClient
            .get()
            .uri(ENTITY_API_URL_ID, Long.MAX_VALUE)
            .accept(MediaType.APPLICATION_PROBLEM_JSON)
            .exchange()
            .expectStatus()
            .isNotFound();
    }

    @Test
    void putExistingTag() throws Exception {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tag
        Tag updatedTag = tagRepository.findById(tag.getId()).block();
        updatedTag.name(UPDATED_NAME).color(UPDATED_COLOR).isForbidden(UPDATED_IS_FORBIDDEN).createdBy(UPDATED_CREATED_BY);
        TagDTO tagDTO = tagMapper.toDto(updatedTag);

        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedTagToMatchAllProperties(updatedTag);
    }

    @Test
    void putNonExistingTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tag.setId(longCount.incrementAndGet());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithIdMismatchTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tag.setId(longCount.incrementAndGet());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void putWithMissingIdPathParamTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tag.setId(longCount.incrementAndGet());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .put()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void partialUpdateTagWithPatch() throws Exception {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tag using partial update
        Tag partialUpdatedTag = new Tag();
        partialUpdatedTag.setId(tag.getId());

        partialUpdatedTag.isForbidden(UPDATED_IS_FORBIDDEN).createdBy(UPDATED_CREATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTag.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTag))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedTag, tag), getPersistedTag(tag));
    }

    @Test
    void fullUpdateTagWithPatch() throws Exception {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the tag using partial update
        Tag partialUpdatedTag = new Tag();
        partialUpdatedTag.setId(tag.getId());

        partialUpdatedTag.name(UPDATED_NAME).color(UPDATED_COLOR).isForbidden(UPDATED_IS_FORBIDDEN).createdBy(UPDATED_CREATED_BY);

        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, partialUpdatedTag.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(partialUpdatedTag))
            .exchange()
            .expectStatus()
            .isOk();

        // Validate the Tag in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertTagUpdatableFieldsEquals(partialUpdatedTag, getPersistedTag(partialUpdatedTag));
    }

    @Test
    void patchNonExistingTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tag.setId(longCount.incrementAndGet());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, tagDTO.getId())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithIdMismatchTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tag.setId(longCount.incrementAndGet());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL_ID, longCount.incrementAndGet())
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isBadRequest();

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void patchWithMissingIdPathParamTag() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        tag.setId(longCount.incrementAndGet());

        // Create the Tag
        TagDTO tagDTO = tagMapper.toDto(tag);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        webTestClient
            .patch()
            .uri(ENTITY_API_URL)
            .contentType(MediaType.valueOf("application/merge-patch+json"))
            .bodyValue(om.writeValueAsBytes(tagDTO))
            .exchange()
            .expectStatus()
            .isEqualTo(405);

        // Validate the Tag in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    void deleteTag() {
        // Initialize the database
        insertedTag = tagRepository.save(tag).block();

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the tag
        webTestClient
            .delete()
            .uri(ENTITY_API_URL_ID, tag.getId())
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus()
            .isNoContent();

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return tagRepository.count().block();
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

    protected Tag getPersistedTag(Tag tag) {
        return tagRepository.findById(tag.getId()).block();
    }

    protected void assertPersistedTagToMatchAllProperties(Tag expectedTag) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTagAllPropertiesEquals(expectedTag, getPersistedTag(expectedTag));
        assertTagUpdatableFieldsEquals(expectedTag, getPersistedTag(expectedTag));
    }

    protected void assertPersistedTagToMatchUpdatableProperties(Tag expectedTag) {
        // Test fails because reactive api returns an empty object instead of null
        // assertTagAllUpdatablePropertiesEquals(expectedTag, getPersistedTag(expectedTag));
        assertTagUpdatableFieldsEquals(expectedTag, getPersistedTag(expectedTag));
    }
}
