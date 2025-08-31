package com.senprojectbackend1.broker;

import com.senprojectbackend1.broker.dto.NotificationMessage;
import com.senprojectbackend1.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Implémentation simplifiée du service de broker de notifications.
 * Utilise des Sinks Reactor pour la communication interne au lieu de Kafka.
 */
@Service
public class NotificationBrokerServiceImpl implements NotificationBrokerService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationBrokerServiceImpl.class);
    private final Sinks.Many<Notification> notificationSink;
    private final Sinks.Many<NotificationMessage> messageSink;

    public NotificationBrokerServiceImpl() {
        this.notificationSink = Sinks.many().multicast().onBackpressureBuffer();
        this.messageSink = Sinks.many().multicast().onBackpressureBuffer();
        LOG.info("NotificationBrokerService initialized with internal sinks");
    }

    @Override
    public boolean sendNotification(Notification notification) {
        LOG.debug("Sending notification via internal sink: {}", notification);
        try {
            Sinks.EmitResult result = notificationSink.tryEmitNext(notification);
            if (result.isSuccess()) {
                LOG.info("Successfully sent notification via internal sink: type={}, userId={}", 
                    notification.getType(), notification.getUserId());
                return true;
            } else {
                LOG.error("Failed to send notification via internal sink: {}", result);
                return false;
            }
        } catch (Exception e) {
            LOG.error("Error sending notification via internal sink", e);
            return false;
        }
    }

    @Override
    public boolean sendNotificationMessage(NotificationMessage message) {
        LOG.debug("Sending notification message via internal sink: {}", message);
        try {
            Sinks.EmitResult result = messageSink.tryEmitNext(message);
            if (result.isSuccess()) {
                LOG.info("Successfully sent notification message via internal sink: type={}, userId={}", 
                    message.getType(), message.getUserId());
                return true;
            } else {
                LOG.error("Failed to send notification message via internal sink: {}", result);
                return false;
            }
        } catch (Exception e) {
            LOG.error("Error sending notification message via internal sink", e);
            return false;
        }
    }

    @Override
    public Flux<Notification> getNotificationFlux() {
        LOG.debug("Getting notification flux from internal sink");
        return notificationSink.asFlux();
    }

    @Override
    public Flux<NotificationMessage> getNotificationMessageFlux() {
        LOG.debug("Getting notification message flux from internal sink");
        return messageSink.asFlux();
    }
}
