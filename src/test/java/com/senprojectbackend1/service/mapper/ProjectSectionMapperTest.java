package com.senprojectbackend1.service.mapper;

import static com.senprojectbackend1.domain.ProjectSectionAsserts.*;
import static com.senprojectbackend1.domain.ProjectSectionTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectSectionMapperTest {

    private ProjectSectionMapper projectSectionMapper;

    @BeforeEach
    void setUp() {
        projectSectionMapper = new ProjectSectionMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProjectSectionSample1();
        var actual = projectSectionMapper.toEntity(projectSectionMapper.toDto(expected));
        assertProjectSectionAllPropertiesEquals(expected, actual);
    }
}
