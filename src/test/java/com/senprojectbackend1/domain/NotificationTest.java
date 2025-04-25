package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.NotificationTestSamples.*;
import static com.senprojectbackend1.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class NotificationTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Notification.class);
        Notification notification1 = getNotificationSample1();
        Notification notification2 = new Notification();
        assertThat(notification1).isNotEqualTo(notification2);

        notification2.setId(notification1.getId());
        assertThat(notification1).isEqualTo(notification2);

        notification2 = getNotificationSample2();
        assertThat(notification1).isNotEqualTo(notification2);
    }

    @Test
    void userTest() {
        Notification notification = getNotificationRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        notification.setUser(userProfileBack);
        assertThat(notification.getUser()).isEqualTo(userProfileBack);

        notification.user(null);
        assertThat(notification.getUser()).isNull();
    }
}
