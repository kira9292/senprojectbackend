package com.senprojectbackend1.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ProjectSectionDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ProjectSectionDTO.class);
        ProjectSectionDTO projectSectionDTO1 = new ProjectSectionDTO();
        projectSectionDTO1.setId(1L);
        ProjectSectionDTO projectSectionDTO2 = new ProjectSectionDTO();
        assertThat(projectSectionDTO1).isNotEqualTo(projectSectionDTO2);
        projectSectionDTO2.setId(projectSectionDTO1.getId());
        assertThat(projectSectionDTO1).isEqualTo(projectSectionDTO2);
        projectSectionDTO2.setId(2L);
        assertThat(projectSectionDTO1).isNotEqualTo(projectSectionDTO2);
        projectSectionDTO1.setId(null);
        assertThat(projectSectionDTO1).isNotEqualTo(projectSectionDTO2);
    }
}
