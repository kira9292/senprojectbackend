package com.senprojectbackend1.domain.criteria;

import com.senprojectbackend1.domain.enumeration.Genre;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import org.springdoc.core.annotations.ParameterObject;
import tech.jhipster.service.Criteria;
import tech.jhipster.service.filter.*;

/**
 * Criteria class for the {@link com.senprojectbackend1.domain.UserProfile} entity. This class is used
 * in {@link com.senprojectbackend1.web.rest.UserProfileResource} to receive all the possible filtering options from
 * the Http GET request parameters.
 * For example the following could be a valid request:
 * {@code /user-profiles?id.greaterThan=5&attr1.contains=something&attr2.specified=false}
 * As Spring is unable to properly convert the types, unless specific {@link Filter} class are used, we need to use
 * fix type specific filters.
 */
@ParameterObject
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfileCriteria implements Serializable, Criteria {

    /**
     * Class for filtering Genre
     */
    public static class GenreFilter extends Filter<Genre> {

        public GenreFilter() {}

        public GenreFilter(GenreFilter filter) {
            super(filter);
        }

        @Override
        public GenreFilter copy() {
            return new GenreFilter(this);
        }
    }

    private static final long serialVersionUID = 1L;

    private StringFilter id;

    private StringFilter login;

    private StringFilter firstName;

    private StringFilter lastName;

    private StringFilter email;

    private StringFilter imageUrl;

    private BooleanFilter activated;

    private StringFilter langKey;

    private StringFilter createdBy;

    private InstantFilter createdDate;

    private StringFilter lastModifiedBy;

    private InstantFilter lastModifiedDate;

    private StringFilter profileLink;

    private StringFilter biography;

    private InstantFilter birthDate;

    private StringFilter job;

    private GenreFilter sexe;

    private Boolean distinct;

    public UserProfileCriteria() {}

    public UserProfileCriteria(UserProfileCriteria other) {
        this.id = other.optionalId().map(StringFilter::copy).orElse(null);
        this.login = other.optionalLogin().map(StringFilter::copy).orElse(null);
        this.firstName = other.optionalFirstName().map(StringFilter::copy).orElse(null);
        this.lastName = other.optionalLastName().map(StringFilter::copy).orElse(null);
        this.email = other.optionalEmail().map(StringFilter::copy).orElse(null);
        this.imageUrl = other.optionalImageUrl().map(StringFilter::copy).orElse(null);
        this.activated = other.optionalActivated().map(BooleanFilter::copy).orElse(null);
        this.langKey = other.optionalLangKey().map(StringFilter::copy).orElse(null);
        this.createdBy = other.optionalCreatedBy().map(StringFilter::copy).orElse(null);
        this.createdDate = other.optionalCreatedDate().map(InstantFilter::copy).orElse(null);
        this.lastModifiedBy = other.optionalLastModifiedBy().map(StringFilter::copy).orElse(null);
        this.lastModifiedDate = other.optionalLastModifiedDate().map(InstantFilter::copy).orElse(null);
        this.profileLink = other.optionalProfileLink().map(StringFilter::copy).orElse(null);
        this.biography = other.optionalBiography().map(StringFilter::copy).orElse(null);
        this.birthDate = other.optionalBirthDate().map(InstantFilter::copy).orElse(null);
        this.job = other.optionalJob().map(StringFilter::copy).orElse(null);
        this.sexe = other.optionalSexe().map(GenreFilter::copy).orElse(null);
        this.distinct = other.distinct;
    }

    @Override
    public UserProfileCriteria copy() {
        return new UserProfileCriteria(this);
    }

    public StringFilter getId() {
        return id;
    }

    public Optional<StringFilter> optionalId() {
        return Optional.ofNullable(id);
    }

    public StringFilter id() {
        if (id == null) {
            setId(new StringFilter());
        }
        return id;
    }

    public void setId(StringFilter id) {
        this.id = id;
    }

    public StringFilter getLogin() {
        return login;
    }

    public Optional<StringFilter> optionalLogin() {
        return Optional.ofNullable(login);
    }

    public StringFilter login() {
        if (login == null) {
            setLogin(new StringFilter());
        }
        return login;
    }

    public void setLogin(StringFilter login) {
        this.login = login;
    }

    public StringFilter getFirstName() {
        return firstName;
    }

    public Optional<StringFilter> optionalFirstName() {
        return Optional.ofNullable(firstName);
    }

    public StringFilter firstName() {
        if (firstName == null) {
            setFirstName(new StringFilter());
        }
        return firstName;
    }

    public void setFirstName(StringFilter firstName) {
        this.firstName = firstName;
    }

    public StringFilter getLastName() {
        return lastName;
    }

    public Optional<StringFilter> optionalLastName() {
        return Optional.ofNullable(lastName);
    }

    public StringFilter lastName() {
        if (lastName == null) {
            setLastName(new StringFilter());
        }
        return lastName;
    }

    public void setLastName(StringFilter lastName) {
        this.lastName = lastName;
    }

    public StringFilter getEmail() {
        return email;
    }

    public Optional<StringFilter> optionalEmail() {
        return Optional.ofNullable(email);
    }

    public StringFilter email() {
        if (email == null) {
            setEmail(new StringFilter());
        }
        return email;
    }

    public void setEmail(StringFilter email) {
        this.email = email;
    }

    public StringFilter getImageUrl() {
        return imageUrl;
    }

    public Optional<StringFilter> optionalImageUrl() {
        return Optional.ofNullable(imageUrl);
    }

    public StringFilter imageUrl() {
        if (imageUrl == null) {
            setImageUrl(new StringFilter());
        }
        return imageUrl;
    }

    public void setImageUrl(StringFilter imageUrl) {
        this.imageUrl = imageUrl;
    }

    public BooleanFilter getActivated() {
        return activated;
    }

    public Optional<BooleanFilter> optionalActivated() {
        return Optional.ofNullable(activated);
    }

    public BooleanFilter activated() {
        if (activated == null) {
            setActivated(new BooleanFilter());
        }
        return activated;
    }

    public void setActivated(BooleanFilter activated) {
        this.activated = activated;
    }

    public StringFilter getLangKey() {
        return langKey;
    }

    public Optional<StringFilter> optionalLangKey() {
        return Optional.ofNullable(langKey);
    }

    public StringFilter langKey() {
        if (langKey == null) {
            setLangKey(new StringFilter());
        }
        return langKey;
    }

    public void setLangKey(StringFilter langKey) {
        this.langKey = langKey;
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

    public InstantFilter getCreatedDate() {
        return createdDate;
    }

    public Optional<InstantFilter> optionalCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    public InstantFilter createdDate() {
        if (createdDate == null) {
            setCreatedDate(new InstantFilter());
        }
        return createdDate;
    }

    public void setCreatedDate(InstantFilter createdDate) {
        this.createdDate = createdDate;
    }

    public StringFilter getLastModifiedBy() {
        return lastModifiedBy;
    }

    public Optional<StringFilter> optionalLastModifiedBy() {
        return Optional.ofNullable(lastModifiedBy);
    }

    public StringFilter lastModifiedBy() {
        if (lastModifiedBy == null) {
            setLastModifiedBy(new StringFilter());
        }
        return lastModifiedBy;
    }

    public void setLastModifiedBy(StringFilter lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public InstantFilter getLastModifiedDate() {
        return lastModifiedDate;
    }

    public Optional<InstantFilter> optionalLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    public InstantFilter lastModifiedDate() {
        if (lastModifiedDate == null) {
            setLastModifiedDate(new InstantFilter());
        }
        return lastModifiedDate;
    }

    public void setLastModifiedDate(InstantFilter lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public StringFilter getProfileLink() {
        return profileLink;
    }

    public Optional<StringFilter> optionalProfileLink() {
        return Optional.ofNullable(profileLink);
    }

    public StringFilter profileLink() {
        if (profileLink == null) {
            setProfileLink(new StringFilter());
        }
        return profileLink;
    }

    public void setProfileLink(StringFilter profileLink) {
        this.profileLink = profileLink;
    }

    public StringFilter getBiography() {
        return biography;
    }

    public Optional<StringFilter> optionalBiography() {
        return Optional.ofNullable(biography);
    }

    public StringFilter biography() {
        if (biography == null) {
            setBiography(new StringFilter());
        }
        return biography;
    }

    public void setBiography(StringFilter biography) {
        this.biography = biography;
    }

    public InstantFilter getBirthDate() {
        return birthDate;
    }

    public Optional<InstantFilter> optionalBirthDate() {
        return Optional.ofNullable(birthDate);
    }

    public InstantFilter birthDate() {
        if (birthDate == null) {
            setBirthDate(new InstantFilter());
        }
        return birthDate;
    }

    public void setBirthDate(InstantFilter birthDate) {
        this.birthDate = birthDate;
    }

    public StringFilter getJob() {
        return job;
    }

    public Optional<StringFilter> optionalJob() {
        return Optional.ofNullable(job);
    }

    public StringFilter job() {
        if (job == null) {
            setJob(new StringFilter());
        }
        return job;
    }

    public void setJob(StringFilter job) {
        this.job = job;
    }

    public GenreFilter getSexe() {
        return sexe;
    }

    public Optional<GenreFilter> optionalSexe() {
        return Optional.ofNullable(sexe);
    }

    public GenreFilter sexe() {
        if (sexe == null) {
            setSexe(new GenreFilter());
        }
        return sexe;
    }

    public void setSexe(GenreFilter sexe) {
        this.sexe = sexe;
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
        final UserProfileCriteria that = (UserProfileCriteria) o;
        return (
            Objects.equals(id, that.id) &&
            Objects.equals(login, that.login) &&
            Objects.equals(firstName, that.firstName) &&
            Objects.equals(lastName, that.lastName) &&
            Objects.equals(email, that.email) &&
            Objects.equals(imageUrl, that.imageUrl) &&
            Objects.equals(activated, that.activated) &&
            Objects.equals(langKey, that.langKey) &&
            Objects.equals(createdBy, that.createdBy) &&
            Objects.equals(createdDate, that.createdDate) &&
            Objects.equals(lastModifiedBy, that.lastModifiedBy) &&
            Objects.equals(lastModifiedDate, that.lastModifiedDate) &&
            Objects.equals(profileLink, that.profileLink) &&
            Objects.equals(biography, that.biography) &&
            Objects.equals(birthDate, that.birthDate) &&
            Objects.equals(job, that.job) &&
            Objects.equals(sexe, that.sexe) &&
            Objects.equals(distinct, that.distinct)
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(
            id,
            login,
            firstName,
            lastName,
            email,
            imageUrl,
            activated,
            langKey,
            createdBy,
            createdDate,
            lastModifiedBy,
            lastModifiedDate,
            profileLink,
            biography,
            birthDate,
            job,
            sexe,
            distinct
        );
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfileCriteria{" +
            optionalId().map(f -> "id=" + f + ", ").orElse("") +
            optionalLogin().map(f -> "login=" + f + ", ").orElse("") +
            optionalFirstName().map(f -> "firstName=" + f + ", ").orElse("") +
            optionalLastName().map(f -> "lastName=" + f + ", ").orElse("") +
            optionalEmail().map(f -> "email=" + f + ", ").orElse("") +
            optionalImageUrl().map(f -> "imageUrl=" + f + ", ").orElse("") +
            optionalActivated().map(f -> "activated=" + f + ", ").orElse("") +
            optionalLangKey().map(f -> "langKey=" + f + ", ").orElse("") +
            optionalCreatedBy().map(f -> "createdBy=" + f + ", ").orElse("") +
            optionalCreatedDate().map(f -> "createdDate=" + f + ", ").orElse("") +
            optionalLastModifiedBy().map(f -> "lastModifiedBy=" + f + ", ").orElse("") +
            optionalLastModifiedDate().map(f -> "lastModifiedDate=" + f + ", ").orElse("") +
            optionalProfileLink().map(f -> "profileLink=" + f + ", ").orElse("") +
            optionalBiography().map(f -> "biography=" + f + ", ").orElse("") +
            optionalBirthDate().map(f -> "birthDate=" + f + ", ").orElse("") +
            optionalJob().map(f -> "job=" + f + ", ").orElse("") +
            optionalSexe().map(f -> "sexe=" + f + ", ").orElse("") +
            optionalDistinct().map(f -> "distinct=" + f + ", ").orElse("") +
        "}";
    }
}
