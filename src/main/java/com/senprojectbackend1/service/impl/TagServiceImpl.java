package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.domain.criteria.TagCriteria;
import com.senprojectbackend1.repository.TagRepository;
import com.senprojectbackend1.service.TagService;
import com.senprojectbackend1.service.dto.PageDTO;
import com.senprojectbackend1.service.dto.TagDTO;
import com.senprojectbackend1.service.dto.TagWithCountDTO;
import com.senprojectbackend1.service.mapper.TagMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.Tag}.
 */
@Service
@Transactional
public class TagServiceImpl implements TagService {

    private static final Logger LOG = LoggerFactory.getLogger(TagServiceImpl.class);

    private final TagRepository tagRepository;

    private final TagMapper tagMapper;

    public TagServiceImpl(TagRepository tagRepository, TagMapper tagMapper) {
        this.tagRepository = tagRepository;
        this.tagMapper = tagMapper;
    }

    @Override
    public Mono<TagDTO> save(TagDTO tagDTO) {
        LOG.debug("Request to save Tag : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).map(tagMapper::toDto);
    }

    @Override
    public Mono<TagDTO> update(TagDTO tagDTO) {
        LOG.debug("Request to update Tag : {}", tagDTO);
        return tagRepository.save(tagMapper.toEntity(tagDTO)).map(tagMapper::toDto);
    }

    @Override
    public Mono<TagDTO> partialUpdate(TagDTO tagDTO) {
        LOG.debug("Request to partially update Tag : {}", tagDTO);

        return tagRepository
            .findById(tagDTO.getId())
            .map(existingTag -> {
                tagMapper.partialUpdate(existingTag, tagDTO);

                return existingTag;
            })
            .flatMap(tagRepository::save)
            .map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<TagDTO> findByCriteria(TagCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all Tags by Criteria");
        return tagRepository.findByCriteria(criteria, pageable).map(tagMapper::toDto);
    }

    /**
     * Find the count of tags by criteria.
     * @param criteria filtering criteria
     * @return the count of tags
     */
    public Mono<Long> countByCriteria(TagCriteria criteria) {
        LOG.debug("Request to get the count of all Tags by Criteria");
        return tagRepository.countByCriteria(criteria);
    }

    public Mono<Long> countAll() {
        return tagRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<TagDTO> findOne(Long id) {
        LOG.debug("Request to get Tag : {}", id);
        return tagRepository.findById(id).map(tagMapper::toDto);
    }

    @Override
    public Mono<Void> delete(Long id) {
        LOG.debug("Request to delete Tag : {}", id);
        return tagRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public Flux<TagDTO> findByProjectId(Long projectId) {
        LOG.debug("Request to get Tags for Project : {}", projectId);
        return tagRepository.findByProjectId(projectId).map(tagMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<PageDTO<TagWithCountDTO>> getPaginatedTags(int page, int size) {
        LOG.debug("Request to get paginated Tags with count");
        int offset = page * size;

        return Mono.zip(
            tagRepository
                .findTagsWithCount(size, offset)
                .flatMap(tag ->
                    tagRepository
                        .countProjectsForTag(tag.getId())
                        .map(count -> {
                            TagWithCountDTO dto = new TagWithCountDTO();
                            dto.setId(tag.getId());
                            dto.setName(tag.getName());
                            dto.setCount(count);
                            return dto;
                        })
                )
                .collectList(),
            tagRepository.countAllTags()
        ).map(tuple -> {
            var tags = tuple.getT1();
            var total = tuple.getT2();

            PageDTO<TagWithCountDTO> pageDTO = new PageDTO<>();
            pageDTO.setContent(tags);
            pageDTO.setTotalElements(total);
            pageDTO.setTotalPages((int) Math.ceil((double) total / size));
            pageDTO.setCurrentPage(page);
            pageDTO.setPageSize(size);
            return pageDTO;
        });
    }
}
