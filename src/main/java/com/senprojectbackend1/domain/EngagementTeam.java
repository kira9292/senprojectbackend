package com.senprojectbackend1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A EngagementTeam.
 */
@Table("engagement_team")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EngagementTeam implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Column("jhi_like")
    private Integer like;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "members" }, allowSetters = true)
    private Team team;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "projects", "teams" }, allowSetters = true)
    private UserProfile user;

    @Column("team_id")
    private Long teamId;

    @Column("user_id")
    private String userId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public EngagementTeam id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLike() {
        return this.like;
    }

    public EngagementTeam like(Integer like) {
        this.setLike(like);
        return this;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public EngagementTeam createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
        this.teamId = team != null ? team.getId() : null;
    }

    public EngagementTeam team(Team team) {
        this.setTeam(team);
        return this;
    }

    public UserProfile getUser() {
        return this.user;
    }

    public void setUser(UserProfile userProfile) {
        this.user = userProfile;
        this.userId = userProfile != null ? userProfile.getId() : null;
    }

    public EngagementTeam user(UserProfile userProfile) {
        this.setUser(userProfile);
        return this;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(Long team) {
        this.teamId = team;
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userProfile) {
        this.userId = userProfile;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EngagementTeam)) {
            return false;
        }
        return getId() != null && getId().equals(((EngagementTeam) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EngagementTeam{" +
            "id=" + getId() +
            ", like=" + getLike() +
            ", createdAt='" + getCreatedAt() + "'" +
            "}";
    }
}
