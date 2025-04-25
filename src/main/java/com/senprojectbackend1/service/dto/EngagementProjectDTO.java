package com.senprojectbackend1.service.dto;

import com.senprojectbackend1.domain.enumeration.EngagementType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.senprojectbackend1.domain.EngagementProject} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EngagementProjectDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private EngagementType type;

    @NotNull(message = "must not be null")
    private Instant createdAt;

    private UserProfileDTO user;

    private ProjectDTO project;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EngagementType getType() {
        return type;
    }

    public void setType(EngagementType type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserProfileDTO getUser() {
        return user;
    }

    public void setUser(UserProfileDTO user) {
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
        if (!(o instanceof EngagementProjectDTO engagementProjectDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, engagementProjectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EngagementProjectDTO{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", user=" + getUser() +
            ", project=" + getProject() +
            "}";
    }
}
