package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.ExternalLink;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.service.dto.ExternalLinkDTO;
import com.senprojectbackend1.service.dto.ProjectDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link ExternalLink} and its DTO {@link ExternalLinkDTO}.
 */
@Mapper(componentModel = "spring")
public interface ExternalLinkMapper extends EntityMapper<ExternalLinkDTO, ExternalLink> {
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    ExternalLinkDTO toDto(ExternalLink s);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
