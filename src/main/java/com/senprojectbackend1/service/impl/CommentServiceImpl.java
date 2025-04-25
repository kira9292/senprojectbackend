package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.Comment;
import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.criteria.CommentCriteria;
import com.senprojectbackend1.domain.enumeration.CommentStatus;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.repository.CommentRepository;
import com.senprojectbackend1.repository.EngagementProjectRepository;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.repository.TeamMembershipRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.CommentService;
import com.senprojectbackend1.service.NotificationService;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.CommentDTO;
import com.senprojectbackend1.service.dto.CommentSimpleDTO;
import com.senprojectbackend1.service.dto.ProjectDTO;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import com.senprojectbackend1.service.dto.UserProfileSimpleDTO;
import com.senprojectbackend1.service.mapper.CommentMapper;
import com.senprojectbackend1.service.mapper.ProjectMapper;
import com.senprojectbackend1.service.mapper.UserProfileMapper;
import java.time.Instant;
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
 * Service Implementation for managing {@link com.senprojectbackend1.domain.Comment}.
 */
@Service
@Transactional
public class CommentServiceImpl implements CommentService {

    private static final Logger LOG = LoggerFactory.getLogger(CommentServiceImpl.class);

    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;
    private final UserProfileRepository userProfileRepository;
    private final ProjectRepository projectRepository;
    private final EngagementProjectRepository engagementProjectRepository;
    private final TeamMembershipRepository teamMembershipRepository;
    private final NotificationService notificationService;
    private final ProjectMapper projectMapper;
    private final UserProfileMapper userProfileMapper;

    public CommentServiceImpl(
        CommentRepository commentRepository,
        CommentMapper commentMapper,
        UserProfileRepository userProfileRepository,
        ProjectRepository projectRepository,
        EngagementProjectRepository engagementProjectRepository,
        TeamMembershipRepository teamMembershipRepository,
        NotificationService notificationService,
        ProjectMapper projectMapper,
        UserProfileMapper userProfileMapper
    ) {
        this.commentRepository = commentRepository;
        this.commentMapper = commentMapper;
        this.userProfileRepository = userProfileRepository;
        this.projectRepository = projectRepository;
        this.engagementProjectRepository = engagementProjectRepository;
        this.teamMembershipRepository = teamMembershipRepository;
        this.notificationService = notificationService;
        this.projectMapper = projectMapper;
        this.userProfileMapper = userProfileMapper;
    }

    @Override
    public Mono<CommentDTO> save(CommentDTO commentDTO) {
        LOG.debug("Request to save Comment : {}", commentDTO);
        return commentRepository.save(commentMapper.toEntity(commentDTO)).map(commentMapper::toDto);
    }

    @Override
    public Mono<CommentDTO> update(CommentDTO commentDTO) {
        LOG.debug("Request to update Comment : {}", commentDTO);
        return commentRepository.save(commentMapper.toEntity(commentDTO)).map(commentMapper::toDto);
    }

    @Override
    public Mono<CommentDTO> partialUpdate(CommentDTO commentDTO) {
        LOG.debug("Request to partially update Comment : {}", commentDTO);

        return commentRepository
            .findById(commentDTO.getId())
            .map(existingComment -> {
                commentMapper.partialUpdate(existingComment, commentDTO);

                return existingComment;
            })
            .flatMap(commentRepository::save)
            .map(commentMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<CommentDTO> findByCriteria(CommentCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Comments by Criteria");
        return commentRepository.findByCriteria(criteria, pageable).map(commentMapper::toDto);
    }

    /**
     * Find the count of comments by criteria.
     * @param criteria filtering criteria
     * @return the count of comments
     */
    public Mono<Long> countByCriteria(CommentCriteria criteria) {
        LOG.debug("Request to get the count of all Comments by Criteria");
        return commentRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return commentRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<CommentDTO> findOne(Long id) {
        LOG.debug("Request to get Comment : {}", id);
        return commentRepository.findById(id).map(commentMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Comment : {}", id);
        return commentRepository.deleteById(id);
    }

    @Override
    public Flux<CommentDTO> findByProjectId(Long projectId) {
        LOG.debug("Request to get all Comments by project id : {}", projectId);
        return commentRepository.findByProject(projectId).map(commentMapper::toDto);
    }

    @Override
    public Flux<CommentDTO> findByProject(Long id) {
        LOG.debug("Request to get all Comments by project id with user details: {}", id);
        return commentRepository
            .findByProject(id)
            .<CommentSimpleDTO>flatMap(comment -> {
                CommentSimpleDTO dto = new CommentSimpleDTO();
                dto.setId(comment.getId());
                dto.setContent(comment.getContent());
                dto.setCreatedAt(comment.getCreatedAt());
                dto.setUpdatedAt(comment.getUpdatedAt());
                dto.setStatus(comment.getStatus());
                if (comment.getProject() != null) {
                    dto.setProject(projectMapper.toDto(comment.getProject()));
                }

                if (comment.getUserId() != null) {
                    return userProfileRepository
                        .findById(comment.getUserId())
                        .map(userProfile -> {
                            dto.setUser(userProfileMapper.toSimpleDto(userProfile));
                            return dto;
                        })
                        .defaultIfEmpty(dto);
                }
                return Mono.just(dto);
            })
            .<CommentDTO>map(simpleDto -> {
                CommentDTO fullDto = new CommentDTO();
                fullDto.setId(simpleDto.getId());
                fullDto.setContent(simpleDto.getContent());
                fullDto.setCreatedAt(simpleDto.getCreatedAt());
                fullDto.setUpdatedAt(simpleDto.getUpdatedAt());
                fullDto.setStatus(simpleDto.getStatus());
                fullDto.setProject(simpleDto.getProject());

                if (simpleDto.getUser() != null) {
                    UserProfileDTO userDto = new UserProfileDTO();
                    userDto.setId(simpleDto.getUser().getId());
                    userDto.setLogin(simpleDto.getUser().getLogin());
                    userDto.setFirstName(simpleDto.getUser().getFirstName());
                    userDto.setLastName(simpleDto.getUser().getLastName());
                    fullDto.setUser(userDto);
                }
                return fullDto;
            });
    }

    @Override
    @Transactional
    public Mono<CommentDTO> createProjectComment(Long projectId, String content, String login) {
        LOG.debug("Service request to create Comment for Project : {}, content: {}, login: {}", projectId, content, login);

        return userProfileRepository
            .findOneByLogin(login)
            .flatMap(userProfile -> {
                if (userProfile == null) {
                    LOG.error("User profile not found for login: {}", login);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
                }

                return projectRepository
                    .findById(projectId)
                    .flatMap(project -> {
                        CommentDTO commentDTO = new CommentDTO();
                        commentDTO.setContent(content);
                        commentDTO.setCreatedAt(Instant.now());
                        commentDTO.setStatus(CommentStatus.ACTIVE);
                        commentDTO.setProject(projectMapper.toDto(project));
                        commentDTO.setUser(userProfileMapper.toDto(userProfile));

                        return save(commentDTO).flatMap(savedComment ->
                            projectRepository
                                .incrementTotalComments(projectId)
                                .then(notifyTeamMembers(project, content, projectId))
                                .thenReturn(savedComment)
                        );
                    });
            });
    }

    private Mono<Void> notifyTeamMembers(com.senprojectbackend1.domain.Project project, String content, Long projectId) {
        if (project.getTeam() != null && project.getTeam().getId() != null) {
            return teamMembershipRepository
                .findAll()
                .filter(membership -> membership.getTeamId().equals(project.getTeam().getId()))
                .flatMap(membership ->
                    notificationService.createNotification(
                        membership.getMembersId(),
                        "Nouveau commentaire sur le projet '" + project.getTitle() + "' : " + content,
                        NotificationType.PROJECT_COMMENT,
                        projectId.toString()
                    )
                )
                .then();
        }
        return Mono.empty();
    }
}
