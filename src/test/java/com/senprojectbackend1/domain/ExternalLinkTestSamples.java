package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class ExternalLinkTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2L * Integer.MAX_VALUE));

    public static ExternalLink getExternalLinkSample1() {
        return new ExternalLink().id(1L).title("title1").url("url1");
    }

    public static ExternalLink getExternalLinkSample2() {
        return new ExternalLink().id(2L).title("title2").url("url2");
    }

    public static ExternalLink getExternalLinkRandomSampleGenerator() {
        return new ExternalLink().id(longCount.incrementAndGet()).title(UUID.randomUUID().toString()).url(UUID.randomUUID().toString());
    }
}
