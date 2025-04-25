package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.EngagementTeamTestSamples.*;
import static com.senprojectbackend1.domain.TeamTestSamples.*;
import static com.senprojectbackend1.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class EngagementTeamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(EngagementTeam.class);
        EngagementTeam engagementTeam1 = getEngagementTeamSample1();
        EngagementTeam engagementTeam2 = new EngagementTeam();
        assertThat(engagementTeam1).isNotEqualTo(engagementTeam2);

        engagementTeam2.setId(engagementTeam1.getId());
        assertThat(engagementTeam1).isEqualTo(engagementTeam2);

        engagementTeam2 = getEngagementTeamSample2();
        assertThat(engagementTeam1).isNotEqualTo(engagementTeam2);
    }

    @Test
    void teamTest() {
        EngagementTeam engagementTeam = getEngagementTeamRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        engagementTeam.setTeam(teamBack);
        assertThat(engagementTeam.getTeam()).isEqualTo(teamBack);

        engagementTeam.team(null);
        assertThat(engagementTeam.getTeam()).isNull();
    }

    @Test
    void userTest() {
        EngagementTeam engagementTeam = getEngagementTeamRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        engagementTeam.setUser(userProfileBack);
        assertThat(engagementTeam.getUser()).isEqualTo(userProfileBack);

        engagementTeam.user(null);
        assertThat(engagementTeam.getUser()).isNull();
    }
}
