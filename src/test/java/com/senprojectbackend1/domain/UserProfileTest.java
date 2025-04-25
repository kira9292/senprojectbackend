package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static com.senprojectbackend1.domain.TeamTestSamples.*;
import static com.senprojectbackend1.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class UserProfileTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(UserProfile.class);
        UserProfile userProfile1 = getUserProfileSample1();
        UserProfile userProfile2 = new UserProfile();

        // Définir des IDs différents
        userProfile1.setId("id1");
        userProfile2.setId("id2");

        assertThat(userProfile1).isNotEqualTo(userProfile2);

        userProfile2.setId(userProfile1.getId());
        assertThat(userProfile1).isEqualTo(userProfile2);

        userProfile2 = getUserProfileSample2();
        assertThat(userProfile1).isNotEqualTo(userProfile2);
    }

    @Test
    void projectTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        userProfile.addProject(projectBack);
        assertThat(userProfile.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getFavoritedbies()).containsOnly(userProfile);

        userProfile.projects(new HashSet<>(Set.of(projectBack)));
        assertThat(userProfile.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getFavoritedbies()).containsOnly(userProfile);

        userProfile.setProjects(new HashSet<>());
        assertThat(userProfile.getProjects()).doesNotContain(projectBack);
        assertThat(projectBack.getFavoritedbies()).doesNotContain(userProfile);
    }

    @Test
    void teamTest() {
        UserProfile userProfile = getUserProfileRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        userProfile.addTeam(teamBack);
        assertThat(userProfile.getTeams()).containsOnly(teamBack);
        assertThat(teamBack.getMembers()).containsOnly(userProfile);

        userProfile.teams(new HashSet<>(Set.of(teamBack)));
        assertThat(userProfile.getTeams()).containsOnly(teamBack);
        assertThat(teamBack.getMembers()).containsOnly(userProfile);

        userProfile.setTeams(new HashSet<>());
        assertThat(userProfile.getTeams()).doesNotContain(teamBack);
        assertThat(teamBack.getMembers()).doesNotContain(userProfile);
    }
}
