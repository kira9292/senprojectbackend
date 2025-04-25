package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Project getProjectSample1() {
        return new Project()
            .id(1L)
            .title("title1")
            .description("description1")
            .showcase("showcase1")
            .githubUrl("githubUrl1")
            .websiteUrl("websiteUrl1")
            .demoUrl("demoUrl1")
            .totalLikes(1)
            .totalShares(1)
            .totalViews(1)
            .totalComments(1)
            .totalFavorites(1)
            .createdBy("createdBy1")
            .lastUpdatedBy("lastUpdatedBy1");
    }

    public static Project getProjectSample2() {
        return new Project()
            .id(2L)
            .title("title2")
            .description("description2")
            .showcase("showcase2")
            .githubUrl("githubUrl2")
            .websiteUrl("websiteUrl2")
            .demoUrl("demoUrl2")
            .totalLikes(2)
            .totalShares(2)
            .totalViews(2)
            .totalComments(2)
            .totalFavorites(2)
            .createdBy("createdBy2")
            .lastUpdatedBy("lastUpdatedBy2");
    }

    public static Project getProjectRandomSampleGenerator() {
        return new Project()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .showcase(UUID.randomUUID().toString())
            .githubUrl(UUID.randomUUID().toString())
            .websiteUrl(UUID.randomUUID().toString())
            .demoUrl(UUID.randomUUID().toString())
            .totalLikes(intCount.incrementAndGet())
            .totalShares(intCount.incrementAndGet())
            .totalViews(intCount.incrementAndGet())
            .totalComments(intCount.incrementAndGet())
            .totalFavorites(intCount.incrementAndGet())
            .createdBy(UUID.randomUUID().toString())
            .lastUpdatedBy(UUID.randomUUID().toString());
    }
}
