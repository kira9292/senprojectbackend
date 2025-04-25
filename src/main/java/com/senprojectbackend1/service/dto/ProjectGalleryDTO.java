package com.senprojectbackend1.service.dto;

import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.Objects;

/**
 * A DTO for the {@link com.senprojectbackend1.domain.ProjectGallery} entity.
 */
@SuppressWarnings("common-java:DuplicatedBlocks")
public class ProjectGalleryDTO implements Serializable {

    private Long id;

    @NotNull(message = "must not be null")
    @Size(max = 255)
    private String imageUrl;

    @Size(max = 500)
    private String description;

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

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
        if (!(o instanceof ProjectGalleryDTO)) {
            return false;
        }

        ProjectGalleryDTO projectGalleryDTO = (ProjectGalleryDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, projectGalleryDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "ProjectGalleryDTO{" +
            "id=" + getId() +
            ", imageUrl='" + getImageUrl() + "'" +
            ", description='" + getDescription() + "'" +
            ", order=" + getOrder() +
            ", project=" + getProject() +
            "}";
    }
}
