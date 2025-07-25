package com.senprojectbackend1.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class TeamAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamAllPropertiesEquals(Team expected, Team actual) {
        assertTeamAutoGeneratedPropertiesEquals(expected, actual);
        assertTeamAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamAllUpdatablePropertiesEquals(Team expected, Team actual) {
        assertTeamUpdatableFieldsEquals(expected, actual);
        assertTeamUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamAutoGeneratedPropertiesEquals(Team expected, Team actual) {
        assertThat(actual)
            .as("Verify Team auto generated properties")
            .satisfies(a -> assertThat(a.getId()).as("check id").isEqualTo(expected.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertTeamUpdatableFieldsEquals(Team expected, Team actual) {
        assertThat(actual)
            .as("Verify Team relevant properties")
            .satisfies(a -> assertThat(a.getName()).as("check name").isEqualTo(expected.getName()))
            .satisfies(a -> assertThat(a.getDescription()).as("check description").isEqualTo(expected.getDescription()))
            .satisfies(a -> assertThat(a.getLogo()).as("check logo").isEqualTo(expected.getLogo()))
            .satisfies(a -> assertThat(a.getCreatedAt()).as("check createdAt").isEqualTo(expected.getCreatedAt()))
            .satisfies(a -> assertThat(a.getUpdatedAt()).as("check updatedAt").isEqualTo(expected.getUpdatedAt()))
            .satisfies(a -> assertThat(a.getVisibility()).as("check visibility").isEqualTo(expected.getVisibility()))
            .satisfies(a -> assertThat(a.getTotalLikes()).as("check totalLikes").isEqualTo(expected.getTotalLikes()))
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
    public static void assertTeamUpdatableRelationshipsEquals(Team expected, Team actual) {
        assertThat(actual)
            .as("Verify Team relationships")
            .satisfies(a -> assertThat(a.getMembers()).as("check members").isEqualTo(expected.getMembers()));
    }
}
