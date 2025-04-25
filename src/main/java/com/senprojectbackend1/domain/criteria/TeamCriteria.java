package com.senprojectbackend1.domain.criteria;

import com.senprojectbackend1.domain.enumeration.TeamVisibility;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.senprojectbackend1.domain.Team} entity. This class is used
 * in {@link com.senprojectbackend1.web.rest.TeamResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /teams?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class TeamCriteria implements Serializable, Criteria {

    /**
     * Class for filtering TeamVisibility
     */
    public static class TeamVisibilityFilter extends Filter<TeamVisibility> {

        public TeamVisibilityFilter() {}

        public TeamVisibilityFilter(TeamVisibilityFilter filter) {
            super(filter);
        }

        @Override
        public TeamVisibilityFilter copy() {
            return new TeamVisibilityFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private StringFilter name;

    private StringFilter description;

    private StringFilter logo;

    private InstantFilter createdAt;

    private InstantFilter updatedAt;

    private TeamVisibilityFilter visibility;

    private IntegerFilter totalLikes;

    private BooleanFilter isDeleted;

    private StringFilter createdBy;

    private StringFilter lastUpdatedBy;

    private Boolean distinct;

    public TeamCriteria() {}

    public TeamCriteria(TeamCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.name = other.optionalName().map(StringFilter::copy).orElse(null);
        this.description = other.optionalDescription().map(StringFilter::copy).orElse(null);
        this.logo = other.optionalLogo().map(StringFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.updatedAt = other.optionalUpdatedAt().map(InstantFilter::copy).orElse(null);
        this.visibility = other.optionalVisibility().map(TeamVisibilityFilter::copy).orElse(null);
        this.totalLikes = other.optionalTotalLikes().map(IntegerFilter::copy).orElse(null);
        this.isDeleted = other.optionalIsDeleted().map(BooleanFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.lastUpdatedBy = other.optionalLastUpdatedBy().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public TeamCriteria copy() {
        return new TeamCriteria(this);
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

    public StringFilter getName() {
        return name;
    }

    public Optional<StringFilter> optionalName() {
        return Optional.ofNullable(name);
    }

    public StringFilter name() {
        if (name == null) {
            setName(new StringFilter());
        }
        return name;
    }

    public void setName(StringFilter name) {
        this.name = name;
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

    public StringFilter getLogo() {
        return logo;
    }

    public Optional<StringFilter> optionalLogo() {
        return Optional.ofNullable(logo);
    }

    public StringFilter logo() {
        if (logo == null) {
            setLogo(new StringFilter());
        }
        return logo;
    }

    public void setLogo(StringFilter logo) {
        this.logo = logo;
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

    public TeamVisibilityFilter getVisibility() {
        return visibility;
    }

    public Optional<TeamVisibilityFilter> optionalVisibility() {
        return Optional.ofNullable(visibility);
    }

    public TeamVisibilityFilter visibility() {
        if (visibility == null) {
            setVisibility(new TeamVisibilityFilter());
        }
        return visibility;
    }

    public void setVisibility(TeamVisibilityFilter visibility) {
        this.visibility = visibility;
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
        final TeamCriteria that = (TeamCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(logo, that.logo) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(updatedAt, that.updatedAt) &&
            Objects.equals(visibility, that.visibility) &&
            Objects.equals(totalLikes, that.totalLikes) &&
            Objects.equals(isDeleted, that.isDeleted) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(lastUpdatedBy, that.lastUpdatedBy) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            name,
            description,
            logo,
            createdAt,
            updatedAt,
            visibility,
            totalLikes,
            isDeleted,
            createdBy,
            lastUpdatedBy,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "TeamCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalName().map(f -> "name=" + f + ", ").orElse("") +
            optionalDescription().map(f -> "description=" + f + ", ").orElse("") +
            optionalLogo().map(f -> "logo=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalUpdatedAt().map(f -> "updatedAt=" + f + ", ").orElse("") +
            optionalVisibility().map(f -> "visibility=" + f + ", ").orElse("") +
            optionalTotalLikes().map(f -> "totalLikes=" + f + ", ").orElse("") +
            optionalIsDeleted().map(f -> "isDeleted=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalLastUpdatedBy().map(f -> "lastUpdatedBy=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
