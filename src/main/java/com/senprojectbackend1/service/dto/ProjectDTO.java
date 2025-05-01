package com.senprojectbackend1.service.dto;

import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.domain.enumeration.ProjectType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.senprojectbackend1.domain.Project} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 150)
    private String title;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 2000)
    private String description;

    @Size(max = 50000)
    private String showcase;

    @NotNull(message = "must not be null")
    private ProjectStatus status;

    @NotNull(message = "must not be null")
    private Instant createdAt;

    private Instant updatedAt;

    @Size(max = 255)
    private String githubUrl;

    @Size(max = 255)
    private String websiteUrl;

    @Size(max = 255)
    private String demoUrl;

    private Boolean openToCollaboration;

    private Boolean openToFunding;

    private ProjectType type;

    private Integer totalLikes;

    private Integer totalShares;

    private Integer totalViews;

    private Integer totalComments;

    private Integer totalFavorites;

    private Boolean isDeleted;

    @Size(max = 100)
    private String createdBy;

    @Size(max = 100)
    private String lastUpdatedBy;

    private TeamDTO team;

    private Set<UserProfileDTO> favoritedbies = new HashSet<>();

    private Set<TagDTO> tags = new HashSet<>();

    private Set<ProjectSectionDTO> sections = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShowcase() {
        return showcase;
    }

    public void setShowcase(String showcase) {
        this.showcase = showcase;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
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

    public String getGithubUrl() {
        return githubUrl;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getDemoUrl() {
        return demoUrl;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public Boolean getOpenToCollaboration() {
        return openToCollaboration;
    }

    public void setOpenToCollaboration(Boolean openToCollaboration) {
        this.openToCollaboration = openToCollaboration;
    }

    public Boolean getOpenToFunding() {
        return openToFunding;
    }

    public void setOpenToFunding(Boolean openToFunding) {
        this.openToFunding = openToFunding;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public Integer getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getTotalShares() {
        return totalShares;
    }

    public void setTotalShares(Integer totalShares) {
        this.totalShares = totalShares;
    }

    public Integer getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(Integer totalViews) {
        this.totalViews = totalViews;
    }

    public Integer getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(Integer totalComments) {
        this.totalComments = totalComments;
    }

    public Integer getTotalFavorites() {
        return totalFavorites;
    }

    public void setTotalFavorites(Integer totalFavorites) {
        this.totalFavorites = totalFavorites;
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

    public TeamDTO getTeam() {
        return team;
    }

    public void setTeam(TeamDTO team) {
        this.team = team;
    }

    public Set<UserProfileDTO> getFavoritedbies() {
        return favoritedbies;
    }

    public void setFavoritedbies(Set<UserProfileDTO> favoritedbies) {
        this.favoritedbies = favoritedbies;
    }

    public Set<TagDTO> getTags() {
        return tags;
    }

    public void setTags(Set<TagDTO> tags) {
        this.tags = tags;
    }

    public Set<ProjectSectionDTO> getSections() {
        return sections;
    }

    public void setSections(Set<ProjectSectionDTO> sections) {
        this.sections = sections;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ProjectDTO projectDTO)) {
            return false;
        }

        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", description='" + getDescription() + "'" +
            ", showcase='" + getShowcase() + "'" +
            ", status='" + getStatus() + "'" +
            ", createdAt='" + getCreatedAt() + "'" +
            ", updatedAt='" + getUpdatedAt() + "'" +
            ", githubUrl='" + getGithubUrl() + "'" +
            ", websiteUrl='" + getWebsiteUrl() + "'" +
            ", demoUrl='" + getDemoUrl() + "'" +
            ", openToCollaboration='" + getOpenToCollaboration() + "'" +
            ", openToFunding='" + getOpenToFunding() + "'" +
            ", type='" + getType() + "'" +
            ", totalLikes=" + getTotalLikes() +
            ", totalShares=" + getTotalShares() +
            ", totalViews=" + getTotalViews() +
            ", totalComments=" + getTotalComments() +
            ", totalFavorites=" + getTotalFavorites() +
            ", isDeleted='" + getIsDeleted() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", lastUpdatedBy='" + getLastUpdatedBy() + "'" +
            ", team=" + getTeam() +
            ", favoritedbies=" + getFavoritedbies() +
            ", tags=" + getTags() +
            ", sections=" + getSections() +
            "}";
    }
}
