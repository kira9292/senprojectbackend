package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static com.senprojectbackend1.domain.TagTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class TagTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Tag.class);
        Tag tag1 = getTagSample1();
        Tag tag2 = new Tag();
        assertThat(tag1).isNotEqualTo(tag2);

        tag2.setId(tag1.getId());
        assertThat(tag1).isEqualTo(tag2);

        tag2 = getTagSample2();
        assertThat(tag1).isNotEqualTo(tag2);
    }

    @Test
    void projectTest() {
        Tag tag = getTagRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        tag.addProject(projectBack);
        assertThat(tag.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getTags()).containsOnly(tag);

        tag.removeProject(projectBack);
        assertThat(tag.getProjects()).doesNotContain(projectBack);
        assertThat(projectBack.getTags()).doesNotContain(tag);

        tag.projects(new HashSet<>(Set.of(projectBack)));
        assertThat(tag.getProjects()).containsOnly(projectBack);
        assertThat(projectBack.getTags()).containsOnly(tag);

        tag.setProjects(new HashSet<>());
        assertThat(tag.getProjects()).doesNotContain(projectBack);
        assertThat(projectBack.getTags()).doesNotContain(tag);
    }
}
