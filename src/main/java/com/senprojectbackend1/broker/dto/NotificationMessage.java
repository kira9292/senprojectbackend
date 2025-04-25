package com.senprojectbackend1.broker.dto;

import com.senprojectbackend1.domain.enumeration.NotificationType;
import java.io.Serializable;
import java.time.Instant;

/**
 * DTO pour les messages de notification échangés via le broker Kafka.
 * Cette classe est indépendante des entités du domaine et peut être sérialisée/désérialisée
 * sans dépendre des classes du domaine.
 */
public class NotificationMessage implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private String content;
    private Instant createdAt;
    private Instant readAt;
    private NotificationType type;
    private String entityId;
    private String action;
    private String userId;

    // Constructeur par défaut nécessaire pour la désérialisation
    public NotificationMessage() {}

    // Constructeur avec tous les champs
    public NotificationMessage(
        Long id,
        String content,
        Instant createdAt,
        Instant readAt,
        NotificationType type,
        String entityId,
        String action,
        String userId
    ) {
        this.id = id;
        this.content = content;
        this.createdAt = createdAt;
        this.readAt = readAt;
        this.type = type;
        this.entityId = entityId;
        this.action = action;
        this.userId = userId;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getReadAt() {
        return readAt;
    }

    public void setReadAt(Instant readAt) {
        this.readAt = readAt;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public String getEntityId() {
        return entityId;
    }

    public void setEntityId(String entityId) {
        this.entityId = entityId;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return (
            "NotificationMessage{" +
            "id=" +
            id +
            ", content='" +
            content +
            '\'' +
            ", createdAt=" +
            createdAt +
            ", readAt=" +
            readAt +
            ", type=" +
            type +
            ", entityId='" +
            entityId +
            '\'' +
            ", action='" +
            action +
            '\'' +
            ", userId='" +
            userId +
            '\'' +
            '}'
        );
    }
}
