package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.broker.NotificationBrokerService;
import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.criteria.NotificationCriteria;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.repository.NotificationRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.security.SecurityUtils;
import com.senprojectbackend1.service.NotificationService;
import com.senprojectbackend1.service.dto.NotificationDTO;
import com.senprojectbackend1.service.mapper.NotificationMapper;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Notification}.
 */
@Service
@Transactional
public class NotificationServiceImpl implements NotificationService {

    private final NotificationBrokerService notificationBrokerService;
    private final NotificationRepository notificationRepository;
    private final UserProfileRepository userProfileRepository;
    private static final Logger LOG = LoggerFactory.getLogger(NotificationServiceImpl.class);

    private final NotificationMapper notificationMapper;

    public NotificationServiceImpl(
        NotificationBrokerService notificationBrokerService,
        NotificationRepository notificationRepository,
        UserProfileRepository userProfileRepository,
        NotificationMapper notificationMapper
    ) {
        this.notificationBrokerService = notificationBrokerService;
        this.notificationRepository = notificationRepository;
        this.userProfileRepository = userProfileRepository;
        this.notificationMapper = notificationMapper;
    }

    @Override
    public Mono<NotificationDTO> save(NotificationDTO notificationDTO) {
        LOG.debug("Request to save Notification : {}", notificationDTO);
        return notificationRepository.save(notificationMapper.toEntity(notificationDTO)).map(notificationMapper::toDto);
    }

    @Override
    public Mono<NotificationDTO> update(NotificationDTO notificationDTO) {
        LOG.debug("Request to update Notification : {}", notificationDTO);
        return notificationRepository.save(notificationMapper.toEntity(notificationDTO)).map(notificationMapper::toDto);
    }

    @Override
    public Mono<NotificationDTO> partialUpdate(NotificationDTO notificationDTO) {
        LOG.debug("Request to partially update Notification : {}", notificationDTO);

        return notificationRepository
            .findById(notificationDTO.getId())
            .map(existingNotification -> {
                notificationMapper.partialUpdate(existingNotification, notificationDTO);

                return existingNotification;
            })
            .flatMap(notificationRepository::save)
            .map(notificationMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<NotificationDTO> findByCriteria(NotificationCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Notifications by Criteria");
        return notificationRepository.findByCriteria(criteria, pageable).map(notificationMapper::toDto);
    }

    /**
     * Find the count of notifications by criteria.
     * @param criteria filtering criteria
     * @return the count of notifications
     */
    public Mono<Long> countByCriteria(NotificationCriteria criteria) {
        LOG.debug("Request to get the count of all Notifications by Criteria");
        return notificationRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return notificationRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<NotificationDTO> findOne(Long id) {
        LOG.debug("Request to get Notification : {}", id);
        return notificationRepository.findById(id).map(notificationMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Notification : {}", id);
        return notificationRepository.deleteById(id);
    }

    /**
     * Create a notification for a user
     *
     * @param userId The user ID
     * @param content The content of the notification
     * @param type The type of the notification
     * @param entityId The ID of the entity related to the notification
     * @param action The action related to the notification (optional)
     * @return The created notification
     */
    @Override
    public Mono<Notification> createNotification(String userId, String content, NotificationType type, String entityId, String action) {
        LOG.debug("Request to create Notification for user: {}, type: {}, entityId: {}, action: {}", userId, type, entityId, action);

        Notification notification = new Notification();
        notification.setContent(content);
        notification.setCreatedAt(Instant.now());
        notification.setType(type);
        notification.setEntityId(entityId);
        notification.setAction(action);
        notification.setUserId(userId);

        return notificationRepository
            .save(notification)
            .doOnSuccess(savedNotification -> {
                LOG.info("Notification created successfully: {}", savedNotification);
                // Envoyer la notification via le broker
                notificationBrokerService.sendNotification(savedNotification);
            })
            .doOnError(error -> LOG.error("Error creating notification: {}", error.getMessage()));
    }

    /**
     * Create a notification for a user
     *
     * @param userId The user ID
     * @param content The content of the notification
     * @param type The type of the notification
     * @param entityId The ID of the entity related to the notification
     * @return The created notification
     */
    @Override
    public Mono<Notification> createNotification(String userId, String content, NotificationType type, String entityId) {
        return createNotification(userId, content, type, entityId, null);
    }

    /**
     * Mark a notification as read
     *
     * @param id The notification ID
     * @return The updated notification
     */
    @Override
    public Mono<Notification> markAsRead(Long id) {
        return notificationRepository
            .findById(id)
            .flatMap(notification -> {
                notification.setReadAt(Instant.now());
                return notificationRepository.save(notification);
            });
    }

    /**
     * Get all unread notifications for a user
     *
     * @param userId The user ID
     * @return A flux of unread notifications
     */
    @Override
    public Flux<Notification> getUnreadNotificationsForUser(String userId) {
        return notificationRepository.findByUserIdAndReadAtIsNull(userId);
    }

    /**
     * Get all notifications for a user
     *
     * @param userId The user ID
     * @return A flux of notifications
     */
    @Override
    public Flux<Notification> getAllNotificationsForUser(String userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @Override
    public Mono<Notification> createSystemNotification(String message, NotificationType type) {
        LOG.debug("Request to create system notification: {}, type: {}", message, type);
        return userProfileRepository
            .findAll()
            .flatMap(user -> createNotification(user.getId(), message, type, null))
            .collectList()
            .flatMap(notifications -> {
                if (notifications.isEmpty()) {
                    return Mono.empty();
                }
                return Mono.just(notifications.get(0));
            });
    }

    @Override
    public Flux<NotificationDTO> findByUserId(String userId, Pageable pageable) {
        LOG.debug("Request to get notifications for user: {}", userId);
        return notificationRepository.findByUserId(userId, pageable).map(notificationMapper::toDto);
    }

    @Override
    public Mono<Void> markAllAsRead(String userId) {
        LOG.debug("Request to mark all notifications as read for user: {}", userId);
        return notificationRepository
            .findByUserIdAndReadAtIsNull(userId)
            .flatMap(notification -> {
                notification.setReadAt(Instant.now());
                return notificationRepository.save(notification);
            })
            .then();
    }

    /**
     * Create notifications for specific users or all users
     *
     * @param content The content of the notification
     * @param type The type of the notification
     * @param entityLogins The logins of the users or "all" for all users
     * @param action The action related to the notification (optional)
     * @param targetIds Array of user IDs or "all" for all users
     * @return The created notifications
     */
    @Override
    public Flux<Notification> createNotifications(
        String content,
        NotificationType type,
        String entityLogins,
        String action,
        String[] targetIds
    ) {
        LOG.debug(
            "Request to create notifications for targets: {}, type: {}, entityLogins: {}, action: {}",
            targetIds,
            type,
            entityLogins,
            action
        );

        // Récupérer l'ID de l'émetteur de la notification
        return SecurityUtils.getCurrentUserLogin()
            .flatMapMany(emitterLogin ->
                userProfileRepository
                    .findOneByLogin(emitterLogin)
                    .flatMapMany(emitter -> {
                        String emitterId = emitter.getId();

                        // Si pas de targetIds spécifiés, envoyer à tous les utilisateurs
                        if (targetIds == null || targetIds.length == 0) {
                            return userProfileRepository
                                .findAll()
                                .flatMap(user -> createNotification(user.getId(), content, type, emitterId, action));
                        }

                        // Envoyer aux utilisateurs spécifiés
                        return Flux.fromArray(targetIds).flatMap(login ->
                            userProfileRepository
                                .findOneByLogin(login)
                                .switchIfEmpty(Mono.error(new RuntimeException("Utilisateur non trouvé: " + login)))
                                .flatMap(user -> createNotification(user.getId(), content, type, emitterId, action))
                        );
                    })
            );
    }

    @Override
    public Mono<Void> deleteByUserIdAndEntityIdAndType(String userId, String entityId, NotificationType type) {
        LOG.debug("Request to delete notifications for user: {}, entity: {}, type: {}", userId, entityId, type);
        return notificationRepository.deleteByUserIdAndEntityIdAndType(userId, entityId, type);
    }
}
