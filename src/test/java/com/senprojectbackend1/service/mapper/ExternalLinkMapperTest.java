package com.senprojectbackend1.service.mapper;

import static com.senprojectbackend1.domain.ExternalLinkAsserts.*;
import static com.senprojectbackend1.domain.ExternalLinkTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ExternalLinkMapperTest {

    private ExternalLinkMapper externalLinkMapper;

    @BeforeEach
    void setUp() {
        externalLinkMapper = new ExternalLinkMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getExternalLinkSample1();
        var actual = externalLinkMapper.toEntity(externalLinkMapper.toDto(expected));
        assertExternalLinkAllPropertiesEquals(expected, actual);
    }
}
