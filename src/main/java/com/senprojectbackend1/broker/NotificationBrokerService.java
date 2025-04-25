package com.senprojectbackend1.broker;

import com.senprojectbackend1.broker.dto.NotificationMessage;
import com.senprojectbackend1.domain.Notification;
import reactor.core.publisher.Flux;

/**
 * Service pour l'interaction avec le broker de notifications.
 * Cette interface normalise l'utilisation du broker dans l'application.
 */
public interface NotificationBrokerService {
    /**
     * Envoie une notification via le broker.
     *
     * @param notification La notification à envoyer
     * @return true si l'envoi a réussi, false sinon
     */
    boolean sendNotification(Notification notification);

    /**
     * Envoie un message de notification directement via le broker.
     *
     * @param message Le message de notification à envoyer
     * @return true si l'envoi a réussi, false sinon
     */
    boolean sendNotificationMessage(NotificationMessage message);

    /**
     * Récupère le flux de notifications.
     *
     * @return Le flux de notifications
     */
    Flux<Notification> getNotificationFlux();

    /**
     * Récupère le flux de messages de notification.
     *
     * @return Le flux de messages de notification
     */
    Flux<NotificationMessage> getNotificationMessageFlux();
}
