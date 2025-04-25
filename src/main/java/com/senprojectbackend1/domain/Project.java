package com.senprojectbackend1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.domain.enumeration.ProjectType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Project.
 */
@Table("project")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Project implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 150)
    @Column("title")
    private String title;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 2000)
    @Column("description")
    private String description;

    @Size(max = 255)
    @Column("showcase")
    private String showcase;

    @NotNull(message = "must not be null")
    @Column("status")
    private ProjectStatus status;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Size(max = 255)
    @Column("github_url")
    private String githubUrl;

    @Size(max = 255)
    @Column("website_url")
    private String websiteUrl;

    @Size(max = 255)
    @Column("demo_url")
    private String demoUrl;

    @Column("open_to_collaboration")
    private Boolean openToCollaboration;

    @Column("open_to_funding")
    private Boolean openToFunding;

    @Column("type")
    private ProjectType type;

    @Column("total_likes")
    private Integer totalLikes;

    @Column("total_shares")
    private Integer totalShares;

    @Column("total_views")
    private Integer totalViews;

    @Column("total_comments")
    private Integer totalComments;

    @Column("total_favorites")
    private Integer totalFavorites;

    @Column("is_deleted")
    private Boolean isDeleted;

    @Size(max = 100)
    @Column("created_by")
    private String createdBy;

    @Size(max = 100)
    @Column("last_updated_by")
    private String lastUpdatedBy;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "members" }, allowSetters = true)
    private Team team;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "projects", "teams" }, allowSetters = true)
    private Set<UserProfile> favoritedbies = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "projects" }, allowSetters = true)
    private Set<Tag> tags = new HashSet<>();

    @Column("team_id")
    private Long teamId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Project id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public Project title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return this.description;
    }

    public Project description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShowcase() {
        return this.showcase;
    }

    public Project showcase(String showcase) {
        this.setShowcase(showcase);
        return this;
    }

    public void setShowcase(String showcase) {
        this.showcase = showcase;
    }

    public ProjectStatus getStatus() {
        return this.status;
    }

    public Project status(ProjectStatus status) {
        this.setStatus(status);
        return this;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Project createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Project updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getGithubUrl() {
        return this.githubUrl;
    }

    public Project githubUrl(String githubUrl) {
        this.setGithubUrl(githubUrl);
        return this;
    }

    public void setGithubUrl(String githubUrl) {
        this.githubUrl = githubUrl;
    }

    public String getWebsiteUrl() {
        return this.websiteUrl;
    }

    public Project websiteUrl(String websiteUrl) {
        this.setWebsiteUrl(websiteUrl);
        return this;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getDemoUrl() {
        return this.demoUrl;
    }

    public Project demoUrl(String demoUrl) {
        this.setDemoUrl(demoUrl);
        return this;
    }

    public void setDemoUrl(String demoUrl) {
        this.demoUrl = demoUrl;
    }

    public Boolean getOpenToCollaboration() {
        return this.openToCollaboration;
    }

    public Project openToCollaboration(Boolean openToCollaboration) {
        this.setOpenToCollaboration(openToCollaboration);
        return this;
    }

    public void setOpenToCollaboration(Boolean openToCollaboration) {
        this.openToCollaboration = openToCollaboration;
    }

    public Boolean getOpenToFunding() {
        return this.openToFunding;
    }

    public Project openToFunding(Boolean openToFunding) {
        this.setOpenToFunding(openToFunding);
        return this;
    }

    public void setOpenToFunding(Boolean openToFunding) {
        this.openToFunding = openToFunding;
    }

    public ProjectType getType() {
        return this.type;
    }

    public Project type(ProjectType type) {
        this.setType(type);
        return this;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public Integer getTotalLikes() {
        return this.totalLikes;
    }

    public Project totalLikes(Integer totalLikes) {
        this.setTotalLikes(totalLikes);
        return this;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Integer getTotalShares() {
        return this.totalShares;
    }

    public Project totalShares(Integer totalShares) {
        this.setTotalShares(totalShares);
        return this;
    }

    public void setTotalShares(Integer totalShares) {
        this.totalShares = totalShares;
    }

    public Integer getTotalViews() {
        return this.totalViews;
    }

    public Project totalViews(Integer totalViews) {
        this.setTotalViews(totalViews);
        return this;
    }

    public void setTotalViews(Integer totalViews) {
        this.totalViews = totalViews;
    }

    public Integer getTotalComments() {
        return this.totalComments;
    }

    public Project totalComments(Integer totalComments) {
        this.setTotalComments(totalComments);
        return this;
    }

    public void setTotalComments(Integer totalComments) {
        this.totalComments = totalComments;
    }

    public Integer getTotalFavorites() {
        return this.totalFavorites;
    }

    public Project totalFavorites(Integer totalFavorites) {
        this.setTotalFavorites(totalFavorites);
        return this;
    }

    public void setTotalFavorites(Integer totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Project isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Project createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public Project lastUpdatedBy(String lastUpdatedBy) {
        this.setLastUpdatedBy(lastUpdatedBy);
        return this;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Team getTeam() {
        return this.team;
    }

    public void setTeam(Team team) {
        this.team = team;
        this.teamId = team != null ? team.getId() : null;
    }

    public Project team(Team team) {
        this.setTeam(team);
        return this;
    }

    public Set<UserProfile> getFavoritedbies() {
        return this.favoritedbies;
    }

    public void setFavoritedbies(Set<UserProfile> userProfiles) {
        this.favoritedbies = userProfiles;
    }

    public Project favoritedbies(Set<UserProfile> userProfiles) {
        this.setFavoritedbies(userProfiles);
        return this;
    }

    public Project addFavoritedby(UserProfile userProfile) {
        this.favoritedbies.add(userProfile);
        return this;
    }

    public Project removeFavoritedby(UserProfile userProfile) {
        this.favoritedbies.remove(userProfile);
        return this;
    }

    public Set<Tag> getTags() {
        return this.tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Project tags(Set<Tag> tags) {
        this.setTags(tags);
        return this;
    }

    public Project addTags(Tag tag) {
        this.tags.add(tag);
        return this;
    }

    public Project removeTags(Tag tag) {
        this.tags.remove(tag);
        return this;
    }

    public Long getTeamId() {
        return this.teamId;
    }

    public void setTeamId(Long team) {
        this.teamId = team;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Project)) {
            return false;
        }
        return getId() != null && getId().equals(((Project) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Project{" +
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
            "}";
    }
}
