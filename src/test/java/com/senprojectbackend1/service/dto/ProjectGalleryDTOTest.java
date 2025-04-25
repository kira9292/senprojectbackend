package com.senprojectbackend1.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectGalleryDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectGalleryDTO.class);
        ProjectGalleryDTO projectGalleryDTO1 = new ProjectGalleryDTO();
        projectGalleryDTO1.setId(1L);
        ProjectGalleryDTO projectGalleryDTO2 = new ProjectGalleryDTO();
        assertThat(projectGalleryDTO1).isNotEqualTo(projectGalleryDTO2);
        projectGalleryDTO2.setId(projectGalleryDTO1.getId());
        assertThat(projectGalleryDTO1).isEqualTo(projectGalleryDTO2);
        projectGalleryDTO2.setId(2L);
        assertThat(projectGalleryDTO1).isNotEqualTo(projectGalleryDTO2);
        projectGalleryDTO1.setId(null);
        assertThat(projectGalleryDTO1).isNotEqualTo(projectGalleryDTO2);
    }
}
