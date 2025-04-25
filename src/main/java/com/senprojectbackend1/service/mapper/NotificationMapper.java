package com.senprojectbackend1.service.mapper;

import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.service.dto.NotificationDTO;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link Notification} and its DTO {@link NotificationDTO}.
 */
@Mapper(componentModel = "spring")
public interface NotificationMapper extends EntityMapper<NotificationDTO, Notification> {
    @Mapping(target = "user", source = "user", qualifiedByName = "userProfileId")
    NotificationDTO toDto(Notification s);

    @Named("userProfileId")
    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "id", source = "id")
    UserProfileDTO toDtoUserProfileId(UserProfile userProfile);
}
