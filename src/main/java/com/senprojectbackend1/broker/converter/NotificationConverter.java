package com.senprojectbackend1.broker.converter;

import com.senprojectbackend1.broker.dto.NotificationMessage;
import com.senprojectbackend1.domain.Notification;
import org.springframework.stereotype.Component;

/**
 * Convertisseur pour transformer les entités Notification en NotificationMessage et vice versa.
 * Cette classe permet de découpler le broker des entités du domaine.
 */
@Component
public class NotificationConverter {

    /**
     * Convertit une entité Notification en NotificationMessage.
     *
     * @param notification L'entité Notification à convertir
     * @return Le NotificationMessage correspondant
     */
    public NotificationMessage toMessage(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationMessage message = new NotificationMessage();
        message.setId(notification.getId());
        message.setContent(notification.getContent());
        message.setCreatedAt(notification.getCreatedAt());
        message.setReadAt(notification.getReadAt());
        message.setType(notification.getType());
        message.setEntityId(notification.getEntityId());
        message.setAction(notification.getAction());
        message.setUserId(notification.getUserId());

        return message;
    }

    /**
     * Convertit un NotificationMessage en entité Notification.
     * Note: Cette méthode ne remplit pas la relation avec UserProfile.
     *
     * @param message Le NotificationMessage à convertir
     * @return L'entité Notification correspondante
     */
    public Notification toEntity(NotificationMessage message) {
        if (message == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setId(message.getId());
        notification.setContent(message.getContent());
        notification.setCreatedAt(message.getCreatedAt());
        notification.setReadAt(message.getReadAt());
        notification.setType(message.getType());
        notification.setEntityId(message.getEntityId());
        notification.setAction(message.getAction());
        notification.setUserId(message.getUserId());

        return notification;
    }
}
