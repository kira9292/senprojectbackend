package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import com.senprojectbackend1.service.dto.UserProfileSimpleDTO;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Team} and its DTO {@link TeamDTO}.
 */
@Mapper(componentModel = "spring")
public interface TeamMapper extends EntityMapper<TeamDTO, Team> {
    @Mapping(target = "members", source = "members", qualifiedByName = "userProfileIdSet")
    TeamDTO toDto(Team s);

    Team toEntity(TeamDTO teamDTO);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    @Mapping(target = "login", source = "login")
    UserProfileSimpleDTO toDtoUserProfileId(UserProfile userProfile);

    @Named("userProfileIdSet")
    default Set<UserProfileSimpleDTO> toDtoUserProfileIdSet(Set<UserProfile> userProfile) {
        return userProfile.stream().map(this::toDtoUserProfileId).collect(Collectors.toSet());
    }
}
