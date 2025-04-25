package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.domain.enumeration.CommentStatus;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.domain.enumeration.ProjectStatus;
import com.senprojectbackend1.repository.*;
import com.senprojectbackend1.security.SecurityUtils;
import com.senprojectbackend1.service.CommentService;
import com.senprojectbackend1.service.NotificationService;
import com.senprojectbackend1.service.ProjectService;
import com.senprojectbackend1.service.TagService;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.CommentDTO;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.ProjectSimpleDTO;
import com.senprojectbackend1.service.dto.TagDTO;
import com.senprojectbackend1.service.mapper.ProjectMapper;
import com.senprojectbackend1.service.mapper.ProjectSectionMapper;
import com.senprojectbackend1.service.mapper.ProjectSimpleMapper;
import com.senprojectbackend1.service.mapper.UserProfileMapper;
import java.time.Instant;
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
    private final CommentService commentService;
    private final UserProfileMapper userProfileMapper;
    private final UserProfileService userProfileService;
    private final EngagementProjectRepository engagementProjectRepository;
    private final TagService tagService;
    private final TeamRepository teamRepository;

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
        CommentService commentService,
        UserProfileMapper userProfileMapper,
        UserProfileService userProfileService,
        EngagementProjectRepository engagementProjectRepository,
        TagService tagService,
        TeamRepository teamRepository) {
        this.projectRepository = projectRepository;
        this.projectSectionRepository = projectSectionRepository;
        this.projectMapper = projectMapper;
        this.projectSectionMapper = projectSectionMapper;
        this.projectSimpleMapper = projectSimpleMapper;
        this.tagRepository = tagRepository;
        this.userProfileRepository = userProfileRepository;
        this.notificationService = notificationService;
        this.teamMembershipRepository = teamMembershipRepository;
        this.commentService = commentService;
        this.userProfileMapper = userProfileMapper;
        this.userProfileService = userProfileService;
        this.engagementProjectRepository = engagementProjectRepository;
        this.tagService = tagService;
        this.teamRepository = teamRepository;
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
                            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of the team")))
                            .flatMap(status -> {
                                if (!"ACCEPTED".equals(status)) {
                                    return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an accepted member of the team"));
                                }
                                return projectRepository.updateProjectStatusToDeleted(id);
                            });
                    });
            });
    }
}
