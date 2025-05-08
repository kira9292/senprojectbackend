package com.senprojectbackend1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senprojectbackend1.domain.enumeration.Genre;
import jakarta.validation.constraints.*;
import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A UserProfile.
 */
@Table("user_profile")
@JsonIgnoreProperties(value = { "new" })
@SuppressWarnings("common-java:DuplicatedBlocks")
public class UserProfile implements Serializable, Persistable<String> {

    @Serial
    private static final long serialVersionUID = 1L;

    @NotNull(message = "must not be null")
    @Id
    @Column("id")
    private String id;

    @NotNull(message = "must not be null")
    @Size(min = 1, max = 50)
    @Column("login")
    private String login;

    @Size(max = 50)
    @Column("first_name")
    private String firstName;

    @Size(max = 50)
    @Column("last_name")
    private String lastName;

    @NotNull(message = "must not be null")
    @Size(min = 5, max = 254)
    @Column("email")
    private String email;

    @Column("image_url")
    private String imageUrl;

    @NotNull(message = "must not be null")
    @Column("activated")
    private Boolean activated;

    @Size(max = 6)
    @Column("lang_key")
    private String langKey;

    @Size(max = 50)
    @Column("created_by")
    private String createdBy;

    @NotNull(message = "must not be null")
    @Column("created_date")
    private Instant createdDate;

    @Size(max = 50)
    @Column("last_modified_by")
    private String lastModifiedBy;

    @Column("last_modified_date")
    private Instant lastModifiedDate;

    @Size(max = 255)
    @Column("profile_link")
    private String profileLink;

    @Size(max = 1000)
    @Column("biography")
    private String biography;

    @Column("birth_date")
    private Instant birthDate;

    @Size(max = 100)
    @Column("job")
    private String job;

    @Column("sexe")
    private Genre sexe;

    @org.springframework.data.annotation.Transient
    private boolean isPersisted;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "team", "favoritedbies", "tags" }, allowSetters = true)
    private Set<Project> projects = new HashSet<>();

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "members" }, allowSetters = true)
    private Set<Team> teams = new HashSet<>();

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public String getId() {
        return this.id;
    }

    public UserProfile id(String id) {
        this.setId(id);
        return this;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLogin() {
        return this.login;
    }

    public UserProfile login(String login) {
        this.setLogin(login);
        return this;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getFirstName() {
        return this.firstName;
    }

    public UserProfile firstName(String firstName) {
        this.setFirstName(firstName);
        return this;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return this.lastName;
    }

    public UserProfile lastName(String lastName) {
        this.setLastName(lastName);
        return this;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return this.email;
    }

    public UserProfile email(String email) {
        this.setEmail(email);
        return this;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImageUrl() {
        return this.imageUrl;
    }

    public UserProfile imageUrl(String imageUrl) {
        this.setImageUrl(imageUrl);
        return this;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Boolean getActivated() {
        return this.activated;
    }

    public UserProfile activated(Boolean activated) {
        this.setActivated(activated);
        return this;
    }

    public void setActivated(Boolean activated) {
        this.activated = activated;
    }

    public String getLangKey() {
        return this.langKey;
    }

    public UserProfile langKey(String langKey) {
        this.setLangKey(langKey);
        return this;
    }

    public void setLangKey(String langKey) {
        this.langKey = langKey;
    }

    public String getCreatedBy() {
        return this.createdBy;
    }

    public UserProfile createdBy(String createdBy) {
        this.setCreatedBy(createdBy);
        return this;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedDate() {
        return this.createdDate;
    }

    public UserProfile createdDate(Instant createdDate) {
        this.setCreatedDate(createdDate);
        return this;
    }

    public void setCreatedDate(Instant createdDate) {
        this.createdDate = createdDate;
    }

    public String getLastModifiedBy() {
        return this.lastModifiedBy;
    }

    public UserProfile lastModifiedBy(String lastModifiedBy) {
        this.setLastModifiedBy(lastModifiedBy);
        return this;
    }

    public void setLastModifiedBy(String lastModifiedBy) {
        this.lastModifiedBy = lastModifiedBy;
    }

    public Instant getLastModifiedDate() {
        return this.lastModifiedDate;
    }

    public UserProfile lastModifiedDate(Instant lastModifiedDate) {
        this.setLastModifiedDate(lastModifiedDate);
        return this;
    }

    public void setLastModifiedDate(Instant lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getProfileLink() {
        return this.profileLink;
    }

    public UserProfile profileLink(String profileLink) {
        this.setProfileLink(profileLink);
        return this;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getBiography() {
        return this.biography;
    }

    public UserProfile biography(String biography) {
        this.setBiography(biography);
        return this;
    }

    public void setBiography(String biography) {
        this.biography = biography;
    }

    public Instant getBirthDate() {
        return this.birthDate;
    }

    public UserProfile birthDate(Instant birthDate) {
        this.setBirthDate(birthDate);
        return this;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public String getJob() {
        return this.job;
    }

    public UserProfile job(String job) {
        this.setJob(job);
        return this;
    }

    public void setJob(String job) {
        this.job = job;
    }

    public Genre getSexe() {
        return this.sexe;
    }

    public UserProfile sexe(Genre sexe) {
        this.setSexe(sexe);
        return this;
    }

    public void setSexe(Genre sexe) {
        this.sexe = sexe;
    }

    @org.springframework.data.annotation.Transient
    @Override
    public boolean isNew() {
        return !this.isPersisted;
    }

    public UserProfile setIsPersisted() {
        this.isPersisted = true;
        return this;
    }

    public Set<Project> getProjects() {
        return this.projects;
    }

    public void setProjects(Set<Project> projects) {
        if (this.projects != null) {
            this.projects.forEach(i -> i.removeFavoritedby(this));
        }
        if (projects != null) {
            projects.forEach(i -> i.addFavoritedby(this));
        }
        this.projects = projects;
    }

    public UserProfile projects(Set<Project> projects) {
        this.setProjects(projects);
        return this;
    }

    public UserProfile addProject(Project project) {
        this.projects.add(project);
        project.getFavoritedbies().add(this);
        return this;
    }

    public UserProfile removeProject(Project project) {
        this.projects.remove(project);
        project.getFavoritedbies().remove(this);
        return this;
    }

    public Set<Team> getTeams() {
        return this.teams;
    }

    public void setTeams(Set<Team> teams) {
        if (this.teams != null) {
            this.teams.forEach(i -> i.removeMembers(this));
        }
        if (teams != null) {
            teams.forEach(i -> i.addMembers(this));
        }
        this.teams = teams;
    }

    public UserProfile teams(Set<Team> teams) {
        this.setTeams(teams);
        return this;
    }

    public void addTeam(Team team) {
        this.teams.add(team);
        team.getMembers().add(this);
    }

    public void removeTeam(Team team) {
        this.teams.remove(team);
        team.getMembers().remove(this);
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof UserProfile)) {
            return false;
        }
        UserProfile other = (UserProfile) o;
        // Si les deux objets ont un ID, comparer uniquement les IDs
        if (this.getId() != null && other.getId() != null) {
            return Objects.equals(this.getId(), other.getId());
        }
        // Si un seul objet a un ID, ils sont différents
        if (this.getId() != null || other.getId() != null) {
            return false;
        }
        // Si les deux objets n'ont pas d'ID, comparer login et email
        // Si login et email sont tous les deux null, les objets sont différents
        if (this.getLogin() == null && this.getEmail() == null) {
            return false;
        }
        return Objects.equals(this.getLogin(), other.getLogin()) && Objects.equals(this.getEmail(), other.getEmail());
    }

    @Override
    public int hashCode() {
        // Si l'ID est présent, utiliser uniquement l'ID
        if (getId() != null) {
            return Objects.hash(getId());
        }
        // Sinon, utiliser login et email
        // Si les deux sont null, utiliser un hashcode constant différent de 0
        if (getLogin() == null && getEmail() == null) {
            return 31;
        }
        return Objects.hash(getLogin(), getEmail());
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "UserProfile{" +
            "id=" + getId() +
            ", login='" + getLogin() + "'" +
            ", firstName='" + getFirstName() + "'" +
            ", lastName='" + getLastName() + "'" +
            ", email='" + getEmail() + "'" +
            ", imageUrl='" + getImageUrl() + "'" +
            ", activated='" + getActivated() + "'" +
            ", langKey='" + getLangKey() + "'" +
            ", createdBy='" + getCreatedBy() + "'" +
            ", createdDate='" + getCreatedDate() + "'" +
            ", lastModifiedBy='" + getLastModifiedBy() + "'" +
            ", lastModifiedDate='" + getLastModifiedDate() + "'" +
            ", profileLink='" + getProfileLink() + "'" +
            ", biography='" + getBiography() + "'" +
            ", birthDate='" + getBirthDate() + "'" +
            ", job='" + getJob() + "'" +
            ", sexe='" + getSexe() + "'" +
            "}";
    }
}
