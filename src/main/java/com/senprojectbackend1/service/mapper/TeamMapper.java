package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Team;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.repository.UserProfileMembershipProjection;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.dto.TeamMemberDetailsDTO;
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

    @Named("userProfileMembershipProjectionToDetailsDTO")
    default TeamMemberDetailsDTO toTeamMemberDetailsDTO(UserProfileMembershipProjection projection) {
        if (projection == null) return null;
        UserProfileSimpleDTO simple = new UserProfileSimpleDTO();
        simple.setId(projection.getId());
        simple.setLogin(projection.getLogin());
        simple.setFirstName(projection.getFirstName());
        simple.setLastName(projection.getLastName());
        simple.setEmail(projection.getEmail());
        simple.setImageUrl(projection.getImageUrl());
        // Les autres champs peuvent être ajoutés si besoin
        return new TeamMemberDetailsDTO(simple, projection.getStatus(), projection.getRole());
    }
}
