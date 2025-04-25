package com.senprojectbackend1.domain;

import java.util.UUID;

public class UserProfileTestSamples {

    public static UserProfile getUserProfileSample1() {
        return new UserProfile()
            .id("id1")
            .login("login1")
            .firstName("firstName1")
            .lastName("lastName1")
            .email("email1")
            .imageUrl("imageUrl1")
            .langKey("langKey1")
            .createdBy("createdBy1")
            .lastModifiedBy("lastModifiedBy1")
            .profileLink("profileLink1")
            .biography("biography1")
            .job("job1");
    }

    public static UserProfile getUserProfileSample2() {
        return new UserProfile()
            .id("id2")
            .login("login2")
            .firstName("firstName2")
            .lastName("lastName2")
            .email("email2")
            .imageUrl("imageUrl2")
            .langKey("langKey2")
            .createdBy("createdBy2")
            .lastModifiedBy("lastModifiedBy2")
            .profileLink("profileLink2")
            .biography("biography2")
            .job("job2");
    }

    public static UserProfile getUserProfileRandomSampleGenerator() {
        return new UserProfile()
            .id(UUID.randomUUID().toString())
            .login(UUID.randomUUID().toString())
            .firstName(UUID.randomUUID().toString())
            .lastName(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .imageUrl(UUID.randomUUID().toString())
            .langKey(UUID.randomUUID().toString())
            .createdBy(UUID.randomUUID().toString())
            .lastModifiedBy(UUID.randomUUID().toString())
            .profileLink(UUID.randomUUID().toString())
            .biography(UUID.randomUUID().toString())
            .job(UUID.randomUUID().toString());
    }
}
