package com.senprojectbackend1.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ProjectGalleryTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static ProjectGallery getProjectGallerySample1() {
        return new ProjectGallery().id(1L).imageUrl("imageUrl1").description("description1").order(1);
    }

    public static ProjectGallery getProjectGallerySample2() {
        return new ProjectGallery().id(2L).imageUrl("imageUrl2").description("description2").order(2);
    }

    public static ProjectGallery getProjectGalleryRandomSampleGenerator() {
        return new ProjectGallery()
            .id(longCount.incrementAndGet())
            .imageUrl(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .order(intCount.incrementAndGet());
    }
}
