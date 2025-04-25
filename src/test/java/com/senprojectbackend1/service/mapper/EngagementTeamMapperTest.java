package com.senprojectbackend1.service.mapper;

import static com.senprojectbackend1.domain.EngagementTeamAsserts.*;
import static com.senprojectbackend1.domain.EngagementTeamTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class EngagementTeamMapperTest {

    private EngagementTeamMapper engagementTeamMapper;

    @BeforeEach
    void setUp() {
        engagementTeamMapper = new EngagementTeamMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getEngagementTeamSample1();
        var actual = engagementTeamMapper.toEntity(engagementTeamMapper.toDto(expected));
        assertEngagementTeamAllPropertiesEquals(expected, actual);
    }
}
