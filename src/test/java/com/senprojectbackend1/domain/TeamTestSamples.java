package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class TeamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Team getTeamSample1() {
        return new Team()
            .id(1L)
            .name("name1")
            .description("description1")
            .logo("logo1")
            .totalLikes(1)
            .createdBy("createdBy1")
            .lastUpdatedBy("lastUpdatedBy1");
    }

    public static Team getTeamSample2() {
        return new Team()
            .id(2L)
            .name("name2")
            .description("description2")
            .logo("logo2")
            .totalLikes(2)
            .createdBy("createdBy2")
            .lastUpdatedBy("lastUpdatedBy2");
    }

    public static Team getTeamRandomSampleGenerator() {
        return new Team()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .logo(UUID.randomUUID().toString())
            .totalLikes(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastUpdatedBy(UUID.randomUUID().toString());
    }
}
