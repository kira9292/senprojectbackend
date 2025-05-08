package com.senprojectbackend1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senprojectbackend1.domain.enumeration.TeamVisibility;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A Team.
 */
@Table("team")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 100)
    @Column("name")
    private String name;

    @Size(max = 1000)
    @Column("description")
    private String description;

    @Column("logo")
    private String logo;

    @NotNull(message = "must not be null")
    @Column("created_at")
    private Instant createdAt;

    @Column("updated_at")
    private Instant updatedAt;

    @Column("visibility")
    private TeamVisibility visibility;

    @Column("total_likes")
    private Integer totalLikes;

    @Column("is_deleted")
    private Boolean isDeleted;

    @Size(max = 100)
    @Column("created_by")
    private String createdBy;

    @Size(max = 100)
    @Column("last_updated_by")
    private String lastUpdatedBy;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "projects", "teams" }, allowSetters = true)
    private Set<UserProfile> members = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public Team id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public Team name(String name) {
        this.setName(name);
        return this;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return this.description;
    }

    public Team description(String description) {
        this.setDescription(description);
        return this;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLogo() {
        return this.logo;
    }

    public Team logo(String logo) {
        this.setLogo(logo);
        return this;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Instant getCreatedAt() {
        return this.createdAt;
    }

    public Team createdAt(Instant createdAt) {
        this.setCreatedAt(createdAt);
        return this;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return this.updatedAt;
    }

    public Team updatedAt(Instant updatedAt) {
        this.setUpdatedAt(updatedAt);
        return this;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    public TeamVisibility getVisibility() {
        return this.visibility;
    }

    public Team visibility(TeamVisibility visibility) {
        this.setVisibility(visibility);
        return this;
    }

    public void setVisibility(TeamVisibility visibility) {
        this.visibility = visibility;
    }

    public Integer getTotalLikes() {
        return this.totalLikes;
    }

    public Team totalLikes(Integer totalLikes) {
        this.setTotalLikes(totalLikes);
        return this;
    }

    public void setTotalLikes(Integer totalLikes) {
        this.totalLikes = totalLikes;
    }

    public Boolean getIsDeleted() {
        return this.isDeleted;
    }

    public Team isDeleted(Boolean isDeleted) {
        this.setIsDeleted(isDeleted);
        return this;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public Team createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getLastUpdatedBy() {
        return this.lastUpdatedBy;
    }

    public Team lastUpdatedBy(String lastUpdatedBy) {
        this.setLastUpdatedBy(lastUpdatedBy);
        return this;
    }

    public void setLastUpdatedBy(String lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public Set<UserProfile> getMembers() {
        return this.members;
    }

    public void setMembers(Set<UserProfile> userProfiles) {
        this.members = userProfiles;
    }

    public Team members(Set<UserProfile> userProfiles) {
        this.setMembers(userProfiles);
        return this;
    }

    public void addMembers(UserProfile userProfile) {
        this.members.add(userProfile);
    }

    public void removeMembers(UserProfile userProfile) {
        this.members.remove(userProfile);
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Team)) {
            return false;
        }
        return getId() != null && getId().equals(((Team) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "Team{" +
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
            "}";
    }
}
