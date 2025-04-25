package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.ProjectSection;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSectionDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ProjectSection} and its DTO {@link ProjectSectionDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectSectionMapper extends EntityMapper<ProjectSectionDTO, ProjectSection> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    ProjectSectionDTO toDto(ProjectSection s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
