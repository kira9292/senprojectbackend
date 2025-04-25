package com.senprojectbackend1.service.mapper;

import static com.senprojectbackend1.domain.ProjectGalleryAsserts.*;
import static com.senprojectbackend1.domain.ProjectGalleryTestSamples.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ProjectGalleryMapperTest {

    private ProjectGalleryMapper projectGalleryMapper;

    @BeforeEach
    void setUp() {
        projectGalleryMapper = new ProjectGalleryMapperImpl();
    }

    @Test
    void shouldConvertToDtoAndBack() {
        var expected = getProjectGallerySample1();
        var actual = projectGalleryMapper.toEntity(projectGalleryMapper.toDto(expected));
        assertProjectGalleryAllPropertiesEquals(expected, actual);
    }
}
