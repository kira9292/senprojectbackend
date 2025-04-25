package com.senprojectbackend1.service.mapper;

import static com.senprojectbackend1.domain.EngagementProjectAsserts.*;
import static com.senprojectbackend1.domain.EngagementProjectTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EngagementProjectMapperTest {

    private EngagementProjectMapper engagementProjectMapper;

    @BeforeEach
    void setUp() {
        engagementProjectMapper = new EngagementProjectMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEngagementProjectSample1();
        var actual = engagementProjectMapper.toEntity(engagementProjectMapper.toDto(expected));
        assertEngagementProjectAllPropertiesEquals(expected, actual);
    }
}
