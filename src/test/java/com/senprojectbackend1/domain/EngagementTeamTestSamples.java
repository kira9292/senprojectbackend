package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class EngagementTeamTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static EngagementTeam getEngagementTeamSample1() {
        return new EngagementTeam().id(1L).like(1);
    }

    public static EngagementTeam getEngagementTeamSample2() {
        return new EngagementTeam().id(2L).like(2);
    }

    public static EngagementTeam getEngagementTeamRandomSampleGenerator() {
        return new EngagementTeam().id(longCount.incrementAndGet()).like(intCount.incrementAndGet());
    }
}
