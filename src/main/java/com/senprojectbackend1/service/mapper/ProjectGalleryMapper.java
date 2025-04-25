package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.ProjectGallery;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectGalleryDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectGallery} and its DTO {@link ProjectGalleryDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectGalleryMapper extends EntityMapper<ProjectGalleryDTO, ProjectGallery> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    ProjectGalleryDTO toDto(ProjectGallery s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
