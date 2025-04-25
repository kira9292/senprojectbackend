package com.senprojectbackend1.domain.criteria;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.senprojectbackend1.domain.EngagementTeam} entity. This class is used
 * in {@link com.senprojectbackend1.web.rest.EngagementTeamResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /engagement-teams?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class EngagementTeamCriteria implements Serializable, Criteria {

    private static final long serialVersionUID = 1L;

    private LongFilter id;

    private IntegerFilter like;

    private InstantFilter createdAt;

    private LongFilter teamId;

    private StringFilter userId;

    private Boolean distinct;

    public EngagementTeamCriteria() {}

    public EngagementTeamCriteria(EngagementTeamCriteria other) {
        this.id = other.optionalId().map(LongFilter::copy).orElse(null);
        this.like = other.optionalLike().map(IntegerFilter::copy).orElse(null);
        this.createdAt = other.optionalCreatedAt().map(InstantFilter::copy).orElse(null);
        this.teamId = other.optionalTeamId().map(LongFilter::copy).orElse(null);
        this.userId = other.optionalUserId().map(StringFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public EngagementTeamCriteria copy() {
        return new EngagementTeamCriteria(this);
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

    public IntegerFilter getLike() {
        return like;
    }

    public Optional<IntegerFilter> optionalLike() {
        return Optional.ofNullable(like);
    }

    public IntegerFilter like() {
        if (like == null) {
            setLike(new IntegerFilter());
        }
        return like;
    }

    public void setLike(IntegerFilter like) {
        this.like = like;
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

    public StringFilter getUserId() {
        return userId;
    }

    public Optional<StringFilter> optionalUserId() {
        return Optional.ofNullable(userId);
    }

    public StringFilter userId() {
        if (userId == null) {
            setUserId(new StringFilter());
        }
        return userId;
    }

    public void setUserId(StringFilter userId) {
        this.userId = userId;
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
        final EngagementTeamCriteria that = (EngagementTeamCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(like, that.like) &&
            Objects.equals(createdAt, that.createdAt) &&
            Objects.equals(teamId, that.teamId) &&
            Objects.equals(userId, that.userId) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, like, createdAt, teamId, userId, distinct);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "EngagementTeamCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLike().map(f -> "like=" + f + ", ").orElse("") +
            optionalCreatedAt().map(f -> "createdAt=" + f + ", ").orElse("") +
            optionalTeamId().map(f -> "teamId=" + f + ", ").orElse("") +
            optionalUserId().map(f -> "userId=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
