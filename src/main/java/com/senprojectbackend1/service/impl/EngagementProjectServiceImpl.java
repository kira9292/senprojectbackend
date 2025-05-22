package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.criteria.EngagementProjectCriteria;
import com.senprojectbackend1.domain.enumeration.EngagementType;
import com.senprojectbackend1.repository.EngagementProjectRepository;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.service.EngagementProjectService;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.EngagementProjectDTO;
import com.senprojectbackend1.service.mapper.EngagementProjectMapper;
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
 * Service Implementation for managing {@link com.senprojectbackend1.domain.EngagementProject}.
 */
@Service
@Transactional
public class EngagementProjectServiceImpl implements EngagementProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(EngagementProjectServiceImpl.class);

    private final EngagementProjectRepository engagementProjectRepository;
    private final EngagementProjectMapper engagementProjectMapper;
    private final ProjectRepository projectRepository;
    private final UserProfileService userProfileService;

    public EngagementProjectServiceImpl(
        EngagementProjectRepository engagementProjectRepository,
        EngagementProjectMapper engagementProjectMapper,
        ProjectRepository projectRepository,
        UserProfileService userProfileService
    ) {
        this.engagementProjectRepository = engagementProjectRepository;
        this.engagementProjectMapper = engagementProjectMapper;
        this.projectRepository = projectRepository;
        this.userProfileService = userProfileService;
    }

    @Override
    public Mono<EngagementProjectDTO> save(EngagementProjectDTO engagementProjectDTO) {
        LOG.debug("Request to save EngagementProject : {}", engagementProjectDTO);
        return engagementProjectRepository.save(engagementProjectMapper.toEntity(engagementProjectDTO)).map(engagementProjectMapper::toDto);
    }

    @Override
    public Mono<EngagementProjectDTO> update(EngagementProjectDTO engagementProjectDTO) {
        LOG.debug("Request to update EngagementProject : {}", engagementProjectDTO);
        return engagementProjectRepository.save(engagementProjectMapper.toEntity(engagementProjectDTO)).map(engagementProjectMapper::toDto);
    }

    @Override
    public Mono<EngagementProjectDTO> partialUpdate(EngagementProjectDTO engagementProjectDTO) {
        LOG.debug("Request to partially update EngagementProject : {}", engagementProjectDTO);

        return engagementProjectRepository
            .findById(engagementProjectDTO.getId())
            .map(existingEngagementProject -> {
                engagementProjectMapper.partialUpdate(existingEngagementProject, engagementProjectDTO);

                return existingEngagementProject;
            })
            .flatMap(engagementProjectRepository::save)
            .map(engagementProjectMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EngagementProjectDTO> findByCriteria(EngagementProjectCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all EngagementProjects by Criteria");
        return engagementProjectRepository.findByCriteria(criteria, pageable).map(engagementProjectMapper::toDto);
    }

    /**
     * Find the count of engagementProjects by criteria.
     * @param criteria filtering criteria
     * @return the count of engagementProjects
     */
    public Mono<Long> countByCriteria(EngagementProjectCriteria criteria) {
        LOG.debug("Request to get the count of all EngagementProjects by Criteria");
        return engagementProjectRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return engagementProjectRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EngagementProjectDTO> findOne(Long id) {
        LOG.debug("Request to get EngagementProject : {}", id);
        return engagementProjectRepository.findById(id).map(engagementProjectMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EngagementProject : {}", id);
        return engagementProjectRepository.deleteById(id);
    }

    /**
     * Crée un nouvel engagement (LIKE ou SHARE) pour un projet.
     *
     * @param projectId l'ID du projet
     * @param type le type d'engagement (LIKE ou SHARE)
     * @param login le login de l'utilisateur
     * @return le DTO de l'engagement créé ou existant
     */
    @Override
    @Transactional
    public Mono<EngagementProjectDTO> createProjectEngagement(Long projectId, String type, String login) {
        LOG.debug("Service request to create Project engagement : projectId={}, type={}, login={}", projectId, type, login);

        return userProfileService
            .getUserProfileSimpleByLogin(login)
            .flatMap(userProfile -> {
                if (userProfile == null) {
                    LOG.error("User profile not found for login: {}", login);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
                }

                String userId = userProfile.getId();

                return engagementProjectRepository
                    .findAllEngagementsByUserIdAndProjectIdAndType(userId, projectId, type)
                    .collectList()
                    .flatMap(existingEngagements -> {
                        if (type.equals("LIKE")) {
                            if (!existingEngagements.isEmpty()) {
                                // S'il y a plusieurs likes, on les supprime tous (toggle off)
                                return engagementProjectRepository
                                    .deleteAll(existingEngagements)
                                    .then(projectRepository.decrementTotalLikes(projectId))
                                    .then(Mono.<EngagementProjectDTO>empty());
                            } else {
                                // Aucun like, on en crée un
                                EngagementProject engagement = new EngagementProject();
                                engagement.setType(EngagementType.LIKE);
                                engagement.setCreatedAt(Instant.now());
                                engagement.setUserId(userId);
                                engagement.setProjectId(projectId);

                                return engagementProjectRepository
                                    .save(engagement)
                                    .flatMap(savedEngagement ->
                                        projectRepository
                                            .incrementTotalLikes(projectId)
                                            .thenReturn(engagementProjectMapper.toDto(savedEngagement))
                                    );
                            }
                        } else if (type.equals("SHARE")) {
                            if (!existingEngagements.isEmpty()) {
                                // S'il y a plusieurs shares, on ne garde qu'un seul, on supprime les autres
                                if (existingEngagements.size() > 1) {
                                    return engagementProjectRepository
                                        .deleteAll(existingEngagements.subList(1, existingEngagements.size()))
                                        .then(Mono.just(engagementProjectMapper.toDto(existingEngagements.get(0))));
                                } else {
                                    // Un seul share existe déjà, on ne fait rien
                                    return Mono.<EngagementProjectDTO>empty();
                                }
                            } else {
                                // Aucun share, on en crée un
                                EngagementProject engagement = new EngagementProject();
                                engagement.setType(EngagementType.SHARE);
                                engagement.setCreatedAt(Instant.now());
                                engagement.setUserId(userId);
                                engagement.setProjectId(projectId);

                                return engagementProjectRepository
                                    .save(engagement)
                                    .flatMap(savedEngagement ->
                                        projectRepository
                                            .incrementTotalShares(projectId)
                                            .thenReturn(engagementProjectMapper.toDto(savedEngagement))
                                    );
                            }
                        }
                        return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid engagement type"));
                    });
            });
    }

    /**
     * Retourne le statut d'engagement (like/share) d'un utilisateur pour un projet donné.
     *
     * @param projectId l'ID du projet
     * @param login le login de l'utilisateur
     * @return le statut d'engagement (like/share)
     */
    @Override
    public Mono<com.senprojectbackend1.service.dto.EngagementStatusDTO> getUserEngagementStatus(Long projectId, String login) {
        return userProfileService
            .getUserProfileSimpleByLogin(login)
            .flatMap(userProfile -> {
                if (userProfile == null) {
                    LOG.error("User profile not found for login: {}", login);
                    return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found"));
                }
                String userId = userProfile.getId();
                Mono<Boolean> likeMono = engagementProjectRepository
                    .findAllEngagementsByUserIdAndProjectIdAndType(userId, projectId, "LIKE")
                    .collectList()
                    .flatMap(list -> {
                        if (list.size() > 1) {
                            // Supprimer les doublons, ne garder qu'un seul
                            return engagementProjectRepository.deleteAll(list.subList(1, list.size())).thenReturn(true);
                        }
                        return Mono.just(!list.isEmpty());
                    });
                Mono<Boolean> shareMono = engagementProjectRepository
                    .findAllEngagementsByUserIdAndProjectIdAndType(userId, projectId, "SHARE")
                    .collectList()
                    .flatMap(list -> {
                        if (list.size() > 1) {
                            return engagementProjectRepository.deleteAll(list.subList(1, list.size())).thenReturn(true);
                        }
                        return Mono.just(!list.isEmpty());
                    });
                return Mono.zip(likeMono, shareMono).map(tuple ->
                    new com.senprojectbackend1.service.dto.EngagementStatusDTO(tuple.getT1(), tuple.getT2())
                );
            });
    }
}
