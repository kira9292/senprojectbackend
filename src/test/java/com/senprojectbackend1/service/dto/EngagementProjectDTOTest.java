package com.senprojectbackend1.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EngagementProjectDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EngagementProjectDTO.class);
        EngagementProjectDTO engagementProjectDTO1 = new EngagementProjectDTO();
        engagementProjectDTO1.setId(1L);
        EngagementProjectDTO engagementProjectDTO2 = new EngagementProjectDTO();
        assertThat(engagementProjectDTO1).isNotEqualTo(engagementProjectDTO2);
        engagementProjectDTO2.setId(engagementProjectDTO1.getId());
        assertThat(engagementProjectDTO1).isEqualTo(engagementProjectDTO2);
        engagementProjectDTO2.setId(2L);
        assertThat(engagementProjectDTO1).isNotEqualTo(engagementProjectDTO2);
        engagementProjectDTO1.setId(null);
        assertThat(engagementProjectDTO1).isNotEqualTo(engagementProjectDTO2);
    }
}
