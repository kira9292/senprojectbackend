package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectSectionTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ProjectSection getProjectSectionSample1() {
        return new ProjectSection().id(1L).title("title1").content("content1").mediaUrl("mediaUrl1").order(1);
    }

    public static ProjectSection getProjectSectionSample2() {
        return new ProjectSection().id(2L).title("title2").content("content2").mediaUrl("mediaUrl2").order(2);
    }

    public static ProjectSection getProjectSectionRandomSampleGenerator() {
        return new ProjectSection()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .content(UUID.randomUUID().toString())
            .mediaUrl(UUID.randomUUID().toString())
            .order(intCount.incrementAndGet());
    }
}
