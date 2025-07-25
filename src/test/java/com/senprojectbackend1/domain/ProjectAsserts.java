package com.senprojectbackend1.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class ProjectAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProjectAllPropertiesEquals(Project expected, Project actual) {
        assertProjectAutoGeneratedPropertiesEquals(expected, actual);
        assertProjectAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProjectAllUpdatablePropertiesEquals(Project expected, Project actual) {
        assertProjectUpdatableFieldsEquals(expected, actual);
        assertProjectUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProjectAutoGeneratedPropertiesEquals(Project expected, Project actual) {
        assertThat(actual)
            .as("Verify Project auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProjectUpdatableFieldsEquals(Project expected, Project actual) {
        assertThat(actual)
            .as("Verify Project relevant properties")
            .satisfies(a -> assertThat(a.getTitle()).as("check title").isEqualTo(expected.getTitle()))
            .satisfies(a -> assertThat(a.getDescription()).as("check description").isEqualTo(expected.getDescription()))
            .satisfies(a -> assertThat(a.getShowcase()).as("check showcase").isEqualTo(expected.getShowcase()))
            .satisfies(a -> assertThat(a.getStatus()).as("check status").isEqualTo(expected.getStatus()))
            .satisfies(a -> assertThat(a.getCreatedAt()).as("check createdAt").isEqualTo(expected.getCreatedAt()))
            .satisfies(a -> assertThat(a.getUpdatedAt()).as("check updatedAt").isEqualTo(expected.getUpdatedAt()))
            .satisfies(a -> assertThat(a.getGithubUrl()).as("check githubUrl").isEqualTo(expected.getGithubUrl()))
            .satisfies(a -> assertThat(a.getWebsiteUrl()).as("check websiteUrl").isEqualTo(expected.getWebsiteUrl()))
            .satisfies(a -> assertThat(a.getDemoUrl()).as("check demoUrl").isEqualTo(expected.getDemoUrl()))
            .satisfies(a ->
                assertThat(a.getOpenToCollaboration()).as("check openToCollaboration").isEqualTo(expected.getOpenToCollaboration())
            )
            .satisfies(a -> assertThat(a.getOpenToFunding()).as("check openToFunding").isEqualTo(expected.getOpenToFunding()))
            .satisfies(a -> assertThat(a.getType()).as("check type").isEqualTo(expected.getType()))
            .satisfies(a -> assertThat(a.getTotalLikes()).as("check totalLikes").isEqualTo(expected.getTotalLikes()))
            .satisfies(a -> assertThat(a.getTotalShares()).as("check totalShares").isEqualTo(expected.getTotalShares()))
            .satisfies(a -> assertThat(a.getTotalViews()).as("check totalViews").isEqualTo(expected.getTotalViews()))
            .satisfies(a -> assertThat(a.getTotalComments()).as("check totalComments").isEqualTo(expected.getTotalComments()))
            .satisfies(a -> assertThat(a.getTotalFavorites()).as("check totalFavorites").isEqualTo(expected.getTotalFavorites()))
            .satisfies(a -> assertThat(a.getIsDeleted()).as("check isDeleted").isEqualTo(expected.getIsDeleted()))
            .satisfies(a -> assertThat(a.getCreatedBy()).as("check createdBy").isEqualTo(expected.getCreatedBy()))
            .satisfies(a -> assertThat(a.getLastUpdatedBy()).as("check lastUpdatedBy").isEqualTo(expected.getLastUpdatedBy()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertProjectUpdatableRelationshipsEquals(Project expected, Project actual) {
        assertThat(actual)
            .as("Verify Project relationships")
            .satisfies(a -> assertThat(a.getTeam()).as("check team").isEqualTo(expected.getTeam()))
            .satisfies(a -> assertThat(a.getFavoritedbies()).as("check favoritedbies").isEqualTo(expected.getFavoritedbies()))
            .satisfies(a -> assertThat(a.getTags()).as("check tags").isEqualTo(expected.getTags()));
    }
}
