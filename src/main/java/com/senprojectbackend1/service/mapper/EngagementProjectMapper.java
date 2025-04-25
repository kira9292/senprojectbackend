package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.service.dto.EngagementProjectDTO;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EngagementProject} and its DTO {@link EngagementProjectDTO}.
 */
@Mapper(componentModel = "spring")
public interface EngagementProjectMapper extends EntityMapper<EngagementProjectDTO, EngagementProject> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userProfileId")
    @Mapping(target = "project", source = "project", qualifiedByName = "projectId")
    EngagementProjectDTO toDto(EngagementProject s);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);

    @Named("projectId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    ProjectDTO toDtoProjectId(Project project);
}
