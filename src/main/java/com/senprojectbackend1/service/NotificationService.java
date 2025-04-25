package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.criteria.NotificationCriteria;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.service.dto.NotificationDTO;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link Notification}.
 */
public interface NotificationService {
    /**
     * Save a notification.
     *
     * @param notificationDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<NotificationDTO> save(NotificationDTO notificationDTO);

    /**
     * Updates a notification.
     *
     * @param notificationDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<NotificationDTO> update(NotificationDTO notificationDTO);

    public Flux<Notification> getAllNotificationsForUser(String userId);

    /**
     * Partially updates a notification.
     *
     * @param notificationDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<NotificationDTO> partialUpdate(NotificationDTO notificationDTO);
    /**
     * Find notifications by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<NotificationDTO> findByCriteria(NotificationCriteria criteria, Pageable pageable);

    /**
     * Find the count of notifications by criteria.
     * @param criteria filtering criteria
     * @return the count of notifications
     */
    public Mono<Long> countByCriteria(NotificationCriteria criteria);

    /**
     * Returns the number of notifications available.
     * @return the number of entities in the database.
     *
     */
    Mono<Long> countAll();

    /**
     * Get the "id" notification.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<NotificationDTO> findOne(Long id);

    /**
     * Delete the "id" notification.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(Long id);

    /**
     * Create a notification for a user.
     *
     * @param userId the id of the user.
     * @param content the content of the notification.
     * @param type the type of the notification.
     * @param entityId the id of the entity related to the notification.
     * @return the created notification.
     */
    public Mono<Notification> createNotification(String userId, String content, NotificationType type, String entityId);

    /**
     * Create a notification for a specific user.
     *
     * @param userId the ID of the user to notify
     * @param content the content of the notification
     * @param type the type of notification
     * @param entityId the ID of the related entity (optional)
     * @param action the action to perform (optional)
     * @return the created notification
     */
    Mono<Notification> createNotification(String userId, String content, NotificationType type, String entityId, String action);

    public Mono<Notification> markAsRead(Long id);

    public Flux<Notification> getUnreadNotificationsForUser(String userId);

    /**
     * Create a system notification for all users.
     *
     * @param message the message to send
     * @param type the type of notification (SYSTEM, HEARTBEAT, INFO, WARNING)
     * @return the created notification
     */
    Mono<Notification> createSystemNotification(String message, NotificationType type);

    /**
     * Find notifications by user ID.
     *
     * @param userId the user ID.
     * @param pageable the pagination information.
     * @return the list of notifications.
     */
    Flux<NotificationDTO> findByUserId(String userId, Pageable pageable);

    /**
     * Mark all notifications as read for the current user.
     *
     * @param userId the ID of the user
     * @return a Mono completing when all notifications are marked as read
     */
    Mono<Void> markAllAsRead(String userId);

    /**
     * Create notifications for specific users or all users.
     *
     * @param content the content of the notification
     * @param type the type of notification
     * @param entityLogins the logins of the related entities (optional)
     * @param action the action to perform (optional)
     * @param targetIds array of user logins to notify. If empty or null, notification will be sent to all users
     * @return the created notifications
     */
    Flux<Notification> createNotifications(String content, NotificationType type, String entityLogins, String action, String[] targetIds);

    Mono<Void> deleteByUserIdAndEntityIdAndType(String userId, String entityId, NotificationType type);
}
