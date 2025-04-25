package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.ExternalLinkTestSamples.*;
import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExternalLinkTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExternalLink.class);
        ExternalLink externalLink1 = getExternalLinkSample1();
        ExternalLink externalLink2 = new ExternalLink();
        assertThat(externalLink1).isNotEqualTo(externalLink2);

        externalLink2.setId(externalLink1.getId());
        assertThat(externalLink1).isEqualTo(externalLink2);

        externalLink2 = getExternalLinkSample2();
        assertThat(externalLink1).isNotEqualTo(externalLink2);
    }

    @Test
    void projectTest() {
        ExternalLink externalLink = getExternalLinkRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        externalLink.setProject(projectBack);
        assertThat(externalLink.getProject()).isEqualTo(projectBack);

        externalLink.project(null);
        assertThat(externalLink.getProject()).isNull();
    }
}
