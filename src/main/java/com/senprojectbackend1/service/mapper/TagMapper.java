package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSimple2DTO;
import com.senprojectbackend1.service.dto.TagDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Tag} and its DTO {@link TagDTO}.
 */
@Mapper(componentModel = "spring")
public interface TagMapper extends EntityMapper<TagDTO, Tag> {
    TagDTO toDto(Tag s);

    @Mapping(target = "projects", ignore = true)
    Tag toEntity(TagDTO tagDTO);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);

    @Named("projectIdSet")
    default Set<ProjectDTO> toDtoProjectIdSet(Set<Project> project) {
        return project.stream().map(this::toDtoProjectId).collect(Collectors.toSet());
    }

    Project toEntity(ProjectSimple2DTO projectSimple2DTO);
}
