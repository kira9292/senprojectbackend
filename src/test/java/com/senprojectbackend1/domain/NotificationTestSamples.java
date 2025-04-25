package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class NotificationTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Notification getNotificationSample1() {
        return new Notification().id(1L).content("content1").entityId("entityId1");
    }

    public static Notification getNotificationSample2() {
        return new Notification().id(2L).content("content2").entityId("entityId2");
    }

    public static Notification getNotificationRandomSampleGenerator() {
        return new Notification()
            .id(longCount.incrementAndGet())
            .content(UUID.randomUUID().toString())
            .entityId(UUID.randomUUID().toString());
    }
}
