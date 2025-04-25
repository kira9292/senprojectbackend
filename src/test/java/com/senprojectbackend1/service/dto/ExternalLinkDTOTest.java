package com.senprojectbackend1.service.dto;

import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ExternalLinkDTOTest {

    @Test
    void dtoEqualsVerifier() throws Exception {
        TestUtil.equalsVerifier(ExternalLinkDTO.class);
        ExternalLinkDTO externalLinkDTO1 = new ExternalLinkDTO();
        externalLinkDTO1.setId(1L);
        ExternalLinkDTO externalLinkDTO2 = new ExternalLinkDTO();
        assertThat(externalLinkDTO1).isNotEqualTo(externalLinkDTO2);
        externalLinkDTO2.setId(externalLinkDTO1.getId());
        assertThat(externalLinkDTO1).isEqualTo(externalLinkDTO2);
        externalLinkDTO2.setId(2L);
        assertThat(externalLinkDTO1).isNotEqualTo(externalLinkDTO2);
        externalLinkDTO1.setId(null);
        assertThat(externalLinkDTO1).isNotEqualTo(externalLinkDTO2);
    }
}
