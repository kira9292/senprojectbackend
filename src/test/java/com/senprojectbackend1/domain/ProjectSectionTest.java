package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.ProjectSectionTestSamples.*;
import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSectionTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSection.class);
        ProjectSection projectSection1 = getProjectSectionSample1();
        ProjectSection projectSection2 = new ProjectSection();
        assertThat(projectSection1).isNotEqualTo(projectSection2);

        projectSection2.setId(projectSection1.getId());
        assertThat(projectSection1).isEqualTo(projectSection2);

        projectSection2 = getProjectSectionSample2();
        assertThat(projectSection1).isNotEqualTo(projectSection2);
    }

    @Test
    void projectTest() {
        ProjectSection projectSection = getProjectSectionRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        projectSection.setProject(projectBack);
        assertThat(projectSection.getProject()).isEqualTo(projectBack);

        projectSection.project(null);
        assertThat(projectSection.getProject()).isNull();
    }
}
