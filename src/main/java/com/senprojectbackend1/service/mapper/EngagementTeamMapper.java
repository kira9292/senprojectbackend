package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.EngagementTeam;
import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.service.dto.EngagementTeamDTO;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link EngagementTeam} and its DTO {@link EngagementTeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface EngagementTeamMapper extends EntityMapper<EngagementTeamDTO, EngagementTeam> {
    @Mapping(target = "team", source = "team", qualifiedByName = "teamId")
    @Mapping(target = "user", source = "user", qualifiedByName = "userProfileId")
    EngagementTeamDTO toDto(EngagementTeam s);

    @Named("teamId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    TeamDTO toDtoTeamId(Team team);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);
}
