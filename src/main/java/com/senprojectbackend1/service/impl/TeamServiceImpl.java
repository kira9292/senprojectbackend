package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.criteria.TeamCriteria;
import com.senprojectbackend1.repository.ProjectRepository;
import com.senprojectbackend1.repository.TeamRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.service.TeamService;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.ProjectSimple2DTO;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.mapper.ProjectMapper;
import com.senprojectbackend1.service.mapper.TeamMapper;
import com.senprojectbackend1.service.mapper.UserProfileMapper;
import java.util.HashSet;
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
 * Service Implementation for managing {@link com.senprojectbackend1.domain.Team}.
 */
@Service
@Transactional
public class TeamServiceImpl implements TeamService {

    private static final Logger LOG = LoggerFactory.getLogger(TeamServiceImpl.class);

    private final TeamRepository teamRepository;

    private final ProjectRepository projectRepository;

    private final TeamMapper teamMapper;

    private final ProjectMapper projectMapper;
    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;
    private final UserProfileService userProfileService;

    public TeamServiceImpl(
        TeamRepository teamRepository,
        ProjectRepository projectRepository,
        TeamMapper teamMapper,
        ProjectMapper projectMapper,
        UserProfileRepository userProfileRepository,
        UserProfileMapper userProfileMapper,
        UserProfileService userProfileService
    ) {
        this.teamRepository = teamRepository;
        this.projectRepository = projectRepository;
        this.teamMapper = teamMapper;
        this.projectMapper = projectMapper;
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.userProfileService = userProfileService;
    }

    @Override
    public Mono<TeamDTO> save(TeamDTO teamDTO) {
        LOG.debug("Request to save Team : {}", teamDTO);
        return teamRepository.save(teamMapper.toEntity(teamDTO)).map(teamMapper::toDto);
    }

    @Override
    public Mono<TeamDTO> update(TeamDTO teamDTO) {
        LOG.debug("Request to update Team : {}", teamDTO);
        return teamRepository.save(teamMapper.toEntity(teamDTO)).map(teamMapper::toDto);
    }

    @Override
    public Mono<TeamDTO> partialUpdate(TeamDTO teamDTO) {
        LOG.debug("Request to partially update Team : {}", teamDTO);

        return teamRepository
            .findById(teamDTO.getId())
            .map(existingTeam -> {
                teamMapper.partialUpdate(existingTeam, teamDTO);

                return existingTeam;
            })
            .flatMap(teamRepository::save)
            .map(teamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TeamDTO> findByCriteria(TeamCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Teams by Criteria");
        return teamRepository.findByCriteria(criteria, pageable).map(teamMapper::toDto);
    }

    /**
     * Find the count of teams by criteria.
     * @param criteria filtering criteria
     * @return the count of teams
     */
    public Mono<Long> countByCriteria(TeamCriteria criteria) {
        LOG.debug("Request to get the count of all Teams by Criteria");
        return teamRepository.countByCriteria(criteria);
    }

    public Flux<TeamDTO> findAllWithEagerRelationships(Pageable pageable) {
        return teamRepository.findAllWithEagerRelationships(pageable).map(teamMapper::toDto);
    }

    public Mono<Long> countAll() {
        return teamRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TeamDTO> findOne(Long id) {
        LOG.debug("Request to get Team : {}", id);
        return teamRepository.findOneWithEagerRelationships(id).map(teamMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Team : {}", id);
        return teamRepository.deleteById(id);
    }

    @Override
    public Flux<TeamDTO> findAllByMemberLogin(String login) {
        LOG.debug("Request to find all teams for user {}", login);
        return teamRepository.findAllByMembers_Login(login).map(teamMapper::toDto).flatMap(this::enrichTeamWithMembers);
    }

    private Mono<TeamDTO> enrichTeamWithMembers(TeamDTO team) {
        return userProfileRepository
            .findTeamMembersByTeamId(team.getId())
            .map(userProfileMapper::toSimpleDto)
            .collectList()
            .map(members -> {
                team.setMembers(new HashSet<>(members));
                return team;
            });
    }

    @Override
    public Mono<TeamDTO> findOneByIdAndMemberLogin(Long id, String login) {
        LOG.debug("Request to find team with id {} for user {}", id, login);
        return teamRepository.findOneByIdAndMemberLogin(id, login).map(teamMapper::toDto).flatMap(this::enrichTeamWithMembers);
    }

    @Override
    public Flux<ProjectSimple2DTO> getTeamProjects(Long teamId) {
        LOG.debug("Request to find all projects for team with id {}", teamId);
        return projectRepository
            .findProjectsByTeamId(teamId)
            .flatMap(project ->
                projectRepository
                    .findTagsByProjectId(project.getId())
                    .collectList()
                    .map(tags -> {
                        project.setTags(new HashSet<>(tags));
                        return projectMapper.toSimpleDto(project);
                    })
            );
    }

    @Override
    public Mono<TeamDTO> findByProjectId(Long id) {
        LOG.debug("Request to find team for Project : {}", id);
        return teamRepository.findByProjectWithEagerRelationships(id).map(teamMapper::toDto).flatMap(this::enrichTeamWithMembers);
    }

    @Override
    public Mono<Void> deleteTeamAndUpdateProjects(Long teamId, String userLogin) {
        LOG.debug("Request to delete Team and update its projects : {}, user: {}", teamId, userLogin);
        return userProfileService
            .getUserProfileSimpleByLogin(userLogin)
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "User profile not found")))
            .flatMap(userProfile -> {
                return teamRepository
                    .findMemberStatus(teamId, userProfile.getId())
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not a member of the team")))
                    .flatMap(status -> {
                        if (!"ACCEPTED".equals(status)) {
                            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "User is not an accepted member of the team"));
                        }
                        return teamRepository
                            .updateProjectsForDeletedTeam(teamId)
                            .then(teamRepository.markTeamAsDeleted(teamId));
                    });
            });
    }
}
