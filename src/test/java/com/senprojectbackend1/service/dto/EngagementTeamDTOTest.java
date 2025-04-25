package com.senprojectbackend1.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EngagementTeamDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(EngagementTeamDTO.class);
        EngagementTeamDTO engagementTeamDTO1 = new EngagementTeamDTO();
        engagementTeamDTO1.setId(1L);
        EngagementTeamDTO engagementTeamDTO2 = new EngagementTeamDTO();
        assertThat(engagementTeamDTO1).isNotEqualTo(engagementTeamDTO2);
        engagementTeamDTO2.setId(engagementTeamDTO1.getId());
        assertThat(engagementTeamDTO1).isEqualTo(engagementTeamDTO2);
        engagementTeamDTO2.setId(2L);
        assertThat(engagementTeamDTO1).isNotEqualTo(engagementTeamDTO2);
        engagementTeamDTO1.setId(null);
        assertThat(engagementTeamDTO1).isNotEqualTo(engagementTeamDTO2);
    }
}
