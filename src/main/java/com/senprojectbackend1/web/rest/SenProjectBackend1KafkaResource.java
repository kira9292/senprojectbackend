package com.senprojectbackend1.web.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.senprojectbackend1.broker.KafkaConsumer;
import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.security.SecurityUtils;
import java.time.Duration;
import java.time.Instant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/notifications")
public class SenProjectBackend1KafkaResource {

    private final Logger LOG = LoggerFactory.getLogger(SenProjectBackend1KafkaResource.class);
    private final KafkaConsumer kafkaConsumer;
    private final UserProfileRepository userProfileRepository;

    public SenProjectBackend1KafkaResource(KafkaConsumer kafkaConsumer, UserProfileRepository userProfileRepository) {
        this.kafkaConsumer = kafkaConsumer;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * Stream notifications for the current authenticated user using Server-Sent Events
     */
    @GetMapping(value = "/me", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ServerSentEvent<String>> streamCurrentUserNotifications() {
        return SecurityUtils.getCurrentUserLogin()
            .flatMapMany(login ->
                userProfileRepository
                    .findOneByLogin(login)
                    .flatMapMany(user -> {
                        LOG.debug("REST request to stream notifications for user: {}", user.getId());

                        // Create a connection established notification
                        Notification connectionNotification = new Notification()
                            .content("Notification stream connected")
                            .createdAt(Instant.now())
                            .type(NotificationType.SYSTEM);
                        connectionNotification.setUserId(user.getId());

                        // Filter notifications for this user only
                        Flux<Notification> userNotifications =
                            this.kafkaConsumer.getNotificationFlux().filter(notification -> user.getId().equals(notification.getUserId()));

                        // Create a heartbeat notification every 30 seconds to keep the connection alive
                        Flux<Notification> heartbeat = Flux.interval(Duration.ofSeconds(30)).map(i -> {
                            Notification ping = new Notification()
                                .content("heartbeat")
                                .createdAt(Instant.now())
                                .type(NotificationType.SYSTEM);
                            ping.setUserId(user.getId());
                            return ping;
                        });

                        // Combine all notifications and convert to JSON string
                        return Flux.just(connectionNotification)
                            .concatWith(userNotifications)
                            .mergeWith(heartbeat)
                            .map(notification -> {
                                try {
                                    // Convert notification to JSON
                                    ObjectMapper mapper = new ObjectMapper();
                                    mapper.registerModule(new JavaTimeModule());
                                    return mapper.writeValueAsString(notification);
                                } catch (Exception e) {
                                    LOG.error("Error serializing notification", e);
                                    return "{}";
                                }
                            })
                            .map(jsonData -> ServerSentEvent.<String>builder().event("notification").data(jsonData).build());
                    })
            );
    }
}
