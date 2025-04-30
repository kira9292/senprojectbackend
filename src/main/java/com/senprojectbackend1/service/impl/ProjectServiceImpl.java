package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.*;
import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.repository.*;
import com.senprojectbackend1.service.NotificationService;
import com.senprojectbackend1.service.ProjectService;
import com.senprojectbackend1.service.TagService;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.*;
import com.senprojectbackend1.service.mapper.ProjectMapper;
import com.senprojectbackend1.service.mapper.ProjectSectionMapper;
import com.senprojectbackend1.service.mapper.ProjectSimpleMapper;
import com.senprojectbackend1.web.rest.errors.BadRequestAlertException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.Project}.
 */
@Service
@Transactional
public class ProjectServiceImpl implements ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectServiceImpl.class);

    private final ProjectRepository projectRepository;
    private final ProjectSectionRepository projectSectionRepository;
    private final ProjectMapper projectMapper;
    private final ProjectSectionMapper projectSectionMapper;
    private final ProjectSimpleMapper projectSimpleMapper;
    private final TagRepository tagRepository;
    private final UserProfileRepository userProfileRepository;
    private final NotificationService notificationService;
    private final TeamMembershipRepository teamMembershipRepository;
    private final UserProfileService userProfileService;
    private final EngagementProjectRepository engagementProjectRepository;
    private final TagService tagService;
    private final TeamRepository teamRepository;
    private final ExternalLinkRepository externalLinkRepository;
    private final ProjectGalleryRepository projectGalleryRepository;

    public ProjectServiceImpl(
        ProjectRepository projectRepository,
        ProjectSectionRepository projectSectionRepository,
        ProjectMapper projectMapper,
        ProjectSectionMapper projectSectionMapper,
        ProjectSimpleMapper projectSimpleMapper,
        TagRepository tagRepository,
        UserProfileRepository userProfileRepository,
        NotificationService notificationService,
        TeamMembershipRepository teamMembershipRepository,
        UserProfileService userProfileService,
        EngagementProjectRepository engagementProjectRepository,
        TagService tagService,
        TeamRepository teamRepository,
        ExternalLinkRepository externalLinkRepository,
        ProjectGalleryRepository projectGalleryRepository
    ) {
        this.projectRepository = projectRepository;
        this.projectSectionRepository = projectSectionRepository;
        this.projectMapper = projectMapper;
        this.projectSectionMapper = projectSectionMapper;
        this.projectSimpleMapper = projectSimpleMapper;
        this.tagRepository = tagRepository;
        this.userProfileRepository = userProfileRepository;
        this.notificationService = notificationService;
        this.teamMembershipRepository = teamMembershipRepository;
        this.userProfileService = userProfileService;
        this.engagementProjectRepository = engagementProjectRepository;
        this.tagService = tagService;
        this.teamRepository = teamRepository;
        this.externalLinkRepository = externalLinkRepository;
        this.projectGalleryRepository = projectGalleryRepository;
    }

    @Override
    public Mono<ProjectDTO> save(ProjectDTO projectDTO) {
        LOG.debug("Request to save Project : {}", projectDTO);
        return projectRepository.save(projectMapper.toEntity(projectDTO)).map(projectMapper::toDto);
    }

    @Override
    public Mono<ProjectDTO> update(ProjectDTO projectDTO) {
        LOG.debug("Request to update Project : {}", projectDTO);
        return projectRepository.save(projectMapper.toEntity(projectDTO)).map(projectMapper::toDto);
    }

    @Override
    public Mono<ProjectDTO> partialUpdate(ProjectDTO projectDTO) {
        LOG.debug("Request to partially update Project : {}", projectDTO);

        return projectRepository
            .findById(projectDTO.getId())
            .map(existingProject -> {
                projectMapper.partialUpdate(existingProject, projectDTO);

                return existingProject;
            })
            .flatMap(projectRepository::save)
            .map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectDTO> findByCriteria(ProjectCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Projects by Criteria");
        return projectRepository.findByCriteria(criteria, pageable).map(projectMapper::toDto);
    }

    /**
     * Find the count of projects by criteria.
     * @param criteria filtering criteria
     * @return the count of projects
     */
    public Mono<Long> countByCriteria(ProjectCriteria criteria) {
        LOG.debug("Request to get the count of all Projects by Criteria");
        return projectRepository.countByCriteria(criteria);
    }

    public Flux<ProjectDTO> findAllWithEagerRelationships(Pageable pageable) {
        return projectRepository.findAllWithEagerRelationships(pageable).map(projectMapper::toDto);
    }

    public Mono<Long> countAll() {
        return projectRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProjectDTO> findOne(Long id) {
        LOG.debug("Request to get Project : {}", id);
        return projectRepository.findOneWithEagerRelationships(id).map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProjectSimpleDTO> findOneSimple(Long id) {
        LOG.debug("Request to get Project with tags : {}", id);

        return projectRepository
            .findOneWithEagerRelationships(id)
            .flatMap(project -> {
                ProjectSimpleDTO dto = projectSimpleMapper.toDto(project);

                // Récupérer et mapper les tags du projet
                Mono<Set<TagDTO>> tagsMono = tagRepository
                    .findByProjectId(project.getId())
                    .map(tag -> {
                        TagDTO tagDTO = new TagDTO();
                        tagDTO.setId(tag.getId());
                        tagDTO.setName(tag.getName());
                        return tagDTO;
                    })
                    .collect(Collectors.toSet())
                    .doOnNext(tags -> LOG.debug("Project tags found: {}", tags));

                // Fusionner les résultats et construire le DTO final
                return tagsMono.map(tags -> {
                    dto.setTags(tags);
                    LOG.debug("Project simple DTO created with tags for id: {}", project.getId());
                    return dto;
                });
            });
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Project : {}", id);
        return projectRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectSimpleDTO> findAllProjectsOfCurrentUser(String userLogin) {
        LOG.debug("Request to get all Projects of current user: {}", userLogin);

        return userProfileRepository
            .findOneByLogin(userLogin)
            .flatMapMany(userProfile -> {
                if (userProfile == null) {
                    return Flux.empty();
                }

                // Utiliser la méthode qui charge les relations eager comme findOneWithEagerRelationships
                return projectRepository
                    .findAllByTeamMemberWithEagerRelationships(userProfile.getId())
                    .flatMap(project -> {
                        ProjectSimpleDTO dto = projectSimpleMapper.toDto(project);

                        // Récupérer et mapper les tags du projet
                        Mono<Set<TagDTO>> tagsMono = tagRepository
                            .findByProjectId(project.getId())
                            .map(tag -> {
                                TagDTO tagDTO = new TagDTO();
                                tagDTO.setId(tag.getId());
                                tagDTO.setName(tag.getName());
                                return tagDTO;
                            })
                            .collect(Collectors.toSet())
                            .doOnNext(tags -> LOG.debug("Project tags found: {}", tags));

                        // Fusionner les résultats et construire le DTO final
                        return tagsMono.map(tags -> {
                            dto.setTags(tags);
                            LOG.debug("Project simple DTO created with tags for id: {}", project.getId());
                            return dto;
                        });
                    });
            });
    }

    @Override
    public Mono<ProjectDTO> getProject(Long id) {
        return findOne(id);
    }

    @Override
    public Mono<ProjectDTO> updateProject(ProjectDTO projectDTO) {
        return update(projectDTO);
    }

    @Override
    public Mono<Void> deleteProject(Long id) {
        LOG.debug("Request to delete Project : {}", id);
        return findOne(id).flatMap(project -> {
            // D'abord supprimer le projet
            return delete(id).then(
                Mono.defer(() -> {
                    // Ensuite créer la notification pour tous les membres de l'équipe
                    if (project.getTeam() != null && project.getTeam().getId() != null) {
                        LOG.debug("Creating notifications for team members of project: {}", project.getTitle());
                        return teamMembershipRepository
                            .findAll()
                            .filter(membership -> membership.getTeamId().equals(project.getTeam().getId()))
                            .flatMap(membership -> {
                                LOG.debug("Processing notification for team member: {}", membership.getMembersId());
                                return userProfileRepository
                                    .findById(membership.getMembersId())
                                    .flatMap(user -> {
                                        LOG.debug("Creating notification for user: {}", user.getLogin());
                                        return notificationService.createNotification(
                                            user.getId(),
                                            "Le projet '" + project.getTitle() + "' a été supprimé",
                                            NotificationType.PROJECT_DELETED,
                                            id.toString()
                                        );
                                    })
                                    .onErrorResume(e -> {
                                        LOG.error("Error creating notification for user {}: {}", membership.getMembersId(), e.getMessage());
                                        return Mono.empty();
                                    });
                            })
                            .then();
                    }
                    return Mono.empty();
                })
            );
        });
    }

    @Override
    public Mono<ProjectDTO> approveProject(Long id) {
        return findOne(id).flatMap(project -> {
            project.setStatus(ProjectStatus.PUBLISHED);
            return update(project);
        });
    }

    @Override
    public Mono<ProjectDTO> rejectProject(Long id) {
        return findOne(id).flatMap(project -> {
            project.setStatus(ProjectStatus.ARCHIVED);
            return update(project).flatMap(updatedProject ->
                userProfileRepository
                    .findById(updatedProject.getCreatedBy())
                    .flatMap(user ->
                        notificationService
                            .createNotification(user.getId(), "Votre projet a été rejeté", NotificationType.PROJECT_REJECTED, id.toString())
                            .thenReturn(updatedProject)
                    )
                    .onErrorResume(e -> {
                        LOG.error("Error creating notification for user {}: {}", updatedProject.getCreatedBy(), e.getMessage());
                        return Mono.just(updatedProject);
                    })
            );
        });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProjectDTO> findOneWithSections(Long id) {
        LOG.debug("Request to get Project with sections : {}", id);
        return projectRepository
            .findById(id)
            .flatMap(project -> {
                ProjectDTO projectDTO = projectMapper.toDto(project);

                return Mono.zip(
                    projectSectionRepository.findByProject(id).collectList(),
                    tagService.findByProjectId(id).collect(Collectors.toSet())
                ).map(tuple -> {
                    projectDTO.setSections(projectSectionMapper.toDto(tuple.getT1()));
                    projectDTO.setTags(tuple.getT2());
                    return projectDTO;
                });
            });
    }

    /**
     * Récupère un projet avec ses sections et gère l'incrémentation des vues.
     *
     * @param id l'ID du projet
     * @param login le login de l'utilisateur
     * @return le projet avec ses sections
     */
    @Override
    @Transactional
    public Mono<ProjectDTO> findOneWithSectionsAndIncrementViews(Long id, String login) {
        LOG.debug("Request to get Project with sections and increment views : {}, login: {}", id, login);

        return userProfileService
            .getUserProfileSimpleByLogin(login)
            .flatMap(userProfile -> {
                if (userProfile == null) {
                    LOG.error("User profile not found for login: {}", login);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
                }

                String userId = userProfile.getId();
                // Vérifier si l'utilisateur a déjà vu ce projet
                return engagementProjectRepository
                    .findEngagementByUserIdAndProjectIdAndType(userId, id, "VIEW")
                    .flatMap(existingEngagement -> {
                        // Si l'engagement existe déjà, on ne fait rien de plus
                        return findOneWithSections(id);
                    })
                    .switchIfEmpty(
                        // Si l'engagement n'existe pas, on crée un nouvel engagement et on incrémente les vues
                        Mono.defer(() -> {
                            EngagementProject engagement = new EngagementProject();
                            engagement.setType(EngagementType.VIEW);
                            engagement.setCreatedAt(Instant.now());
                            engagement.setUserId(userId);
                            engagement.setProjectId(id);

                            return engagementProjectRepository
                                .save(engagement)
                                .then(projectRepository.incrementTotalViews(id))
                                .then(findOneWithSections(id));
                        })
                    );
            });
    }

    @Override
    @Transactional
    public Mono<Boolean> toggleFavorite(Long id, String login) {
        LOG.debug("Request to toggle favorite status for Project : {}, login: {}", id, login);

        return userProfileService
            .getUserProfileSimpleByLogin(login)
            .flatMap(userProfile -> {
                if (userProfile == null) {
                    LOG.error("User profile not found for login: {}", login);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
                }

                String userId = userProfile.getId();

                return projectRepository
                    .isFavorite(id, userId)
                    .flatMap(isFavorite -> {
                        if (isFavorite) {
                            // Si le projet est déjà en favori, on le retire
                            return projectRepository
                                .removeFromFavorites(id, userId)
                                .then(projectRepository.decrementTotalFavorites(id))
                                .thenReturn(false);
                        } else {
                            // Si le projet n'est pas en favori, on l'ajoute
                            return projectRepository
                                .addToFavorites(id, userId)
                                .then(projectRepository.incrementTotalFavorites(id))
                                .thenReturn(true);
                        }
                    });
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<Boolean> isFavorite(Long id, String login) {
        LOG.debug("Request to check if Project is favorite : {}, login: {}", id, login);

        return userProfileService
            .getUserProfileSimpleByLogin(login)
            .flatMap(userProfile -> {
                if (userProfile == null) {
                    LOG.error("User profile not found for login: {}", login);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
                }

                return projectRepository.isFavorite(id, userProfile.getId());
            });
    }

    @Override
    public Mono<Void> markProjectAsDeleted(Long id, String userLogin) {
        LOG.debug("Request to mark Project as deleted : {}, user: {}", id, userLogin);
        return userProfileService
            .getUserProfileSimpleByLogin(userLogin)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found")))
            .flatMap(userProfile -> {
                return projectRepository
                    .findById(id)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")))
                    .flatMap(project -> {
                        if (project.getTeam() == null) {
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project has no team"));
                        }
                        return teamRepository
                            .findMemberStatus(project.getTeam().getId(), userProfile.getId())
                            .switchIfEmpty(
                                Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of the team"))
                            )
                            .flatMap(status -> {
                                if (!"ACCEPTED".equals(status)) {
                                    return Mono.error(
                                        new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an accepted member of the team")
                                    );
                                }
                                return projectRepository.updateProjectStatusToDeleted(id);
                            });
                    });
            });
    }

    @Override
    public Mono<ProjectDTO> changeProjectStatus(Long projectId, String newStatus, String userLogin) {
        return projectRepository
            .findById(projectId)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Projet introuvable")))
            .flatMap(project -> {
                if (project.getTeamId() == null) {
                    return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Projet sans équipe"));
                }
                return userProfileRepository
                    .findOneByLogin(userLogin)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found")))
                    .flatMap(userProfile ->
                        teamMembershipRepository
                            .findByTeamIdAndUserId(project.getTeamId(), userProfile.getId())
                            .switchIfEmpty(
                                Mono.error(
                                    new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas membre de l'équipe du projet")
                                )
                            )
                            .flatMap(membership -> {
                                if (!"ACCEPTED".equals(membership.getStatus())) {
                                    return Mono.error(
                                        new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous devez être membre accepté de l'équipe")
                                    );
                                }
                                String role = membership.getRole();
                                if ("PUBLISHED".equalsIgnoreCase(newStatus)) {
                                    return com.senprojectbackend1.security.SecurityUtils.hasCurrentUserAnyOfAuthorities(
                                        "ROLE_ADMIN",
                                        "ROLE_SUPPORT"
                                    ).flatMap(isAdminOrSupport -> {
                                        if (!isAdminOrSupport) {
                                            return Mono.error(
                                                new ResponseStatusException(
                                                    HttpStatus.FORBIDDEN,
                                                    "Seuls les admins/support peuvent publier"
                                                )
                                            );
                                        }
                                        project.setStatus(com.senprojectbackend1.domain.enumeration.ProjectStatus.valueOf(newStatus));
                                        return projectRepository.save(project).map(projectMapper::toDto);
                                    });
                                } else if (!"LEAD".equals(role) && !"MODIFY".equals(role)) {
                                    return Mono.error(
                                        new ResponseStatusException(
                                            HttpStatus.FORBIDDEN,
                                            "Seuls les LEAD ou MODIFY peuvent changer le statut"
                                        )
                                    );
                                }
                                // Appliquer le changement
                                project.setStatus(com.senprojectbackend1.domain.enumeration.ProjectStatus.valueOf(newStatus));
                                return projectRepository.save(project).map(projectMapper::toDto);
                            })
                    );
            });
    }

    @Override
    public Mono<ProjectDTO> submitProject(ProjectSubmissionDTO submissionDTO, String userLogin) {
        if (submissionDTO.getTitle() == null || submissionDTO.getTitle().trim().length() < 3) {
            return Mono.error(
                new BadRequestAlertException("Le titre du projet doit contenir au moins 3 caractères", "project", "titleinvalid")
            );
        }

        // Vérification de l'existence d'un projet avec le même titre
        return projectRepository
            .findByTitle(submissionDTO.getTitle().trim())
            .collectList()
            .flatMap(existingProjects -> {
                boolean titleExists = existingProjects
                    .stream()
                    .anyMatch(p -> submissionDTO.getId() == null || !submissionDTO.getId().equals(p.getId()));

                if (titleExists) {
                    return Mono.error(new BadRequestAlertException("Un projet avec ce titre existe déjà", "project", "titleexists"));
                }

                boolean isUpdate = submissionDTO.getId() != null;
                Mono<Project> projectMono;
                if (isUpdate) {
                    projectMono = projectRepository
                        .findById(submissionDTO.getId())
                        .switchIfEmpty(Mono.error(new BadRequestAlertException("Projet non trouvé", "project", "notfound")))
                        .flatMap(existingProject -> {
                            if (existingProject.getTeamId() == null) {
                                return Mono.error(new BadRequestAlertException("Projet sans équipe", "project", "noteam"));
                            }
                            return userProfileRepository
                                .findOneByLogin(userLogin)
                                .switchIfEmpty(
                                    Mono.error(new BadRequestAlertException("Profil utilisateur non trouvé", "project", "usernotfound"))
                                )
                                .flatMap(userProfile ->
                                    teamMembershipRepository
                                        .findByTeamIdAndUserId(existingProject.getTeamId(), userProfile.getId())
                                        .switchIfEmpty(
                                            Mono.error(
                                                new BadRequestAlertException(
                                                    "Vous n'êtes pas membre de l'équipe du projet",
                                                    "project",
                                                    "notmember"
                                                )
                                            )
                                        )
                                        .flatMap(membership -> {
                                            if (!"ACCEPTED".equals(membership.getStatus())) {
                                                return Mono.error(
                                                    new BadRequestAlertException(
                                                        "Votre statut d'équipe n'est pas accepté",
                                                        "project",
                                                        "notaccepted"
                                                    )
                                                );
                                            }
                                            if (!"LEAD".equals(membership.getRole()) && !"MODIFY".equals(membership.getRole())) {
                                                return Mono.error(
                                                    new BadRequestAlertException(
                                                        "Seuls les membres LEAD ou MODIFY peuvent modifier le projet",
                                                        "project",
                                                        "noright"
                                                    )
                                                );
                                            }
                                            existingProject
                                                .title(submissionDTO.getTitle())
                                                .description(submissionDTO.getDescription())
                                                .showcase(submissionDTO.getShowcase())
                                                .type(submissionDTO.getType())
                                                .openToCollaboration(submissionDTO.isOpenToCollaboration())
                                                .openToFunding(submissionDTO.isOpenToFunding())
                                                .updatedAt(java.time.Instant.now())
                                                .lastUpdatedBy(userLogin);
                                            return enrichProjectWithAssociations(existingProject, submissionDTO);
                                        })
                                );
                        });
                } else {
                    Project project = new Project()
                        .title(submissionDTO.getTitle())
                        .description(submissionDTO.getDescription())
                        .showcase(submissionDTO.getShowcase())
                        .status(com.senprojectbackend1.domain.enumeration.ProjectStatus.WAITING_VALIDATION)
                        .createdAt(java.time.Instant.now())
                        .updatedAt(java.time.Instant.now())
                        .openToCollaboration(submissionDTO.isOpenToCollaboration())
                        .openToFunding(submissionDTO.isOpenToFunding())
                        .type(submissionDTO.getType())
                        .totalLikes(0)
                        .totalShares(0)
                        .totalViews(0)
                        .totalComments(0)
                        .totalFavorites(0)
                        .isDeleted(false)
                        .createdBy(userLogin)
                        .lastUpdatedBy(userLogin);
                    projectMono = enrichProjectWithAssociations(project, submissionDTO);
                }
                return projectMono
                    .flatMap(projectRepository::save)
                    .flatMap(savedProject -> processAllSections(savedProject, submissionDTO).thenReturn(savedProject))
                    .flatMap(savedProject -> notifyTeamMembers(savedProject, userLogin, isUpdate).thenReturn(savedProject))
                    .map(projectMapper::toDto);
            });
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PageDTO<ProjectDTO>> getPaginatedProjects(int page, int size, String category) {
        LOG.debug("Request to get paginated Projects, page: {}, size: {}, category: {}", page, size, category);

        Mono<java.util.List<ProjectDTO>> projectDTOs;
        Mono<Long> totalCount;

        if (category != null && !category.isEmpty()) {
            // Filtrer par catégorie (tag)
            // Utiliser les limites manuelles pour R2DBC au lieu de Pageable
            int offset = page * size;
            projectDTOs = projectRepository
                .findByTagName(category, size, offset)
                .flatMap(project -> {
                    // Chargement eager des tags pour chaque projet
                    Mono<Project> projectWithTags = projectRepository
                        .findTagsByProjectId(project.getId())
                        .collectList()
                        .map(tags -> {
                            project.setTags(new HashSet<>(tags));
                            return project;
                        });

                    // Chargement des informations de l'équipe si présente
                    if (project.getTeamId() != null) {
                        return projectWithTags.flatMap(
                            p ->
                                teamRepository
                                    .findById(p.getTeamId())
                                    .map(team -> {
                                        p.setTeam(team);
                                        return p;
                                    })
                                    .defaultIfEmpty(p) // Retourne le projet sans équipe si l'équipe n'est pas trouvée
                        );
                    } else {
                        return projectWithTags;
                    }
                })
                .map(projectMapper::toDto)
                .collectList();
            // Récupérer le nombre total de projets avec ce tag
            totalCount = projectRepository.countByTagName(category);
        } else {
            // Utiliser la méthode qui charge déjà toutes les relations
            Pageable pageable = PageRequest.of(page, size);

            // Si la méthode findAllWithEagerRelationships charge déjà l'équipe

            projectDTOs = projectRepository
                .findAllWithEagerRelationships(pageable)
                .flatMap(project -> {
                    if (project.getTeamId() != null) {
                        return teamRepository
                            .findById(project.getTeamId())
                            .map(team -> {
                                project.setTeam(team);
                                return project;
                            })
                            .defaultIfEmpty(project);
                    } else {
                        return Mono.just(project);
                    }
                })
                .map(projectMapper::toDto)
                .collectList();
            // Récupérer le nombre total de projets
            totalCount = projectRepository.count();
        }

        // Combiner les résultats pour créer la réponse paginée
        return Mono.zip(projectDTOs, totalCount).map(tuple -> {
            java.util.List<ProjectDTO> content = tuple.getT1();
            long total = tuple.getT2();
            int totalPages = (int) Math.ceil((double) total / size);

            return new PageDTO<>(content != null ? content : new ArrayList<>(), total, totalPages, page, size);
        });
    }

    // Implémentation de la méthode existante pour maintenir la compatibilité
    @Override
    @Transactional(readOnly = true)
    public Mono<PageDTO<ProjectDTO>> getPaginatedProjects(int page, int size) {
        // Appel à la nouvelle méthode avec category = null
        return getPaginatedProjects(page, size, null);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectDTO> getTop10PopularProjects() {
        LOG.debug("Request to get top 10 popular projects");
        return projectRepository
            .findTop10PopularProjects()
            .map(projectMapper::toDto)
            .doOnNext(project -> LOG.debug("Found popular project: {}", project.getTitle()));
    }

    private Mono<Project> enrichProjectWithAssociations(Project project, ProjectSubmissionDTO dto) {
        Mono<Project> projectMono = Mono.just(project);
        if (dto.getTeamId() != null) {
            projectMono = projectMono.flatMap(p -> teamRepository.findById(dto.getTeamId()).map(p::team).defaultIfEmpty(p));
        }
        if (dto.getTagIds() != null && !dto.getTagIds().isEmpty()) {
            projectMono = projectMono.flatMap(p ->
                Flux.fromIterable(dto.getTagIds())
                    .flatMap(tagRepository::findById)
                    .collectList()
                    .map(tags -> {
                        tags.forEach(p::addTags);
                        return p;
                    })
            );
        }
        return projectMono;
    }

    private Mono<Void> processAllSections(Project project, ProjectSubmissionDTO dto) {
        return projectSectionRepository
            .deleteByProjectId(project.getId())
            .thenMany(saveSections(project, dto.getSections()))
            .then(projectGalleryRepository.deleteByProjectId(project.getId()))
            .thenMany(saveGalleryImages(project, dto.getGalleryImages()))
            .then(externalLinkRepository.deleteByProjectId(project.getId()))
            .thenMany(saveExternalLinks(project, dto.getExternalLinks()))
            .then();
    }

    private Flux<ProjectSection> saveSections(Project project, java.util.List<ProjectSubmissionDTO.SectionDTO> sections) {
        if (sections == null || sections.isEmpty()) return Flux.empty();
        return Flux.fromIterable(sections)
            .index()
            .flatMap(tuple -> {
                int order = tuple.getT1().intValue();
                ProjectSubmissionDTO.SectionDTO sectionDTO = tuple.getT2();
                ProjectSection section = new ProjectSection()
                    .title(sectionDTO.getTitle())
                    .content(sectionDTO.getContent())
                    .mediaUrl(sectionDTO.getMediaUrl())
                    .order(order)
                    .project(project);
                return projectSectionRepository.save(section);
            });
    }

    private Flux<ProjectGallery> saveGalleryImages(Project project, java.util.List<ProjectSubmissionDTO.GalleryImageDTO> images) {
        if (images == null || images.isEmpty()) return Flux.empty();
        return Flux.fromIterable(images)
            .index()
            .flatMap(tuple -> {
                int order = tuple.getT1().intValue();
                ProjectSubmissionDTO.GalleryImageDTO imageDTO = tuple.getT2();
                ProjectGallery galleryImage = new ProjectGallery()
                    .imageUrl(imageDTO.getImageUrl())
                    .description(imageDTO.getDescription())
                    .order(order)
                    .project(project);
                return projectGalleryRepository.save(galleryImage);
            });
    }

    private Flux<ExternalLink> saveExternalLinks(Project project, java.util.List<ProjectSubmissionDTO.ExternalLinkDTO> links) {
        if (links == null || links.isEmpty()) return Flux.empty();
        return Flux.fromIterable(links).flatMap(linkDTO -> {
            ExternalLink link = new ExternalLink().title(linkDTO.getTitle()).url(linkDTO.getUrl()).type(linkDTO.getType()).project(project);
            return externalLinkRepository.save(link);
        });
    }

    private Mono<Void> notifyTeamMembers(Project project, String userLogin, boolean isUpdate) {
        return teamMembershipRepository
            .findAll()
            .filter(m -> m.getTeamId().equals(project.getTeamId()))
            .filter(m -> "ACCEPTED".equals(m.getStatus()))
            .filter(m -> !m.getMembersId().equals(userLogin))
            .flatMap(m ->
                userProfileRepository
                    .findById(m.getMembersId())
                    .flatMap(user ->
                        notificationService.createNotification(
                            user.getId(),
                            (isUpdate ? "Le projet '" : "Un nouveau projet '") +
                            project.getTitle() +
                            (isUpdate ? "' a été modifié." : "' a été créé."),
                            com.senprojectbackend1.domain.enumeration.NotificationType.PROJECT_UPDATED,
                            project.getId().toString()
                        )
                    )
            )
            .then();
    }
}
