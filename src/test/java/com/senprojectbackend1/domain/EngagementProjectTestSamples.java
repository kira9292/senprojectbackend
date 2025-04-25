package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class EngagementProjectTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static EngagementProject getEngagementProjectSample1() {
        return new EngagementProject().id(1L);
    }

    public static EngagementProject getEngagementProjectSample2() {
        return new EngagementProject().id(2L);
    }

    public static EngagementProject getEngagementProjectRandomSampleGenerator() {
        return new EngagementProject().id(longCount.incrementAndGet());
    }
}
