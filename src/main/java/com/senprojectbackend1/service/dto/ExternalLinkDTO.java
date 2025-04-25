package com.senprojectbackend1.service.dto;

import com.senprojectbackend1.domain.enumeration.LinkType;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.senprojectbackend1.domain.ExternalLink} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ExternalLinkDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 150)
    private String title;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String url;

    @NotNull(message = "must not be null")
    private LinkType type;

    @NotNull
    private ProjectDTO project;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LinkType getType() {
        return type;
    }

    public void setType(LinkType type) {
        this.type = type;
    }

    public ProjectDTO getProject() {
        return project;
    }

    public void setProject(ProjectDTO project) {
        this.project = project;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ExternalLinkDTO)) {
            return false;
        }

        ExternalLinkDTO externalLinkDTO = (ExternalLinkDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, externalLinkDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ExternalLinkDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", url='" + getUrl() + "'" +
            ", type='" + getType() + "'" +
            ", project=" + getProject() +
            "}";
    }
}
