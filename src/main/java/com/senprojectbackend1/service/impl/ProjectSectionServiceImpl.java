package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.criteria.ProjectSectionCriteria;
import com.senprojectbackend1.repository.ProjectSectionRepository;
import com.senprojectbackend1.service.ProjectSectionService;
import com.senprojectbackend1.service.dto.ProjectSectionDTO;
import com.senprojectbackend1.service.mapper.ProjectSectionMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.ProjectSection}.
 */
@Service
@Transactional
public class ProjectSectionServiceImpl implements ProjectSectionService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectSectionServiceImpl.class);

    private final ProjectSectionRepository projectSectionRepository;

    private final ProjectSectionMapper projectSectionMapper;

    public ProjectSectionServiceImpl(ProjectSectionRepository projectSectionRepository, ProjectSectionMapper projectSectionMapper) {
        this.projectSectionRepository = projectSectionRepository;
        this.projectSectionMapper = projectSectionMapper;
    }

    @Override
    public Mono<ProjectSectionDTO> save(ProjectSectionDTO projectSectionDTO) {
        LOG.debug("Request to save ProjectSection : {}", projectSectionDTO);
        return projectSectionRepository.save(projectSectionMapper.toEntity(projectSectionDTO)).map(projectSectionMapper::toDto);
    }

    @Override
    public Mono<ProjectSectionDTO> update(ProjectSectionDTO projectSectionDTO) {
        LOG.debug("Request to update ProjectSection : {}", projectSectionDTO);
        return projectSectionRepository.save(projectSectionMapper.toEntity(projectSectionDTO)).map(projectSectionMapper::toDto);
    }

    @Override
    public Mono<ProjectSectionDTO> partialUpdate(ProjectSectionDTO projectSectionDTO) {
        LOG.debug("Request to partially update ProjectSection : {}", projectSectionDTO);

        return projectSectionRepository
            .findById(projectSectionDTO.getId())
            .map(existingProjectSection -> {
                projectSectionMapper.partialUpdate(existingProjectSection, projectSectionDTO);

                return existingProjectSection;
            })
            .flatMap(projectSectionRepository::save)
            .map(projectSectionMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectSectionDTO> findByCriteria(ProjectSectionCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ProjectSections by Criteria");
        return projectSectionRepository.findByCriteria(criteria, pageable).map(projectSectionMapper::toDto);
    }

    /**
     * Find the count of projectSections by criteria.
     * @param criteria filtering criteria
     * @return the count of projectSections
     */
    public Mono<Long> countByCriteria(ProjectSectionCriteria criteria) {
        LOG.debug("Request to get the count of all ProjectSections by Criteria");
        return projectSectionRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return projectSectionRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProjectSectionDTO> findOne(Long id) {
        LOG.debug("Request to get ProjectSection : {}", id);
        return projectSectionRepository.findById(id).map(projectSectionMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ProjectSection : {}", id);
        return projectSectionRepository.deleteById(id);
    }
}
