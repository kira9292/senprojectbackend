package com.senprojectbackend1.domain.criteria;

import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.domain.enumeration.ProjectType;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.senprojectbackend1.domain.Project} entity. This class is used
 * in {@link com.senprojectbackend1.web.rest.ProjectResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /projects?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectCriteria implements Serializable, Criteria {

    /**
     * Class for filtering ProjectStatus
     */
    public static class ProjectStatusFilter extends Filter<ProjectStatus> {

        public ProjectStatusFilter() {}

        public ProjectStatusFilter(ProjectStatusFilter filter) {
            super(filter);
        }

        @Override
        public ProjectStatusFilter copy() {
            return new ProjectStatusFilter(this);
        }
    }

    /**
     * Class for filtering ProjectType
     */
    public static class ProjectTypeFilter extends Filter<ProjectType> {

        public ProjectTypeFilter() {}

        public ProjectTypeFilter(ProjectTypeFilter filter) {
            super(filter);
        }

        @Override
        public ProjectTypeFilter copy() {
            return new ProjectTypeFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter title;

    private StringFilter description;

    private StringFilter showcase;

    private ProjectStatusFilter status;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private StringFilter githubUrl;

    private StringFilter websiteUrl;

    private StringFilter demoUrl;

    private BooleanFilter openToCollaboration;

    private BooleanFilter openToFunding;

    private ProjectTypeFilter type;

    private IntegerFilter totalLikes;

    private IntegerFilter totalShares;

    private IntegerFilter totalViews;

    private IntegerFilter totalComments;

    private IntegerFilter totalFavorites;

    private BooleanFilter isDeleted;

    private StringFilter createdBy;

    private StringFilter lastUpdatedBy;

    private LongFilter teamId;

    private Boolean distinct;

    public ProjectCriteria() {}

    public ProjectCriteria(ProjectCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.title = other.optionalTitle().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.showcase = other.optionalShowcase().map(StringFilter::copy).orElse(null);
        this.status = other.optionalStatus().map(ProjectStatusFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.githubUrl = other.optionalGithubUrl().map(StringFilter::copy).orElse(null);
        this.websiteUrl = other.optionalWebsiteUrl().map(StringFilter::copy).orElse(null);
        this.demoUrl = other.optionalDemoUrl().map(StringFilter::copy).orElse(null);
        this.openToCollaboration = other.optionalOpenToCollaboration().map(BooleanFilter::copy).orElse(null);
        this.openToFunding = other.optionalOpenToFunding().map(BooleanFilter::copy).orElse(null);
        this.type = other.optionalType().map(ProjectTypeFilter::copy).orElse(null);
        this.totalLikes = other.optionalTotalLikes().map(IntegerFilter::copy).orElse(null);
        this.totalShares = other.optionalTotalShares().map(IntegerFilter::copy).orElse(null);
        this.totalViews = other.optionalTotalViews().map(IntegerFilter::copy).orElse(null);
        this.totalComments = other.optionalTotalComments().map(IntegerFilter::copy).orElse(null);
        this.totalFavorites = other.optionalTotalFavorites().map(IntegerFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.lastUpdatedBy = other.optionalLastUpdatedBy().map(StringFilter::copy).orElse(null);
        this.teamId = other.optionalTeamId().map(LongFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public ProjectCriteria copy() {
        return new ProjectCriteria(this);
    }

    public LongFilter getId() {
        return id;
    }

    public Optional<LongFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public LongFilter id() {
        if (id == null) {
            setId(new LongFilter());
        }
        return id;
    }

    public void setId(LongFilter id) {
        this.id = id;
    }

    public StringFilter getTitle() {
        return title;
    }

    public Optional<StringFilter> optionalTitle() {
        return Optional.ofNullable(title);
    }

    public StringFilter title() {
        if (title == null) {
            setTitle(new StringFilter());
        }
        return title;
    }

    public void setTitle(StringFilter title) {
        this.title = title;
    }

    public StringFilter getDescription() {
        return description;
    }

    public Optional<StringFilter> optionalDescription() {
        return Optional.ofNullable(description);
    }

    public StringFilter description() {
        if (description == null) {
            setDescription(new StringFilter());
        }
        return description;
    }

    public void setDescription(StringFilter description) {
        this.description = description;
    }

    public StringFilter getShowcase() {
        return showcase;
    }

    public Optional<StringFilter> optionalShowcase() {
        return Optional.ofNullable(showcase);
    }

    public StringFilter showcase() {
        if (showcase == null) {
            setShowcase(new StringFilter());
        }
        return showcase;
    }

    public void setShowcase(StringFilter showcase) {
        this.showcase = showcase;
    }

    public ProjectStatusFilter getStatus() {
        return status;
    }

    public Optional<ProjectStatusFilter> optionalStatus() {
        return Optional.ofNullable(status);
    }

    public ProjectStatusFilter status() {
        if (status == null) {
            setStatus(new ProjectStatusFilter());
        }
        return status;
    }

    public void setStatus(ProjectStatusFilter status) {
        this.status = status;
    }

    public InstantFilter getCreatedAt() {
        return createdAt;
    }

    public Optional<InstantFilter> optionalCreatedAt() {
        return Optional.ofNullable(createdAt);
    }

    public InstantFilter createdAt() {
        if (createdAt == null) {
            setCreatedAt(new InstantFilter());
        }
        return createdAt;
    }

    public void setCreatedAt(InstantFilter createdAt) {
        this.createdAt = createdAt;
    }

    public InstantFilter getUpdatedAt() {
        return updatedAt;
    }

    public Optional<InstantFilter> optionalUpdatedAt() {
        return Optional.ofNullable(updatedAt);
    }

    public InstantFilter updatedAt() {
        if (updatedAt == null) {
            setUpdatedAt(new InstantFilter());
        }
        return updatedAt;
    }

    public void setUpdatedAt(InstantFilter updatedAt) {
        this.updatedAt = updatedAt;
    }

    public StringFilter getGithubUrl() {
        return githubUrl;
    }

    public Optional<StringFilter> optionalGithubUrl() {
        return Optional.ofNullable(githubUrl);
    }

    public StringFilter githubUrl() {
        if (githubUrl == null) {
            setGithubUrl(new StringFilter());
        }
        return githubUrl;
    }

    public void setGithubUrl(StringFilter githubUrl) {
        this.githubUrl = githubUrl;
    }

    public StringFilter getWebsiteUrl() {
        return websiteUrl;
    }

    public Optional<StringFilter> optionalWebsiteUrl() {
        return Optional.ofNullable(websiteUrl);
    }

    public StringFilter websiteUrl() {
        if (websiteUrl == null) {
            setWebsiteUrl(new StringFilter());
        }
        return websiteUrl;
    }

    public void setWebsiteUrl(StringFilter websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public StringFilter getDemoUrl() {
        return demoUrl;
    }

    public Optional<StringFilter> optionalDemoUrl() {
        return Optional.ofNullable(demoUrl);
    }

    public StringFilter demoUrl() {
        if (demoUrl == null) {
            setDemoUrl(new StringFilter());
        }
        return demoUrl;
    }

    public void setDemoUrl(StringFilter demoUrl) {
        this.demoUrl = demoUrl;
    }

    public BooleanFilter getOpenToCollaboration() {
        return openToCollaboration;
    }

    public Optional<BooleanFilter> optionalOpenToCollaboration() {
        return Optional.ofNullable(openToCollaboration);
    }

    public BooleanFilter openToCollaboration() {
        if (openToCollaboration == null) {
            setOpenToCollaboration(new BooleanFilter());
        }
        return openToCollaboration;
    }

    public void setOpenToCollaboration(BooleanFilter openToCollaboration) {
        this.openToCollaboration = openToCollaboration;
    }

    public BooleanFilter getOpenToFunding() {
        return openToFunding;
    }

    public Optional<BooleanFilter> optionalOpenToFunding() {
        return Optional.ofNullable(openToFunding);
    }

    public BooleanFilter openToFunding() {
        if (openToFunding == null) {
            setOpenToFunding(new BooleanFilter());
        }
        return openToFunding;
    }

    public void setOpenToFunding(BooleanFilter openToFunding) {
        this.openToFunding = openToFunding;
    }

    public ProjectTypeFilter getType() {
        return type;
    }

    public Optional<ProjectTypeFilter> optionalType() {
        return Optional.ofNullable(type);
    }

    public ProjectTypeFilter type() {
        if (type == null) {
            setType(new ProjectTypeFilter());
        }
        return type;
    }

    public void setType(ProjectTypeFilter type) {
        this.type = type;
    }

    public IntegerFilter getTotalLikes() {
        return totalLikes;
    }

    public Optional<IntegerFilter> optionalTotalLikes() {
        return Optional.ofNullable(totalLikes);
    }

    public IntegerFilter totalLikes() {
        if (totalLikes == null) {
            setTotalLikes(new IntegerFilter());
        }
        return totalLikes;
    }

    public void setTotalLikes(IntegerFilter totalLikes) {
        this.totalLikes = totalLikes;
    }

    public IntegerFilter getTotalShares() {
        return totalShares;
    }

    public Optional<IntegerFilter> optionalTotalShares() {
        return Optional.ofNullable(totalShares);
    }

    public IntegerFilter totalShares() {
        if (totalShares == null) {
            setTotalShares(new IntegerFilter());
        }
        return totalShares;
    }

    public void setTotalShares(IntegerFilter totalShares) {
        this.totalShares = totalShares;
    }

    public IntegerFilter getTotalViews() {
        return totalViews;
    }

    public Optional<IntegerFilter> optionalTotalViews() {
        return Optional.ofNullable(totalViews);
    }

    public IntegerFilter totalViews() {
        if (totalViews == null) {
            setTotalViews(new IntegerFilter());
        }
        return totalViews;
    }

    public void setTotalViews(IntegerFilter totalViews) {
        this.totalViews = totalViews;
    }

    public IntegerFilter getTotalComments() {
        return totalComments;
    }

    public Optional<IntegerFilter> optionalTotalComments() {
        return Optional.ofNullable(totalComments);
    }

    public IntegerFilter totalComments() {
        if (totalComments == null) {
            setTotalComments(new IntegerFilter());
        }
        return totalComments;
    }

    public void setTotalComments(IntegerFilter totalComments) {
        this.totalComments = totalComments;
    }

    public IntegerFilter getTotalFavorites() {
        return totalFavorites;
    }

    public Optional<IntegerFilter> optionalTotalFavorites() {
        return Optional.ofNullable(totalFavorites);
    }

    public IntegerFilter totalFavorites() {
        if (totalFavorites == null) {
            setTotalFavorites(new IntegerFilter());
        }
        return totalFavorites;
    }

    public void setTotalFavorites(IntegerFilter totalFavorites) {
        this.totalFavorites = totalFavorites;
    }

    public BooleanFilter getIsDeleted() {
        return isDeleted;
    }

    public Optional<BooleanFilter> optionalIsDeleted() {
        return Optional.ofNullable(isDeleted);
    }

    public BooleanFilter isDeleted() {
        if (isDeleted == null) {
            setIsDeleted(new BooleanFilter());
        }
        return isDeleted;
    }

    public void setIsDeleted(BooleanFilter isDeleted) {
        this.isDeleted = isDeleted;
    }

    public StringFilter getCreatedBy() {
        return createdBy;
    }

    public Optional<StringFilter> optionalCreatedBy() {
        return Optional.ofNullable(createdBy);
    }

    public StringFilter createdBy() {
        if (createdBy == null) {
            setCreatedBy(new StringFilter());
        }
        return createdBy;
    }

    public void setCreatedBy(StringFilter createdBy) {
        this.createdBy = createdBy;
    }

    public StringFilter getLastUpdatedBy() {
        return lastUpdatedBy;
    }

    public Optional<StringFilter> optionalLastUpdatedBy() {
        return Optional.ofNullable(lastUpdatedBy);
    }

    public StringFilter lastUpdatedBy() {
        if (lastUpdatedBy == null) {
            setLastUpdatedBy(new StringFilter());
        }
        return lastUpdatedBy;
    }

    public void setLastUpdatedBy(StringFilter lastUpdatedBy) {
        this.lastUpdatedBy = lastUpdatedBy;
    }

    public LongFilter getTeamId() {
        return teamId;
    }

    public Optional<LongFilter> optionalTeamId() {
        return Optional.ofNullable(teamId);
    }

    public LongFilter teamId() {
        if (teamId == null) {
            setTeamId(new LongFilter());
        }
        return teamId;
    }

    public void setTeamId(LongFilter teamId) {
        this.teamId = teamId;
    }

    public Boolean getDistinct() {
        return distinct;
    }

    public Optional<Boolean> optionalDistinct() {
        return Optional.ofNullable(distinct);
    }

    public Boolean distinct() {
        if (distinct == null) {
            setDistinct(true);
        }
        return distinct;
    }

    public void setDistinct(Boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ProjectCriteria that = (ProjectCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(title, that.title) &&
            Objects.equals(description, that.description) &&
            Objects.equals(showcase, that.showcase) &&
            Objects.equals(status, that.status) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(githubUrl, that.githubUrl) &&
            Objects.equals(websiteUrl, that.websiteUrl) &&
            Objects.equals(demoUrl, that.demoUrl) &&
            Objects.equals(openToCollaboration, that.openToCollaboration) &&
            Objects.equals(openToFunding, that.openToFunding) &&
            Objects.equals(type, that.type) &&
            Objects.equals(totalLikes, that.totalLikes) &&
            Objects.equals(totalShares, that.totalShares) &&
            Objects.equals(totalViews, that.totalViews) &&
            Objects.equals(totalComments, that.totalComments) &&
            Objects.equals(totalFavorites, that.totalFavorites) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
            Objects.equals(teamId, that.teamId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            title,
            description,
            showcase,
            status,
            createdAt,
            updatedAt,
            githubUrl,
            websiteUrl,
            demoUrl,
            openToCollaboration,
            openToFunding,
            type,
            totalLikes,
            totalShares,
            totalViews,
            totalComments,
            totalFavorites,
            isDeleted,
            createdBy,
            lastUpdatedBy,
            teamId,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalTitle().map(f -> "title=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalShowcase().map(f -> "showcase=" + f + ", ").orElse("") +
            optionalStatus().map(f -> "status=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalGithubUrl().map(f -> "githubUrl=" + f + ", ").orElse("") +
            optionalWebsiteUrl().map(f -> "websiteUrl=" + f + ", ").orElse("") +
            optionalDemoUrl().map(f -> "demoUrl=" + f + ", ").orElse("") +
            optionalOpenToCollaboration().map(f -> "openToCollaboration=" + f + ", ").orElse("") +
            optionalOpenToFunding().map(f -> "openToFunding=" + f + ", ").orElse("") +
            optionalType().map(f -> "type=" + f + ", ").orElse("") +
            optionalTotalLikes().map(f -> "totalLikes=" + f + ", ").orElse("") +
            optionalTotalShares().map(f -> "totalShares=" + f + ", ").orElse("") +
            optionalTotalViews().map(f -> "totalViews=" + f + ", ").orElse("") +
            optionalTotalComments().map(f -> "totalComments=" + f + ", ").orElse("") +
            optionalTotalFavorites().map(f -> "totalFavorites=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalLastUpdatedBy().map(f -> "lastUpdatedBy=" + f + ", ").orElse("") +
            optionalTeamId().map(f -> "teamId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
