package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static com.senprojectbackend1.domain.TagTestSamples.*;
import static com.senprojectbackend1.domain.TeamTestSamples.*;
import static com.senprojectbackend1.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ProjectTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Project.class);
        Project project1 = getProjectSample1();
        Project project2 = new Project();
        assertThat(project1).isNotEqualTo(project2);

        project2.setId(project1.getId());
        assertThat(project1).isEqualTo(project2);

        project2 = getProjectSample2();
        assertThat(project1).isNotEqualTo(project2);
    }

    @Test
    void teamTest() {
        Project project = getProjectRandomSampleGenerator();
        Team teamBack = getTeamRandomSampleGenerator();

        project.setTeam(teamBack);
        assertThat(project.getTeam()).isEqualTo(teamBack);

        project.team(null);
        assertThat(project.getTeam()).isNull();
    }

    @Test
    void favoritedbyTest() {
        Project project = getProjectRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        project.addFavoritedby(userProfileBack);
        assertThat(project.getFavoritedbies()).containsOnly(userProfileBack);

        project.removeFavoritedby(userProfileBack);
        assertThat(project.getFavoritedbies()).doesNotContain(userProfileBack);

        project.favoritedbies(new HashSet<>(Set.of(userProfileBack)));
        assertThat(project.getFavoritedbies()).containsOnly(userProfileBack);

        project.setFavoritedbies(new HashSet<>());
        assertThat(project.getFavoritedbies()).doesNotContain(userProfileBack);
    }

    @Test
    void tagsTest() {
        Project project = getProjectRandomSampleGenerator();
        Tag tagBack = getTagRandomSampleGenerator();

        project.addTags(tagBack);
        assertThat(project.getTags()).containsOnly(tagBack);

        project.removeTags(tagBack);
        assertThat(project.getTags()).doesNotContain(tagBack);

        project.tags(new HashSet<>(Set.of(tagBack)));
        assertThat(project.getTags()).containsOnly(tagBack);

        project.setTags(new HashSet<>());
        assertThat(project.getTags()).doesNotContain(tagBack);
    }
}
