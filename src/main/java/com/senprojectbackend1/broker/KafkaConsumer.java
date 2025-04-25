package com.senprojectbackend1.broker;

import com.senprojectbackend1.broker.converter.NotificationConverter;
import com.senprojectbackend1.broker.dto.NotificationMessage;
import com.senprojectbackend1.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Consommateur Kafka pour la réception de notifications.
 * Utilise NotificationMessage pour découpler le broker des entités du domaine.
 */
@Component
public class KafkaConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumer.class);
    private final Sinks.Many<NotificationMessage> notificationSink;
    private final NotificationConverter notificationConverter;

    public KafkaConsumer(NotificationConverter notificationConverter) {
        LOG.info("Initializing KafkaConsumer with notification sink");
        this.notificationSink = Sinks.many().multicast().onBackpressureBuffer();
        this.notificationConverter = notificationConverter;
    }

    /**
     * Récupère le flux de messages de notification.
     *
     * @return Le flux de messages de notification
     */
    public Flux<NotificationMessage> getNotificationMessageFlux() {
        return this.notificationSink.asFlux();
    }

    /**
     * Récupère le flux de notifications converties en entités.
     *
     * @return Le flux de notifications
     */
    public Flux<Notification> getNotificationFlux() {
        return this.notificationSink.asFlux().map(notificationConverter::toEntity);
    }

    /**
     * Accepte un message de notification.
     *
     * @param message Le message de notification à accepter
     */
    public void acceptNotificationMessage(NotificationMessage message) {
        LOG.info("Received notification message in KafkaConsumer: type={}, userId={}", message.getType(), message.getUserId());

        if (notificationSink != null) {
            Sinks.EmitResult result = notificationSink.tryEmitNext(message);
            if (result.isFailure()) {
                LOG.error("Failed to emit notification message to sink: {}", result);
            } else {
                LOG.debug("Successfully emitted notification message to sink");
            }
        } else {
            LOG.error("Notification sink is null!");
        }
    }

    /**
     * Accepte une notification.
     *
     * @param notification La notification à accepter
     */
    public void acceptNotification(Notification notification) {
        LOG.info("Received notification in KafkaConsumer: type={}, userId={}", notification.getType(), notification.getUserId());

        // Convertir l'entité en message pour le broker
        NotificationMessage message = notificationConverter.toMessage(notification);
        acceptNotificationMessage(message);
    }
}
