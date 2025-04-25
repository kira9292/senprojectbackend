package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.ProjectSection;
import com.senprojectbackend1.domain.criteria.ProjectSectionCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProjectSection entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectSectionRepository extends ReactiveCrudRepository<ProjectSection, Long>, ProjectSectionRepositoryInternal {
    Flux<ProjectSection> findAllBy(Pageable pageable);

    @Query("SELECT * FROM project_section entity WHERE entity.project_id = :id")
    Flux<ProjectSection> findByProject(Long id);

    @Query("SELECT * FROM project_section entity WHERE entity.project_id IS NULL")
    Flux<ProjectSection> findAllWhereProjectIsNull();

    @Override
    <S extends ProjectSection> Mono<S> save(S entity);

    @Override
    Flux<ProjectSection> findAll();

    @Override
    Mono<ProjectSection> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);
}

interface ProjectSectionRepositoryInternal {
    <S extends ProjectSection> Mono<S> save(S entity);

    Flux<ProjectSection> findAllBy(Pageable pageable);

    Flux<ProjectSection> findAll();

    Mono<ProjectSection> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProjectSection> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ProjectSection> findByCriteria(ProjectSectionCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProjectSectionCriteria criteria);
}
