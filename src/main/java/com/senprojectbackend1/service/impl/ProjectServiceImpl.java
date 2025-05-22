package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.*;
import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.repository.*;
import com.senprojectbackend1.security.SecurityUtils;
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
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link Project}.
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
    private final com.senprojectbackend1.service.CloudinaryService cloudinaryService;

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
        ProjectGalleryRepository projectGalleryRepository,
        com.senprojectbackend1.service.CloudinaryService cloudinaryService
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
        this.cloudinaryService = cloudinaryService;
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
        return projectRepository
            .findById(id)
            .flatMap(project -> {
                if (project.getTeam() == null || project.getTeam().getId() == null) {
                    return Mono.error(new BadRequestAlertException("Projet sans équipe", "project", "no team"));
                }
                // Vérification des droits : LEAD ou MODIFY
                return checkUserIsAcceptedLeadOrModify(project.getTeam().getId(), project.getLastUpdatedBy())
                    .then(delete(id))
                    .then(notifyTeamOnDelete(project, project.getLastUpdatedBy()));
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
                    .flatMap(existingEngagement ->
                        // Si l'engagement existe, retourner le projet sans créer un nouvel engagement
                        findOneWithSections(id)
                    )
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
                                .then(findOneWithSections(id)); // Retourner le projet après les opérations
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
                        if (Boolean.TRUE.equals(isFavorite)) {
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
            .flatMap(userProfile ->
                projectRepository
                    .findById(id)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")))
                    .flatMap(project -> {
                        if (project.getTeam() == null) {
                            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Project has no team"));
                        }
                        // Vérification des droits : LEAD ou MODIFY
                        return checkUserHasRole(project.getTeam().getId(), userLogin, Set.of("LEAD", "MODIFY")).then(
                            projectRepository.updateProjectStatusToDeleted(id)
                        );
                    })
            );
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
                if ("PUBLISHED".equalsIgnoreCase(newStatus)) {
                    // Seuls les ADMIN ou SUPPORT peuvent publier
                    return SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN", "ROLE_SUPPORT").flatMap(isAdminOrSupport -> {
                        if (Boolean.FALSE.equals(isAdminOrSupport)) {
                            return Mono.error(
                                new ResponseStatusException(HttpStatus.FORBIDDEN, "Seuls les admins/support peuvent publier")
                            );
                        }
                        project.setStatus(ProjectStatus.valueOf(newStatus));
                        return projectRepository.updateStatus(project.getId(), newStatus).map(projectMapper::toDto);
                    });
                } else {
                    // Seuls les LEAD ou MODIFY peuvent changer le statut
                    return checkUserHasRole(project.getTeamId(), userLogin, Set.of("LEAD", "MODIFY")).then(
                        Mono.defer(() -> {
                            project.setStatus(ProjectStatus.valueOf(newStatus));
                            return projectRepository.updateStatus(project.getId(), newStatus).map(projectMapper::toDto);
                        })
                    );
                }
            });
    }

    @Override
    public Mono<ProjectDTO> submitProject(ProjectSubmissionDTO dto, String userLogin) {
        return createOrUpdateProject(dto, userLogin, false);
    }

    @Override
    public Mono<ProjectDTO> updateSubmittedProject(ProjectSubmissionDTO dto, String userLogin) {
        return createOrUpdateProject(dto, userLogin, true);
    }

    private Mono<ProjectDTO> createOrUpdateProject(ProjectSubmissionDTO dto, String userLogin, boolean isUpdate) {
        if (dto.getTitle() == null || dto.getTitle().trim().length() < 3) {
            return Mono.error(
                new BadRequestAlertException("Le titre du projet doit contenir au moins 3 caractères", "project", "titleinvalid")
            );
        }
        if (!isUpdate) {
            // Création : teamId obligatoire et doit exister
            if (dto.getTeamId() == null) {
                return Mono.error(
                    new BadRequestAlertException("Une équipe valide doit être renseignée pour le projet", "project", "noteam")
                );
            }
            // Interdire la création directe en PUBLISHED sauf pour ADMIN/SUPPORT
            if (dto.getStatus() != null && "PUBLISHED".equalsIgnoreCase(dto.getStatus().toString())) {
                return SecurityUtils.hasCurrentUserAnyOfAuthorities("ROLE_ADMIN", "ROLE_SUPPORT").flatMap(isAdminOrSupport -> {
                    if (Boolean.FALSE.equals(isAdminOrSupport)) {
                        return Mono.error(
                            new BadRequestAlertException(
                                "Seuls les admins/support peuvent publier un projet directement",
                                "project",
                                "forbidden"
                            )
                        );
                    }
                    // On laisse continuer la création (status PUBLISHED autorisé)
                    return teamRepository
                        .findById(dto.getTeamId())
                        .switchIfEmpty(Mono.error(new BadRequestAlertException("L'équipe spécifiée n'existe pas", "project", "noteam")))
                        .flatMap(team -> {
                            Project project = new Project()
                                .title(dto.getTitle())
                                .description(dto.getDescription())
                                .showcase(dto.getShowcase())
                                .status(ProjectStatus.PUBLISHED)
                                .createdAt(Instant.now())
                                .updatedAt(Instant.now())
                                .openToCollaboration(dto.isOpenToCollaboration())
                                .openToFunding(dto.isOpenToFunding())
                                .type(dto.getType())
                                .totalLikes(0)
                                .totalShares(0)
                                .totalViews(0)
                                .totalComments(0)
                                .totalFavorites(0)
                                .isDeleted(false)
                                .createdBy(userLogin)
                                .lastUpdatedBy(userLogin)
                                .team(team);
                            return enrichProjectWithAssociations(project, dto)
                                .flatMap(projectRepository::save)
                                .flatMap(savedProject -> processAllSections(savedProject, dto).thenReturn(savedProject))
                                .flatMap(savedProject -> notifyTeamOnCreate(savedProject, userLogin).thenReturn(savedProject))
                                .map(projectMapper::toDto);
                        });
                });
            }
            // Vérifier que l'équipe existe (cas normal, status != PUBLISHED)
            return teamRepository
                .findById(dto.getTeamId())
                .switchIfEmpty(Mono.error(new BadRequestAlertException("L'équipe spécifiée n'existe pas", "project", "noteam")))
                .flatMap(team -> {
                    Project project = new Project()
                        .title(dto.getTitle())
                        .description(dto.getDescription())
                        .showcase(dto.getShowcase())
                        .status(ProjectStatus.WAITING_VALIDATION)
                        .createdAt(Instant.now())
                        .updatedAt(Instant.now())
                        .openToCollaboration(dto.isOpenToCollaboration())
                        .openToFunding(dto.isOpenToFunding())
                        .type(dto.getType())
                        .totalLikes(0)
                        .totalShares(0)
                        .totalViews(0)
                        .totalComments(0)
                        .totalFavorites(0)
                        .isDeleted(false)
                        .createdBy(userLogin)
                        .lastUpdatedBy(userLogin)
                        .team(team);
                    return enrichProjectWithAssociations(project, dto)
                        .flatMap(projectRepository::save)
                        .flatMap(savedProject -> processAllSections(savedProject, dto).thenReturn(savedProject))
                        .flatMap(savedProject -> notifyTeamOnCreate(savedProject, userLogin).thenReturn(savedProject))
                        .map(projectMapper::toDto);
                });
        }
        // Mise à jour
        if (dto.getId() == null) {
            return Mono.error(new BadRequestAlertException("ID du projet manquant pour la mise à jour", "project", "idmissing"));
        }
        return projectRepository
            .findById(dto.getId())
            .switchIfEmpty(Mono.error(new BadRequestAlertException("Projet non trouvé", "project", "notfound")))
            .flatMap(existingProject -> {
                if (existingProject.getTeamId() == null) {
                    return Mono.error(new BadRequestAlertException("Projet sans équipe", "project", "noteam"));
                }
                return checkUserHasRole(existingProject.getTeamId(), userLogin, Set.of("LEAD", "MODIFY")).flatMap(membership -> {
                    // Empêcher la modification de l'équipe sauf si LEAD
                    if (dto.getTeamId() != null && !dto.getTeamId().equals(existingProject.getTeamId())) {
                        if (!"LEAD".equals(membership.getRole())) {
                            return Mono.error(
                                new BadRequestAlertException("Seul un LEAD peut changer l'équipe du projet", "project", "noright")
                            );
                        }
                        // Vérifier que la nouvelle équipe existe
                        return teamRepository
                            .findById(dto.getTeamId())
                            .switchIfEmpty(Mono.error(new BadRequestAlertException("L'équipe spécifiée n'existe pas", "project", "noteam")))
                            .flatMap(newTeam -> {
                                existingProject.team(newTeam);
                                return updateProjectFieldsAndNotify(existingProject, dto, userLogin);
                            });
                    }
                    return updateProjectFieldsAndNotify(existingProject, dto, userLogin);
                });
            })
            .map(projectMapper::toDto);
    }

    // Met à jour les champs du projet, les associations, les sections, et notifie l'équipe
    private Mono<Project> updateProjectFieldsAndNotify(Project existingProject, ProjectSubmissionDTO dto, String userLogin) {
        existingProject
            .title(dto.getTitle())
            .description(dto.getDescription())
            .showcase(dto.getShowcase())
            .type(dto.getType())
            .openToCollaboration(dto.isOpenToCollaboration())
            .openToFunding(dto.isOpenToFunding())
            .updatedAt(Instant.now())
            .lastUpdatedBy(userLogin);
        return enrichProjectWithAssociations(existingProject, dto)
            .flatMap(projectRepository::save)
            .flatMap(savedProject -> processAllSections(savedProject, dto).thenReturn(savedProject))
            .flatMap(savedProject -> {
                if (true) {
                    return notifyTeamOnUpdate(savedProject, userLogin).thenReturn(savedProject);
                } else {
                    return Mono.just(savedProject);
                }
            });
    }

    // Notifie toute l'équipe que le projet a été mis à jour par userLogin
    private Mono<Void> notifyTeamOnUpdate(Project project, String userLogin) {
        return teamMembershipRepository
            .findAll()
            .filter(m -> m.getTeamId().equals(project.getTeamId()))
            .filter(m -> "ACCEPTED".equals(m.getStatus()))
            .flatMap(m ->
                userProfileRepository
                    .findById(m.getMembersId())
                    .flatMap(user ->
                        notificationService.createNotification(
                            user.getId(),
                            "Le projet '" + project.getTitle() + "' a été mis à jour par " + userLogin,
                            NotificationType.PROJECT_UPDATED,
                            project.getId().toString()
                        )
                    )
            )
            .then();
    }

    private Mono<TeamMembership> checkUserHasRole(Long teamId, String userLogin, Set<String> rolesAcceptes) {
        return userProfileRepository
            .findOneByLogin(userLogin)
            .switchIfEmpty(Mono.error(new BadRequestAlertException("Profil utilisateur non trouvé", "project", "usernotfound")))
            .flatMap(userProfile ->
                teamMembershipRepository
                    .findByTeamIdAndUserId(teamId, userProfile.getId())
                    .switchIfEmpty(
                        Mono.error(new BadRequestAlertException("Vous n'êtes pas membre de l'équipe du projet", "project", "notmember"))
                    )
                    .flatMap(membership -> {
                        if (!"ACCEPTED".equals(membership.getStatus())) {
                            return Mono.error(
                                new BadRequestAlertException("Votre statut d'équipe n'est pas accepté", "project", "notaccepted")
                            );
                        }
                        if (!rolesAcceptes.contains(membership.getRole())) {
                            return Mono.error(
                                new BadRequestAlertException("Vous n'avez pas le rôle requis pour cette action", "project", "noright")
                            );
                        }
                        return Mono.just(membership);
                    })
            );
    }

    private Mono<TeamMembership> checkUserIsAcceptedLeadOrModify(Long teamId, String userLogin) {
        return checkUserHasRole(teamId, userLogin, Set.of("LEAD", "MODIFY"));
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectDTO> getPaginatedProjects(Pageable pageable, List<String> categories) {
        LOG.debug("Request to get paginated Projects - pageable: {}, categories: {}", pageable, categories);
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int offset = page * size;
        Flux<Project> projectFlux;
        // Always create criteria to filter by PUBLISHED status and isDeleted=false
        ProjectCriteria criteria = new ProjectCriteria();
        ProjectCriteria.ProjectStatusFilter statusFilter = new ProjectCriteria.ProjectStatusFilter();
        statusFilter.setEquals(ProjectStatus.PUBLISHED);
        criteria.setStatus(statusFilter);
        if (categories == null || categories.isEmpty()) {
            return projectRepository.findByCriteria(criteria, pageable).map(projectMapper::toDto);
        } else {
            // Union des projets pour toutes les catégories, sans doublons
            projectFlux = Flux.fromIterable(categories)
                .flatMap(cat -> projectRepository.findByTagName(cat, Integer.MAX_VALUE, 0)) // Récupérer potentiellement plus pour paginer après filtre
                .distinct(Project::getId);
        }

        return projectFlux
            .filter(p -> p.getStatus() != null && p.getStatus().equals(ProjectStatus.PUBLISHED) && Boolean.FALSE.equals(p.getIsDeleted())) // <<< Ajouter ce filtre
            .sort((p1, p2) -> p1.getCreatedAt().compareTo(p2.getCreatedAt())) // Optionnel: tri par date de création par défaut
            .skip(offset)
            .take(size)
            .flatMap(project ->
                projectRepository
                    .findTagsByProjectId(project.getId())
                    .collectList()
                    .flatMap(tags -> {
                        project.setTags(new java.util.HashSet<>(tags));
                        if (project.getTeamId() != null) {
                            return teamRepository
                                .findById(project.getTeamId())
                                .map(team -> {
                                    project.setTeam(team);
                                    return project;
                                })
                                .defaultIfEmpty(project);
                        } else {
                            return reactor.core.publisher.Mono.just(project);
                        }
                    })
            )
            .map(projectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectDTO> getTopPopularProjects(Pageable pageable) {
        LOG.debug("Request to get top popular projects - pageable: {}", pageable);
        int page = pageable.getPageNumber();
        int size = pageable.getPageSize();
        int offset = page * size;
        return projectRepository
            .findAll()
            .filter(p -> p.getStatus() != null && p.getStatus().name().equals("PUBLISHED") && Boolean.FALSE.equals(p.getIsDeleted()))
            .sort((p1, p2) ->
                Integer.compare(
                    (safeInt(p2.getTotalViews()) + safeInt(p2.getTotalLikes()) + safeInt(p2.getTotalFavorites())),
                    (safeInt(p1.getTotalViews()) + safeInt(p1.getTotalLikes()) + safeInt(p1.getTotalFavorites()))
                )
            )
            .skip(offset)
            .take(size)
            .flatMap(project ->
                projectRepository
                    .findTagsByProjectId(project.getId())
                    .collectList()
                    .flatMap(tags -> {
                        project.setTags(new java.util.HashSet<>(tags));
                        if (project.getTeamId() != null) {
                            return teamRepository
                                .findById(project.getTeamId())
                                .map(team -> {
                                    project.setTeam(team);
                                    return project;
                                })
                                .defaultIfEmpty(project);
                        } else {
                            return reactor.core.publisher.Mono.just(project);
                        }
                    })
            )
            .map(projectMapper::toDto);
    }

    private int safeInt(Integer value) {
        return value != null ? value : 0;
    }

    private Mono<Project> enrichProjectWithAssociations(Project project, ProjectSubmissionDTO dto) {
        Mono<Project> projectMono = Mono.just(project);
        if (dto.getTeamId() != null) {
            projectMono = projectMono.flatMap(p -> teamRepository.findById(dto.getTeamId()).map(p::team).defaultIfEmpty(p));
        }
        if (dto.getTags() != null && !dto.getTags().isEmpty()) {
            projectMono = projectMono.flatMap(p ->
                Flux.fromIterable(dto.getTags())
                    .flatMap(tagInput ->
                        tagRepository
                            .findAll()
                            .filter(existing -> existing.getName().equalsIgnoreCase(tagInput.getName()))
                            .next()
                            .switchIfEmpty(
                                tagRepository.save(new Tag().name(tagInput.getName()).color(tagInput.getColor()).isForbidden(false))
                            )
                    )
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

    private Flux<ProjectSection> saveSections(Project project, List<ProjectSubmissionDTO.SectionDTO> sections) {
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

    private Flux<ProjectGallery> saveGalleryImages(Project project, List<ProjectSubmissionDTO.GalleryImageDTO> images) {
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

    private Flux<ExternalLink> saveExternalLinks(Project project, List<ProjectSubmissionDTO.ExternalLinkDTO> links) {
        if (links == null || links.isEmpty()) return Flux.empty();
        return Flux.fromIterable(links).flatMap(linkDTO -> {
            ExternalLink link = new ExternalLink().title(linkDTO.getTitle()).url(linkDTO.getUrl()).type(linkDTO.getType()).project(project);
            return externalLinkRepository.save(link);
        });
    }

    private Mono<Void> notifyTeamOnCreate(Project project, String userLogin) {
        String msg = "Le projet '" + project.getTitle() + "' a été créé par " + userLogin;
        return sendTeamNotification(project, NotificationType.PROJECT_UPDATED, msg, userLogin);
    }

    private Mono<Void> notifyTeamOnDelete(Project project, String userLogin) {
        String msg = "Le projet '" + project.getTitle() + "' a été supprimé par " + userLogin;
        return sendTeamNotification(project, NotificationType.PROJECT_DELETED, msg, userLogin);
    }

    // --- Début méthodes utilitaires pour le traitement des images ---
    @Override
    public Mono<ProjectSubmissionDTO> processGalleryImages(ProjectSubmissionDTO projectData, String userLogin) {
        if (projectData.getGalleryImages() == null || projectData.getGalleryImages().isEmpty()) {
            return Mono.just(projectData);
        }
        return Flux.fromIterable(projectData.getGalleryImages())
            .flatMap(imageDTO -> {
                try {
                    String rawData = imageDTO.getImageUrl();
                    if (rawData == null || rawData.isBlank() || rawData.startsWith("http")) {
                        return Mono.just(imageDTO); // Déjà une URL ou pas d'image
                    }
                    return cloudinaryService
                        .uploadBase64Image(rawData, "gallery")
                        .map(url -> {
                            imageDTO.setImageUrl(url);
                            return imageDTO;
                        })
                        .onErrorResume(e -> {
                            imageDTO.setImageUrl("");
                            return Mono.just(imageDTO);
                        });
                } catch (Exception e) {
                    return Mono.error(new BadRequestAlertException("Fichier image invalide", "project", "invalidimage"));
                }
            })
            .collectList()
            .map(updatedGallery -> {
                projectData.setGalleryImages(updatedGallery);
                return projectData;
            });
    }

    @Override
    public Mono<ProjectSubmissionDTO> processShowcaseImage(ProjectSubmissionDTO projectData, String userLogin) {
        String rawData = projectData.getShowcase();
        if (rawData == null || rawData.isBlank() || rawData.startsWith("http")) {
            return Mono.just(projectData); // Déjà une URL ou pas d'image
        }
        return cloudinaryService
            .uploadBase64Image(rawData, "showcase")
            .map(showcaseUrl -> {
                projectData.setShowcase(showcaseUrl);
                return projectData;
            })
            .onErrorResume(e -> {
                projectData.setShowcase("");
                return Mono.just(projectData);
            });
    }

    @Override
    public Mono<ProjectSubmissionDTO> processSectionImages(ProjectSubmissionDTO projectData, String userLogin) {
        if (projectData.getSections() == null || projectData.getSections().isEmpty()) {
            return Mono.just(projectData);
        }
        return Flux.fromIterable(projectData.getSections())
            .flatMap(section -> {
                String mediaUrl = section.getMediaUrl();
                if (mediaUrl == null || mediaUrl.isBlank() || mediaUrl.startsWith("http")) {
                    return Mono.just(section); // Déjà une URL ou pas d'image
                }
                return cloudinaryService
                    .uploadBase64Image(mediaUrl, "section")
                    .map(uploadedUrl -> {
                        section.setMediaUrl(uploadedUrl);
                        return section;
                    })
                    .onErrorResume(e -> {
                        section.setMediaUrl("");
                        return Mono.just(section);
                    });
            })
            .collectList()
            .map(updatedSections -> {
                projectData.setSections(updatedSections);
                return projectData;
            });
    }

    @Override
    public Mono<ProjectSubmissionDTO> processAllImages(ProjectSubmissionDTO projectData, String userLogin) {
        return processGalleryImages(projectData, userLogin)
            .flatMap(withGallery -> processShowcaseImage(withGallery, userLogin))
            .flatMap(withShowcase -> processSectionImages(withShowcase, userLogin));
    }

    // Utilitaire DRY pour notifier toute l'équipe d'un projet
    private Mono<Void> sendTeamNotification(Project project, NotificationType type, String message, String userLogin) {
        return teamMembershipRepository
            .findAll()
            .filter(m -> m.getTeamId().equals(project.getTeamId()))
            .filter(m -> "ACCEPTED".equals(m.getStatus()))
            .flatMap(m ->
                userProfileRepository
                    .findById(m.getMembersId())
                    .flatMap(user ->
                        notificationService.createNotification(user.getId(), message, type, project.getId().toString(), userLogin)
                    )
            )
            .then();
    }

    @Override
    public Mono<Long> countAllProjects() {
        return projectRepository.count();
    }

    @Override
    public Mono<Long> countProjectsByCategories(List<String> categories) {
        if (categories == null || categories.isEmpty()) {
            return projectRepository.count();
        }
        // Union : compter les projets distincts ayant au moins un des tags
        return Flux.fromIterable(categories)
            .flatMap(cat -> projectRepository.findByTagName(cat, Integer.MAX_VALUE, 0))
            .distinct(Project::getId)
            .count();
    }

    @Override
    public Mono<Long> countPopularProjects() {
        // Projets PUBLISHED et non supprimés
        return projectRepository
            .findAll()
            .filter(p -> p.getStatus() != null && p.getStatus().name().equals("PUBLISHED") && Boolean.FALSE.equals(p.getIsDeleted()))
            .count();
    }
}
