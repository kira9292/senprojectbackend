package com.senprojectbackend1.broker;

import com.senprojectbackend1.broker.converter.NotificationConverter;
import com.senprojectbackend1.broker.dto.NotificationMessage;
import com.senprojectbackend1.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Component;

/**
 * Producteur Kafka pour l'envoi de notifications.
 * Utilise NotificationMessage pour découpler le broker des entités du domaine.
 */
@Component
public class KafkaProducer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaProducer.class);
    private final StreamBridge streamBridge;
    private final NotificationConverter notificationConverter;
    private static final String NOTIFICATION_DESTINATION = "notificationProducer-out-0";

    public KafkaProducer(StreamBridge streamBridge, NotificationConverter notificationConverter) {
        this.streamBridge = streamBridge;
        this.notificationConverter = notificationConverter;
    }

    /**
     * Envoie une notification via Kafka.
     *
     * @param notification La notification à envoyer
     * @return true si l'envoi a réussi, false sinon
     */
    public boolean sendNotification(Notification notification) {
        LOG.debug("Sending notification to Kafka: {}", notification);

        // Convertir l'entité en DTO pour le broker
        NotificationMessage message = notificationConverter.toMessage(notification);

        boolean result = streamBridge.send(NOTIFICATION_DESTINATION, message);
        if (result) {
            LOG.info("Successfully sent notification to Kafka: type={}, userId={}", notification.getType(), notification.getUserId());
        } else {
            LOG.error("Failed to send notification to Kafka: type={}, userId={}", notification.getType(), notification.getUserId());
        }
        return result;
    }

    /**
     * Envoie un message de notification directement.
     *
     * @param message Le message de notification à envoyer
     * @return true si l'envoi a réussi, false sinon
     */
    public boolean sendNotificationMessage(NotificationMessage message) {
        LOG.debug("Sending notification message to Kafka: {}", message);

        boolean result = streamBridge.send(NOTIFICATION_DESTINATION, message);
        if (result) {
            LOG.info("Successfully sent notification message to Kafka: type={}, userId={}", message.getType(), message.getUserId());
        } else {
            LOG.error("Failed to send notification message to Kafka: type={}, userId={}", message.getType(), message.getUserId());
        }
        return result;
    }
}
