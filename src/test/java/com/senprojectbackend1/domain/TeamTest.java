package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.TeamTestSamples.*;
import static com.senprojectbackend1.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TeamTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Team.class);
        Team team1 = getTeamSample1();
        Team team2 = new Team();
        assertThat(team1).isNotEqualTo(team2);

        team2.setId(team1.getId());
        assertThat(team1).isEqualTo(team2);

        team2 = getTeamSample2();
        assertThat(team1).isNotEqualTo(team2);
    }

    @Test
    void membersTest() {
        Team team = getTeamRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        team.addMembers(userProfileBack);
        assertThat(team.getMembers()).containsOnly(userProfileBack);

        team.removeMembers(userProfileBack);
        assertThat(team.getMembers()).doesNotContain(userProfileBack);

        team.members(new HashSet<>(Set.of(userProfileBack)));
        assertThat(team.getMembers()).containsOnly(userProfileBack);

        team.setMembers(new HashSet<>());
        assertThat(team.getMembers()).doesNotContain(userProfileBack);
    }
}
