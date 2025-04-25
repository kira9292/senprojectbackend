package com.senprojectbackend1.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senprojectbackend1.broker.KafkaConsumer;
import com.senprojectbackend1.domain.Notification;
import java.util.function.Consumer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConsumerConfig {

    private static final Logger LOG = LoggerFactory.getLogger(KafkaConsumerConfig.class);
    private final KafkaConsumer kafkaConsumer;
    private final ObjectMapper objectMapper;

    public KafkaConsumerConfig(KafkaConsumer kafkaConsumer, ObjectMapper objectMapper) {
        this.kafkaConsumer = kafkaConsumer;
        this.objectMapper = objectMapper
            .copy()
            .registerModule(new JavaTimeModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Bean
    public Consumer<String> notificationConsumer() {
        return message -> {
            LOG.debug("Received raw message from Kafka: {}", message);
            try {
                Notification notification = objectMapper.readValue(message, Notification.class);
                LOG.info("Successfully deserialized notification: type={}, userId={}", notification.getType(), notification.getUserId());
                kafkaConsumer.acceptNotification(notification);
            } catch (Exception e) {
                LOG.error("Failed to deserialize notification message: {}", e.getMessage(), e);
            }
        };
    }
}
