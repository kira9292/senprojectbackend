package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSimple2DTO;
import com.senprojectbackend1.service.dto.TagDTO;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Project} and its DTO {@link ProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface ProjectMapper extends EntityMapper<ProjectDTO, Project> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    @Mapping(target = "favoritedbies", source = "favoritedbies", qualifiedByName = "userProfileIdSet")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagIdSet")
    ProjectDTO toDto(Project s);

    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "showcase", source = "showcase")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "openToCollaboration", source = "openToCollaboration")
    @Mapping(target = "openToFunding", source = "openToFunding")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "totalLikes", source = "totalLikes")
    @Mapping(target = "totalShares", source = "totalShares")
    @Mapping(target = "totalViews", source = "totalViews")
    @Mapping(target = "totalComments", source = "totalComments")
    @Mapping(target = "totalFavorites", source = "totalFavorites")
    @Mapping(target = "tags", source = "tags", qualifiedByName = "tagIdSet")
    ProjectSimple2DTO toSimpleDto(Project project);

    Project toEntity(ProjectDTO projectDTO);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    @Mapping(target = "logo", source = "logo")
    @Mapping(target = "members", ignore = true)
    TeamDTO toDtoTeamId(Team team);

    @Named("userProfileId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "teams", ignore = true)
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);

    @Named("userProfileIdSet")
    default Set<UserProfileDTO> toDtoUserProfileIdSet(Set<UserProfile> userProfile) {
        return userProfile.stream().map(this::toDtoUserProfileId).collect(Collectors.toSet());
    }

    @Named("tagId")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TagDTO toDtoTagId(Tag tag);

    @Named("tagIdSet")
    default Set<TagDTO> toDtoTagIdSet(Set<Tag> tag) {
        return tag.stream().map(this::toDtoTagId).collect(Collectors.toSet());
    }
}
