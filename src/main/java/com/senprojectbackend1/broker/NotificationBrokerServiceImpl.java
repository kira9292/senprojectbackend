package com.senprojectbackend1.broker;

import com.senprojectbackend1.broker.dto.NotificationMessage;
import com.senprojectbackend1.domain.Notification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

/**
 * Implémentation du service de broker de notifications.
 */
@Service
public class NotificationBrokerServiceImpl implements NotificationBrokerService {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationBrokerServiceImpl.class);
    private final KafkaProducer kafkaProducer;
    private final KafkaConsumer kafkaConsumer;

    public NotificationBrokerServiceImpl(KafkaProducer kafkaProducer, KafkaConsumer kafkaConsumer) {
        this.kafkaProducer = kafkaProducer;
        this.kafkaConsumer = kafkaConsumer;
        LOG.info("NotificationBrokerService initialized");
    }

    @Override
    public boolean sendNotification(Notification notification) {
        LOG.debug("Sending notification via broker service: {}", notification);
        return kafkaProducer.sendNotification(notification);
    }

    @Override
    public boolean sendNotificationMessage(NotificationMessage message) {
        LOG.debug("Sending notification message via broker service: {}", message);
        return kafkaProducer.sendNotificationMessage(message);
    }

    @Override
    public Flux<Notification> getNotificationFlux() {
        LOG.debug("Getting notification flux from broker service");
        return kafkaConsumer.getNotificationFlux();
    }

    @Override
    public Flux<NotificationMessage> getNotificationMessageFlux() {
        LOG.debug("Getting notification message flux from broker service");
        return kafkaConsumer.getNotificationMessageFlux();
    }
}
