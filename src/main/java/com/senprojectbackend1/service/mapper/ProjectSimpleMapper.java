package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.service.dto.ProjectSimpleDTO;
import com.senprojectbackend1.service.dto.TagDTO;
import com.senprojectbackend1.service.dto.TeamDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectSimpleDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectSimpleMapper extends EntityMapper<ProjectSimpleDTO, Project> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagIdSet")
    ProjectSimpleDTO toDto(Project s);

    Project toEntity(ProjectSimpleDTO projectDTO);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TeamDTO toDtoTeamId(Team team);

    @Named("tagId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TagDTO toDtoTagId(Tag tag);

    @Named("tagIdSet")
    default Set<TagDTO> toDtoTagIdSet(Set<Tag> tag) {
        return tag.stream().map(this::toDtoTagId).collect(Collectors.toSet());
    }
}
