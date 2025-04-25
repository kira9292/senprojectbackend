package com.senprojectbackend1.domain;

import static com.senprojectbackend1.domain.CommentTestSamples.*;
import static com.senprojectbackend1.domain.ProjectTestSamples.*;
import static com.senprojectbackend1.domain.UserProfileTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import com.senprojectbackend1.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class CommentTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Comment.class);
        Comment comment1 = getCommentSample1();
        Comment comment2 = new Comment();
        assertThat(comment1).isNotEqualTo(comment2);

        comment2.setId(comment1.getId());
        assertThat(comment1).isEqualTo(comment2);

        comment2 = getCommentSample2();
        assertThat(comment1).isNotEqualTo(comment2);
    }

    @Test
    void userTest() {
        Comment comment = getCommentRandomSampleGenerator();
        UserProfile userProfileBack = getUserProfileRandomSampleGenerator();

        comment.setUser(userProfileBack);
        assertThat(comment.getUser()).isEqualTo(userProfileBack);

        comment.user(null);
        assertThat(comment.getUser()).isNull();
    }

    @Test
    void projectTest() {
        Comment comment = getCommentRandomSampleGenerator();
        Project projectBack = getProjectRandomSampleGenerator();

        comment.setProject(projectBack);
        assertThat(comment.getProject()).isEqualTo(projectBack);

        comment.project(null);
        assertThat(comment.getProject()).isNull();
    }
}
