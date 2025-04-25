package com.senprojectbackend1.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.senprojectbackend1.domain.ProjectSection} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectSectionDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(min = 3, max = 150)
    private String title;

    @NotNull(message = "must not be null")
    @Size(min = 10, max = 5000)
    private String content;

    @Size(max = 255)
    private String mediaUrl;

    @NotNull(message = "must not be null")
    private Integer order;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getMediaUrl() {
        return mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
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
        if (!(o instanceof ProjectSectionDTO)) {
            return false;
        }

        ProjectSectionDTO projectSectionDTO = (ProjectSectionDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectSectionDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectSectionDTO{" +
            "id=" + getId() +
            ", title='" + getTitle() + "'" +
            ", content='" + getContent() + "'" +
            ", mediaUrl='" + getMediaUrl() + "'" +
            ", order=" + getOrder() +
            ", project=" + getProject() +
            "}";
    }
}
