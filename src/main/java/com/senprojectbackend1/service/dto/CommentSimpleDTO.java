package com.senprojectbackend1.service.dto;

import com.senprojectbackend1.domain.enumeration.CommentStatus;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A Simple DTO for the Comment entity with UserProfileSimpleDTO.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class CommentSimpleDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 1000)
    private String content;

    @NotNull(message = "must not be null")
    private Instant createdAt;

    private Instant updatedAt;

    @NotNull(message = "must not be null")
    private CommentStatus status;

    private UserProfileSimpleDTO user;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public CommentStatus getStatus() {
        return status;
    }

    public void setStatus(CommentStatus status) {
        this.status = status;
    }

    public UserProfileSimpleDTO getUser() {
        return user;
    }

    public void setUser(UserProfileSimpleDTO user) {
        this.user = user;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof CommentSimpleDTO)) {
            return false;
        }

        CommentSimpleDTO commentDTO = (CommentSimpleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, commentDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return (
            "CommentSimpleDTO{" +
            "id=" +
            getId() +
            ", content='" +
            getContent() +
            "'" +
            ", createdAt='" +
            getCreatedAt() +
            "'" +
            ", updatedAt='" +
            getUpdatedAt() +
            "'" +
            ", status='" +
            getStatus() +
            "'" +
            ", user=" +
            getUser() +
            ", project=" +
            getProject() +
            "}"
        );
    }
}
