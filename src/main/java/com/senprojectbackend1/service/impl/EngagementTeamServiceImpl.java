package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.criteria.EngagementTeamCriteria;
import com.senprojectbackend1.repository.EngagementTeamRepository;
import com.senprojectbackend1.service.EngagementTeamService;
import com.senprojectbackend1.service.dto.EngagementTeamDTO;
import com.senprojectbackend1.service.mapper.EngagementTeamMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.EngagementTeam}.
 */
@Service
@Transactional
public class EngagementTeamServiceImpl implements EngagementTeamService {

    private static final Logger LOG = LoggerFactory.getLogger(EngagementTeamServiceImpl.class);

    private final EngagementTeamRepository engagementTeamRepository;

    private final EngagementTeamMapper engagementTeamMapper;

    public EngagementTeamServiceImpl(EngagementTeamRepository engagementTeamRepository, EngagementTeamMapper engagementTeamMapper) {
        this.engagementTeamRepository = engagementTeamRepository;
        this.engagementTeamMapper = engagementTeamMapper;
    }

    @Override
    public Mono<EngagementTeamDTO> save(EngagementTeamDTO engagementTeamDTO) {
        LOG.debug("Request to save EngagementTeam : {}", engagementTeamDTO);
        return engagementTeamRepository.save(engagementTeamMapper.toEntity(engagementTeamDTO)).map(engagementTeamMapper::toDto);
    }

    @Override
    public Mono<EngagementTeamDTO> update(EngagementTeamDTO engagementTeamDTO) {
        LOG.debug("Request to update EngagementTeam : {}", engagementTeamDTO);
        return engagementTeamRepository.save(engagementTeamMapper.toEntity(engagementTeamDTO)).map(engagementTeamMapper::toDto);
    }

    @Override
    public Mono<EngagementTeamDTO> partialUpdate(EngagementTeamDTO engagementTeamDTO) {
        LOG.debug("Request to partially update EngagementTeam : {}", engagementTeamDTO);

        return engagementTeamRepository
            .findById(engagementTeamDTO.getId())
            .map(existingEngagementTeam -> {
                engagementTeamMapper.partialUpdate(existingEngagementTeam, engagementTeamDTO);

                return existingEngagementTeam;
            })
            .flatMap(engagementTeamRepository::save)
            .map(engagementTeamMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<EngagementTeamDTO> findByCriteria(EngagementTeamCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all EngagementTeams by Criteria");
        return engagementTeamRepository.findByCriteria(criteria, pageable).map(engagementTeamMapper::toDto);
    }

    /**
     * Find the count of engagementTeams by criteria.
     * @param criteria filtering criteria
     * @return the count of engagementTeams
     */
    public Mono<Long> countByCriteria(EngagementTeamCriteria criteria) {
        LOG.debug("Request to get the count of all EngagementTeams by Criteria");
        return engagementTeamRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return engagementTeamRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<EngagementTeamDTO> findOne(Long id) {
        LOG.debug("Request to get EngagementTeam : {}", id);
        return engagementTeamRepository.findById(id).map(engagementTeamMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete EngagementTeam : {}", id);
        return engagementTeamRepository.deleteById(id);
    }
}
