package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.EngagementProjectTestSamples.*;
import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static com.senprojectbackend1.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EngagementProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EngagementProject.class);
        EngagementProject engagementProject1 = getEngagementProjectSample1();
        EngagementProject engagementProject2 = new EngagementProject();
        assertThat(engagementProject1).isNotEqualTo(engagementProject2);

        engagementProject2.setId(engagementProject1.getId());
        assertThat(engagementProject1).isEqualTo(engagementProject2);

        engagementProject2 = getEngagementProjectSample2();
        assertThat(engagementProject1).isNotEqualTo(engagementProject2);
    }

    @Test
    void userTest() {
        EngagementProject engagementProject = getEngagementProjectRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        engagementProject.setUser(userProfileBack);
        assertThat(engagementProject.getUser()).isEqualTo(userProfileBack);

        engagementProject.user(null);
        assertThat(engagementProject.getUser()).isNull();
    }

    @Test
    void projectTest() {
        EngagementProject engagementProject = getEngagementProjectRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        engagementProject.setProject(projectBack);
        assertThat(engagementProject.getProject()).isEqualTo(projectBack);

        engagementProject.project(null);
        assertThat(engagementProject.getProject()).isNull();
    }
}
