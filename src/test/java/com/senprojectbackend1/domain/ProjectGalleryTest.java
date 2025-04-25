package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.ProjectGalleryTestSamples.*;
import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectGalleryTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectGallery.class);
        ProjectGallery projectGallery1 = getProjectGallerySample1();
        ProjectGallery projectGallery2 = new ProjectGallery();
        assertThat(projectGallery1).isNotEqualTo(projectGallery2);

        projectGallery2.setId(projectGallery1.getId());
        assertThat(projectGallery1).isEqualTo(projectGallery2);

        projectGallery2 = getProjectGallerySample2();
        assertThat(projectGallery1).isNotEqualTo(projectGallery2);
    }

    @Test
    void projectTest() {
        ProjectGallery projectGallery = getProjectGalleryRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        projectGallery.setProject(projectBack);
        assertThat(projectGallery.getProject()).isEqualTo(projectBack);

        projectGallery.project(null);
        assertThat(projectGallery.getProject()).isNull();
    }
}
