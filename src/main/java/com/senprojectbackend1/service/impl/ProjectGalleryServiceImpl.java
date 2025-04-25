package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.criteria.ProjectGalleryCriteria;
import com.senprojectbackend1.repository.ProjectGalleryRepository;
import com.senprojectbackend1.service.ProjectGalleryService;
import com.senprojectbackend1.service.dto.ProjectGalleryDTO;
import com.senprojectbackend1.service.mapper.ProjectGalleryMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.ProjectGallery}.
 */
@Service
@Transactional
public class ProjectGalleryServiceImpl implements ProjectGalleryService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectGalleryServiceImpl.class);

    private final ProjectGalleryRepository projectGalleryRepository;

    private final ProjectGalleryMapper projectGalleryMapper;

    public ProjectGalleryServiceImpl(ProjectGalleryRepository projectGalleryRepository, ProjectGalleryMapper projectGalleryMapper) {
        this.projectGalleryRepository = projectGalleryRepository;
        this.projectGalleryMapper = projectGalleryMapper;
    }

    @Override
    public Mono<ProjectGalleryDTO> save(ProjectGalleryDTO projectGalleryDTO) {
        LOG.debug("Request to save ProjectGallery : {}", projectGalleryDTO);
        return projectGalleryRepository.save(projectGalleryMapper.toEntity(projectGalleryDTO)).map(projectGalleryMapper::toDto);
    }

    @Override
    public Mono<ProjectGalleryDTO> update(ProjectGalleryDTO projectGalleryDTO) {
        LOG.debug("Request to update ProjectGallery : {}", projectGalleryDTO);
        return projectGalleryRepository.save(projectGalleryMapper.toEntity(projectGalleryDTO)).map(projectGalleryMapper::toDto);
    }

    @Override
    public Mono<ProjectGalleryDTO> partialUpdate(ProjectGalleryDTO projectGalleryDTO) {
        LOG.debug("Request to partially update ProjectGallery : {}", projectGalleryDTO);

        return projectGalleryRepository
            .findById(projectGalleryDTO.getId())
            .map(existingProjectGallery -> {
                projectGalleryMapper.partialUpdate(existingProjectGallery, projectGalleryDTO);

                return existingProjectGallery;
            })
            .flatMap(projectGalleryRepository::save)
            .map(projectGalleryMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ProjectGalleryDTO> findByCriteria(ProjectGalleryCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ProjectGalleries by Criteria");
        return projectGalleryRepository.findByCriteria(criteria, pageable).map(projectGalleryMapper::toDto);
    }

    /**
     * Find the count of projectGalleries by criteria.
     * @param criteria filtering criteria
     * @return the count of projectGalleries
     */
    public Mono<Long> countByCriteria(ProjectGalleryCriteria criteria) {
        LOG.debug("Request to get the count of all ProjectGalleries by Criteria");
        return projectGalleryRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return projectGalleryRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ProjectGalleryDTO> findOne(Long id) {
        LOG.debug("Request to get ProjectGallery : {}", id);
        return projectGalleryRepository.findById(id).map(projectGalleryMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ProjectGallery : {}", id);
        return projectGalleryRepository.deleteById(id);
    }
}
