package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.service.dto.*;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link UserProfile} and its DTO {@link UserProfileDTO}.
 */
@Mapper(componentModel = "spring")
public interface UserProfileMapper extends EntityMapper<UserProfileDTO, UserProfile> {
    @Mapping(target = "projects", source = "projects", qualifiedByName = "projectIdSet")
    @Mapping(target = "teams", source = "teams", qualifiedByName = "teamIdSet")
    UserProfileDTO toDto(UserProfile s);

    @Named("toSimpleDto")
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    @Mapping(target = "firstName", source = "firstName")
    @Mapping(target = "lastName", source = "lastName")
    @Mapping(target = "imageUrl", source = "imageUrl")
    UserProfileSimpleDTO toSimpleDto(UserProfile userProfile);

    @Mapping(target = "projects", ignore = true)
    @Mapping(target = "teams", ignore = true)
    UserProfile toEntity(UserProfileDTO userProfileDTO);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "title", source = "title")
    @Mapping(target = "type", source = "type")
    @Mapping(target = "description", source = "description")
    @Mapping(target = "totalComments", source = "totalComments")
    @Mapping(target = "totalLikes", source = "totalLikes")
    @Mapping(target = "totalShares", source = "totalShares")
    @Mapping(target = "totalViews", source = "totalViews")
    @Mapping(target = "showcase", source = "showcase")
    @Mapping(target = "openToCollaboration", source = "openToCollaboration")
    @Mapping(target = "openToFunding", source = "openToFunding")
    ProjectDTO toDtoProjectId(Project project);

    @Named("projectIdSet")
    default Set<ProjectDTO> toDtoProjectIdSet(Set<Project> project) {
        return project.stream().map(this::toDtoProjectId).collect(Collectors.toSet());
    }

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "name", source = "name")
    TeamSimpleDTO toDtoTeamId(Team team);

    @Named("teamIdSet")
    default Set<TeamSimpleDTO> toDtoTeamIdSet(Set<Team> team) {
        return team.stream().map(this::toDtoTeamId).collect(Collectors.toSet());
    }

    /**
     * Map only the UserProfile fields (without relationships) to a UserProfileCompleteDTO
     */
    void userProfileToUserProfileDTO(UserProfile userProfile, @MappingTarget UserProfileDTO completeDTO);
    void userProfileToUserProfileSimpleDTO(UserProfile userProfile, @MappingTarget UserProfileSimpleDTO completeDTO);
}
