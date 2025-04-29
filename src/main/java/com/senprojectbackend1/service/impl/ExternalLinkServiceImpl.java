package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.criteria.ExternalLinkCriteria;
import com.senprojectbackend1.repository.ExternalLinkRepository;
import com.senprojectbackend1.service.ExternalLinkService;
import com.senprojectbackend1.service.dto.ExternalLinkDTO;
import com.senprojectbackend1.service.mapper.ExternalLinkMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.ExternalLink}.
 */
@Service
public class ExternalLinkServiceImpl implements ExternalLinkService {

    private static final Logger LOG = LoggerFactory.getLogger(ExternalLinkServiceImpl.class);

    private final ExternalLinkRepository externalLinkRepository;

    private final ExternalLinkMapper externalLinkMapper;

    public ExternalLinkServiceImpl(ExternalLinkRepository externalLinkRepository, ExternalLinkMapper externalLinkMapper) {
        this.externalLinkRepository = externalLinkRepository;
        this.externalLinkMapper = externalLinkMapper;
    }

    @Override
    @Transactional
    public Mono<ExternalLinkDTO> save(ExternalLinkDTO externalLinkDTO) {
        LOG.debug("Request to save ExternalLink : {}", externalLinkDTO);
        return externalLinkRepository.save(externalLinkMapper.toEntity(externalLinkDTO)).map(externalLinkMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<ExternalLinkDTO> update(ExternalLinkDTO externalLinkDTO) {
        LOG.debug("Request to update ExternalLink : {}", externalLinkDTO);
        return externalLinkRepository.save(externalLinkMapper.toEntity(externalLinkDTO)).map(externalLinkMapper::toDto);
    }

    @Transactional
    @Override
    public Mono<ExternalLinkDTO> partialUpdate(ExternalLinkDTO externalLinkDTO) {
        LOG.debug("Request to partially update ExternalLink : {}", externalLinkDTO);

        return externalLinkRepository
            .findById(externalLinkDTO.getId())
            .map(existingExternalLink -> {
                externalLinkMapper.partialUpdate(existingExternalLink, externalLinkDTO);

                return existingExternalLink;
            })
            .flatMap(externalLinkRepository::save)
            .map(externalLinkMapper::toDto);
    }

    @Override
    public Flux<ExternalLinkDTO> findByCriteria(ExternalLinkCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all ExternalLinks by Criteria");
        return externalLinkRepository.findByCriteria(criteria, pageable).map(externalLinkMapper::toDto);
    }

    /**
     * Find the count of externalLinks by criteria.
     * @param criteria filtering criteria
     * @return the count of externalLinks
     */
    public Mono<Long> countByCriteria(ExternalLinkCriteria criteria) {
        LOG.debug("Request to get the count of all ExternalLinks by Criteria");
        return externalLinkRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return externalLinkRepository.count();
    }

    @Override
    public Mono<ExternalLinkDTO> findOne(Long id) {
        LOG.debug("Request to get ExternalLink : {}", id);
        return externalLinkRepository.findById(id).map(externalLinkMapper::toDto);
    }

    @Override
    @Transactional
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete ExternalLink : {}", id);
        return externalLinkRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<ExternalLinkDTO> findByProject(Long id) {
        LOG.debug("Request to get all ExternalLinks for Project : {}", id);
        return externalLinkRepository.findByProject(id).map(externalLinkMapper::toDto);
    }
}
