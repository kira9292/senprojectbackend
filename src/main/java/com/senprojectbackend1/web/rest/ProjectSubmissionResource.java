// Contrôleur REST pour la soumission de projets
package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.ExternalLink;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.ProjectGallery;
import com.senprojectbackend1.domain.ProjectSection;
import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.domain.enumeration.ProjectType;
import com.senprojectbackend1.repository.*;
import com.senprojectbackend1.security.SecurityUtils;
import com.senprojectbackend1.service.ProjectService;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSubmissionDTO;
import com.senprojectbackend1.service.mapper.ProjectMapper;
import com.senprojectbackend1.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;

/**
 * REST controller for managing {@link com.senprojectbackend1.domain.Project} submissions.
 */
@RestController
@RequestMapping("/api/projects")
public class ProjectSubmissionResource {

    private final Logger log = LoggerFactory.getLogger(ProjectSubmissionResource.class);

    private static final String ENTITY_NAME = "project";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final ProjectService projectService;
    private final ProjectRepository projectRepository;
    private final TeamRepository teamRepository;
    private final TagRepository tagRepository;
    private final UserProfileRepository userProfileRepository;
    private final ProjectMapper projectMapper;
    private final ProjectSectionRepository projectSectionRepository;
    private final ProjectGalleryRepository projectGalleryRepository;
    private final ExternalLinkRepository externalLinkRepository;

    public ProjectSubmissionResource(
        ProjectService projectService,
        ProjectRepository projectRepository,
        TeamRepository teamRepository,
        TagRepository tagRepository,
        UserProfileRepository userProfileRepository,
        ProjectMapper projectMapper,
        ProjectSectionRepository projectSectionRepository,
        ProjectGalleryRepository projectGalleryRepository,
        ExternalLinkRepository externalLinkRepository
    ) {
        this.projectService = projectService;
        this.projectRepository = projectRepository;
        this.teamRepository = teamRepository;
        this.tagRepository = tagRepository;
        this.userProfileRepository = userProfileRepository;
        this.projectMapper = projectMapper;
        this.projectSectionRepository = projectSectionRepository;
        this.projectGalleryRepository = projectGalleryRepository;
        this.externalLinkRepository = externalLinkRepository;
    }

    /**
     * {@code POST  /projects/submit} : Submit a new project.
     *
     * @param projectSubmissionDTO the project to submit.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new projectDTO.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("/submit")
    public Mono<ResponseEntity<ProjectDTO>> submitProject(@Valid @RequestBody ProjectSubmissionDTO projectSubmissionDTO)
        throws URISyntaxException {
        log.debug("REST request to submit Project : {}", projectSubmissionDTO);

        // Validation logique supplémentaire si nécessaire
        if (projectSubmissionDTO.getTitle() == null || projectSubmissionDTO.getTitle().trim().length() < 3) {
            return Mono.error(
                new BadRequestAlertException("Le titre du projet doit contenir au moins 3 caractères", ENTITY_NAME, "titleinvalid")
            );
        }

        // Récupérer l'utilisateur courant
        return SecurityUtils.getCurrentUserLogin()
            .switchIfEmpty(Mono.error(new BadRequestAlertException("Utilisateur courant non trouvé", ENTITY_NAME, "usernotfound")))
            .flatMap(currentUserLogin -> {
                // Créer le projet avec les valeurs par défaut
                Project project = new Project()
                    .title(projectSubmissionDTO.getTitle())
                    .description(projectSubmissionDTO.getDescription())
                    .showcase(projectSubmissionDTO.getShowcase())
                    .status(ProjectStatus.WAITING_VALIDATION) // Par défaut en attente de validation
                    .createdAt(Instant.now())
                    .updatedAt(Instant.now())
                    .openToCollaboration(projectSubmissionDTO.isOpenToCollaboration())
                    .openToFunding(projectSubmissionDTO.isOpenToFunding())
                    .type(projectSubmissionDTO.getType())
                    .totalLikes(0)
                    .totalShares(0)
                    .totalViews(0)
                    .totalComments(0)
                    .totalFavorites(0)
                    .isDeleted(false)
                    .createdBy(currentUserLogin)
                    .lastUpdatedBy(currentUserLogin);

                // Traiter les associations de manière réactive
                Mono<Project> projectWithAssociations = processAssociations(project, projectSubmissionDTO);

                // Sauvegarder le projet
                return projectWithAssociations
                    .flatMap(projectRepository::save)
                    .flatMap(savedProject -> {
                        // Ajouter les sections, galeries et liens de manière réactive
                        return processProjectSections(savedProject, projectSubmissionDTO)
                            .then(processGalleryImages(savedProject, projectSubmissionDTO))
                            .then(processExternalLinks(savedProject, projectSubmissionDTO))
                            .thenReturn(savedProject);
                    })
                    .map(projectMapper::toDto)
                    .map(result -> {
                        try {
                            return ResponseEntity.created(new URI("/api/projects/" + result.getId())).body(result);
                        } catch (URISyntaxException e) {
                            throw new RuntimeException(e);
                        }
                    });
            });
    }

    /**
     * Process les associations avec l'équipe et les tags
     */
    private Mono<Project> processAssociations(Project project, ProjectSubmissionDTO projectSubmissionDTO) {
        Mono<Project> projectMono = Mono.just(project);

        // Associer l'équipe si fournie
        if (projectSubmissionDTO.getTeamId() != null) {
            projectMono = projectMono.flatMap(p -> teamRepository.findById(projectSubmissionDTO.getTeamId()).map(p::team).defaultIfEmpty(p)
            );
        }

        // Associer les tags si fournis
        if (projectSubmissionDTO.getTagIds() != null && !projectSubmissionDTO.getTagIds().isEmpty()) {
            projectMono = projectMono.flatMap(p -> {
                // Collecte tous les tags en un seul Mono
                Mono<Project> taggedProject = Mono.just(p);

                for (Long tagId : projectSubmissionDTO.getTagIds()) {
                    taggedProject = taggedProject.flatMap(proj ->
                        tagRepository
                            .findById(tagId)
                            .map(tag -> {
                                proj.addTags(tag);
                                return proj;
                            })
                            .defaultIfEmpty(proj)
                    );
                }

                return taggedProject;
            });
        }

        return projectMono;
    }

    /**
     * Process les sections du projet
     */
    private Mono<Void> processProjectSections(Project savedProject, ProjectSubmissionDTO projectSubmissionDTO) {
        if (projectSubmissionDTO.getSections() == null || projectSubmissionDTO.getSections().isEmpty()) {
            return Mono.empty();
        }

        return Flux.fromIterable(projectSubmissionDTO.getSections())
            .index()
            .flatMap(tuple -> {
                int order = tuple.getT1().intValue();
                ProjectSubmissionDTO.SectionDTO sectionDTO = tuple.getT2();

                ProjectSection section = new ProjectSection()
                    .title(sectionDTO.getTitle())
                    .content(sectionDTO.getContent())
                    .mediaUrl(sectionDTO.getMediaUrl())
                    .order(order)
                    .project(savedProject);

                return projectSectionRepository.save(section);
            })
            .then(); // Convertir Flux<Void> en Mono<Void>
    }

    /**
     * Process les images de galerie
     */
    private Mono<Void> processGalleryImages(Project savedProject, ProjectSubmissionDTO projectSubmissionDTO) {
        if (projectSubmissionDTO.getGalleryImages() == null || projectSubmissionDTO.getGalleryImages().isEmpty()) {
            return Mono.empty();
        }

        // Traiter chaque image en séquence
        Mono<Void> result = Mono.empty();
        int order = 0;

        for (ProjectSubmissionDTO.GalleryImageDTO imageDTO : projectSubmissionDTO.getGalleryImages()) {
            final int currentOrder = order++;

            ProjectGallery galleryImage = new ProjectGallery()
                .imageUrl(imageDTO.getImageUrl())
                .description(imageDTO.getDescription())
                .order(currentOrder)
                .project(savedProject);

            result = result.then(projectGalleryRepository.save(galleryImage).then());

            // Comme placeholder en attendant le repository:
            result = result.then(Mono.empty());
        }

        return result;
    }

    /**
     * Process les liens externes
     */
    private Mono<Void> processExternalLinks(Project savedProject, ProjectSubmissionDTO projectSubmissionDTO) {
        if (projectSubmissionDTO.getExternalLinks() == null || projectSubmissionDTO.getExternalLinks().isEmpty()) {
            return Mono.empty();
        }

        // Traiter chaque lien en séquence
        Mono<Void> result = Mono.empty();

        for (ProjectSubmissionDTO.ExternalLinkDTO linkDTO : projectSubmissionDTO.getExternalLinks()) {
            ExternalLink link = new ExternalLink()
                .title(linkDTO.getTitle())
                .url(linkDTO.getUrl())
                .type(linkDTO.getType())
                .project(savedProject);

            result = result.then(externalLinkRepository.save(link).then());

            // Comme placeholder en attendant le repository:
            result = result.then(Mono.empty());
        }

        return result;
    }
}
