package com.senprojectbackend1.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.senprojectbackend1.domain.enumeration.LinkType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * A ExternalLink.
 */
@Table("external_link")
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExternalLink implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @Column("id")
    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 150)
    @Column("title")
    private String title;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    @Column("url")
    private String url;

    @NotNull(message = "must not be null")
    @Column("type")
    private LinkType type;

    @org.springframework.data.annotation.Transient
    @JsonIgnoreProperties(value = { "team", "favoritedbies", "tags" }, allowSetters = true)
    private Project project;

    @Column("project_id")
    private Long projectId;

    // jhipster-needle-entity-add-field - JHipster will add fields here

    public Long getId() {
        return this.id;
    }

    public ExternalLink id(Long id) {
        this.setId(id);
        return this;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public ExternalLink title(String title) {
        this.setTitle(title);
        return this;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getUrl() {
        return this.url;
    }

    public ExternalLink url(String url) {
        this.setUrl(url);
        return this;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LinkType getType() {
        return this.type;
    }

    public ExternalLink type(LinkType type) {
        this.setType(type);
        return this;
    }

    public void setType(LinkType type) {
        this.type = type;
    }

    public Project getProject() {
        return this.project;
    }

    public void setProject(Project project) {
        this.project = project;
        this.projectId = project != null ? project.getId() : null;
    }

    public ExternalLink project(Project project) {
        this.setProject(project);
        return this;
    }

    public Long getProjectId() {
        return this.projectId;
    }

    public void setProjectId(Long project) {
        this.projectId = project;
    }

    // jhipster-needle-entity-add-getters-setters - JHipster will add getters and setters here

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalLink)) {
            return false;
        }
        return getId() != null && getId().equals(((ExternalLink) o).getId());
    }

    @Override
    public int hashCode() {
        // see https://vladmihalcea.com/how-to-implement-equals-and-hashcode-using-the-jpa-entity-identifier/
        return getClass().hashCode();
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExternalLink{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", url='" + getUrl() + "'" +
            ", type='" + getType() + "'" +
            "}";
    }
}
