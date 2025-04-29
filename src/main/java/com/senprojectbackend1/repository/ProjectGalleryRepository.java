package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.ProjectGallery;
import com.senprojectbackend1.domain.criteria.ProjectGalleryCriteria;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the ProjectGallery entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectGalleryRepository extends ReactiveCrudRepository<ProjectGallery, Long>, ProjectGalleryRepositoryInternal {
    Flux<ProjectGallery> findAllBy(Pageable pageable);

    @Query("SELECT * FROM project_gallery entity WHERE entity.project_id = :id")
    Flux<ProjectGallery> findByProject(Long id);

    @Query("SELECT * FROM project_gallery entity WHERE entity.project_id IS NULL")
    Flux<ProjectGallery> findAllWhereProjectIsNull();

    @Override
    <S extends ProjectGallery> Mono<S> save(S entity);

    @Override
    Flux<ProjectGallery> findAll();

    @Override
    Mono<ProjectGallery> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    @Modifying
    @Query("DELETE FROM project_gallery WHERE project_id = :projectId")
    reactor.core.publisher.Mono<Void> deleteByProjectId(@org.springframework.data.repository.query.Param("projectId") Long projectId);
}

interface ProjectGalleryRepositoryInternal {
    <S extends ProjectGallery> Mono<S> save(S entity);

    Flux<ProjectGallery> findAllBy(Pageable pageable);

    Flux<ProjectGallery> findAll();

    Mono<ProjectGallery> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<ProjectGallery> findAllBy(Pageable pageable, Criteria criteria);
    Flux<ProjectGallery> findByCriteria(ProjectGalleryCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProjectGalleryCriteria criteria);
}
