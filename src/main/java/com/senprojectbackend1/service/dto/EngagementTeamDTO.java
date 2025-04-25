package com.senprojectbackend1.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

/**
 * A DTO for the {@link com.senprojectbackend1.domain.EngagementTeam} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EngagementTeamDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    private Integer like;

    @NotNull(message = "must not be null")
    private Instant createdAt;

    private TeamDTO team;

    private UserProfileDTO user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getLike() {
        return like;
    }

    public void setLike(Integer like) {
        this.like = like;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public UserProfileDTO getUser() {
        return user;
    }

    public void setUser(UserProfileDTO user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EngagementTeamDTO)) {
            return false;
        }

        EngagementTeamDTO engagementTeamDTO = (EngagementTeamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, engagementTeamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EngagementTeamDTO{" +
            "id=" + getId() +
            ", like=" + getLike() +
            ", createdAt='" + getCreatedAt() + "'" +
            ", team=" + getTeam() +
            ", user=" + getUser() +
            "}";
    }
}
