package com.senprojectbackend1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EngagementProject.
 */
@Table("engagement_project")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EngagementProject implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("type")
    private EngagementType type;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "projects", "teams" }, allowSetters = true)
    private UserProfile user;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "team", "favoritedbies", "tags" }, allowSetters = true)
    private Project project;

    @Column("user_id")
    private String userId;

    @Column("project_id")
    private Long projectId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EngagementProject id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public EngagementType getType() {
        return this.type;
    }

    public EngagementProject type(EngagementType type) {
        this.setType(type);
        return this;
    }

    public void setType(EngagementType type) {
        this.type = type;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public EngagementProject createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public UserProfile getUser() {
        return this.user;
    }

    public void setUser(UserProfile userProfile) {
        this.user = userProfile;
        this.userId = userProfile != null ? userProfile.getId() : null;
    }

    public EngagementProject user(UserProfile userProfile) {
        this.setUser(userProfile);
        return this;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public EngagementProject project(Project project) {
        this.setProject(project);
        return this;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userProfile) {
        this.userId = userProfile;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EngagementProject)) {
            return false;
        }
        return getId() != null && getId().equals(((EngagementProject) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EngagementProject{" +
            "id=" + getId() +
            ", type='" + getType() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
