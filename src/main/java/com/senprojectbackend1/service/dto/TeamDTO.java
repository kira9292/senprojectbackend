package com.senprojectbackend1.service.dto;

import com.senprojectbackend1.domain.enumeration.TeamVisibility;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.senprojectbackend1.domain.Team} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 100)
    private String name;

    @Size(max = 1000)
    private String description;

    @Size(max = 255)
    private String logo;

    private Instant createdAt;

    private Instant updatedAt;

    private TeamVisibility visibility;

    private Integer totalLikes;

    private Boolean isDeleted;

    @Size(max = 100)
    private String createdBy;

    @Size(max = 100)
    private String lastUpdatedBy;

    private Set<UserProfileSimpleDTO> members = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
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

    public TeamVisibility getVisibility() {
        return visibility;
    }

    public void setVisibility(TeamVisibility visibility) {
        this.visibility = visibility;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Set<UserProfileSimpleDTO> getMembers() {
        return members;
    }

    public void setMembers(Set<UserProfileSimpleDTO> members) {
        this.members = members;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamDTO)) {
            return false;
        }

        TeamDTO teamDTO = (TeamDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, teamDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamDTO{" +
            "id=" + getId() +
            ", name='" + getName() + "'" +
            ", description='" + getDescription() + "'" +
            ", logo='" + getLogo() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", visibility='" + getVisibility() + "'" +
            ", totalLikes=" + getTotalLikes() +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastUpdatedBy='" + getLastUpdatedBy() + "'" +
            ", members=" + getMembers() +
            "}";
    }
}
