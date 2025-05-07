package com.senprojectbackend1.service.dto;

import com.senprojectbackend1.domain.enumeration.LinkType;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.domain.enumeration.ProjectType;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.io.Serializable;
import java.util.List;
import java.util.Set;

/**
 * DTO pour la soumission d'un nouveau projet avec toutes ses données associées.
 */
public class ProjectSubmissionDTO implements Serializable {

    @NotNull
    @Size(min = 3, max = 150)
    private String title;

    @NotNull
    @Size(min = 10, max = 2000)
    private String description;

    @Size(max = 50000)
    private String showcase;

    private ProjectType type;

    private Boolean openToCollaboration = false;

    private Boolean openToFunding = false;

    private Long teamId;

    private Set<Long> tagIds;

    private List<SectionDTO> sections;

    private List<GalleryImageDTO> galleryImages;

    private List<ExternalLinkDTO> externalLinks;

    private Long id;

    private List<TagInputDTO> tags;

    private ProjectStatus status;

    // Getters et setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShowcase() {
        return showcase;
    }

    public void setShowcase(String showcase) {
        this.showcase = showcase;
    }

    public ProjectType getType() {
        return type;
    }

    public void setType(ProjectType type) {
        this.type = type;
    }

    public Boolean isOpenToCollaboration() {
        return openToCollaboration;
    }

    public void setOpenToCollaboration(Boolean openToCollaboration) {
        this.openToCollaboration = openToCollaboration;
    }

    public Boolean isOpenToFunding() {
        return openToFunding;
    }

    public void setOpenToFunding(Boolean openToFunding) {
        this.openToFunding = openToFunding;
    }

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public Set<Long> getTagIds() {
        return tagIds;
    }

    public void setTagIds(Set<Long> tagIds) {
        this.tagIds = tagIds;
    }

    public List<SectionDTO> getSections() {
        return sections;
    }

    public void setSections(List<SectionDTO> sections) {
        this.sections = sections;
    }

    public List<GalleryImageDTO> getGalleryImages() {
        return galleryImages;
    }

    public void setGalleryImages(List<GalleryImageDTO> galleryImages) {
        this.galleryImages = galleryImages;
    }

    public List<ExternalLinkDTO> getExternalLinks() {
        return externalLinks;
    }

    public void setExternalLinks(List<ExternalLinkDTO> externalLinks) {
        this.externalLinks = externalLinks;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<TagInputDTO> getTags() {
        return tags;
    }

    public void setTags(List<TagInputDTO> tags) {
        this.tags = tags;
    }

    public ProjectStatus getStatus() {
        return status;
    }

    public void setStatus(ProjectStatus status) {
        this.status = status;
    }

    // Classes imbriquées pour les sous-entités
    public static class SectionDTO implements Serializable {

        @NotNull
        @Size(min = 3, max = 150)
        private String title;

        @NotNull
        @Size(min = 10, max = 5000)
        private String content;

        @Size(max = 255)
        private String mediaUrl;

        // Getters et setters
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
    }

    public static class GalleryImageDTO implements Serializable {

        @NotNull
        @Size(max = 255)
        private String imageUrl;

        @Size(max = 500)
        private String description;

        // Getters et setters
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
    }

    public static class ExternalLinkDTO implements Serializable {

        @NotNull
        @Size(min = 3, max = 150)
        private String title;

        @NotNull
        @Size(max = 255)
        private String url;

        @NotNull
        private LinkType type;

        // Getters et setters
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
    }

    public static class TagInputDTO implements Serializable {

        @NotNull
        @Size(min = 2, max = 50)
        private String name;

        @Size(max = 7)
        private String color; // Optionnel, format #RRGGBB

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getColor() {
            return color;
        }

        public void setColor(String color) {
            this.color = color;
        }
    }

    @Override
    public String toString() {
        return (
            "ProjectSubmissionDTO{" +
            "title='" +
            title +
            '\'' +
            ", description='" +
            description +
            '\'' +
            ", type=" +
            type +
            ", teamId='" +
            teamId +
            '\'' +
            ", tagIds=" +
            tagIds +
            ", sections=" +
            (sections != null ? sections.size() : 0) +
            ", galleryImages=" +
            (galleryImages != null ? galleryImages.size() : 0) +
            ", externalLinks=" +
            (externalLinks != null ? externalLinks.size() : 0) +
            '}'
        );
    }
}
