package com.senprojectbackend1.domain.enumeration;

/**
 * The NotificationType enumeration.
 */
public enum NotificationType {
    TEAM_INVITATION,
    TEAM_JOINED,
    TEAM_REJECTED,
    TEAM_LEFT,
    // project Notifications
    PROJECT_APPROVED,
    PROJECT_REJECTED,
    PROJECT_DELETED,
    PROJECT_UPDATED,
    PROJECT_COMMENT,
    // user Notifications
    SYSTEM,
    HEARTBEAT,
    INFO,
    WARNING,
}
