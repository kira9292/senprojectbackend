package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.EngagementProject;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.Tag;
import com.senprojectbackend1.domain.criteria.ProjectCriteria;
import com.senprojectbackend1.service.dto.ProjectSimple2DTO;
import com.senprojectbackend1.service.dto.TagDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Project entity.
 */
@SuppressWarnings("unused")
@Repository
public interface ProjectRepository extends ReactiveCrudRepository<Project, Long>, ProjectRepositoryInternal {
    Flux<Project> findAllBy(Pageable pageable);

    @Override
    Mono<Project> findOneWithEagerRelationships(Long id);

    @Override
    Flux<Project> findAllWithEagerRelationships();

    @Override
    Flux<Project> findAllWithEagerRelationships(Pageable page);

    @Query("SELECT * FROM project entity WHERE entity.team_id = :id")
    Flux<Project> findByTeam(Long id);

    @Query("SELECT * FROM project entity WHERE entity.team_id IS NULL")
    Flux<Project> findAllWhereTeamIsNull();

    @Query(
        "SELECT entity.* FROM project entity JOIN rel_project__tags joinTable ON entity.id = joinTable.tags_id WHERE joinTable.tags_id = :id"
    )
    Flux<Project> findByTags(Long id);

    @Override
    <S extends Project> Mono<S> save(S entity);

    @Override
    Flux<Project> findAll();

    @Override
    Mono<Project> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    @Query(
        "SELECT * FROM project entity" +
        "    join public.team t on t.id = entity.team_id" +
        "    join public.rel_team__members rtm on t.id = rtm.team_id" +
        "    WHERE rtm.members_id = :userID "
    )
    Flux<Project> findByTeamMembersIdWithEagerRelationships(String userID);

    /**
     * Find all projects where the user with the given ID is a member of the team.
     *
     * @param userId the ID of the user
     * @return the list of projects
     */
    @Query(
        "SELECT p.* FROM project p " +
        "JOIN team t ON p.team_id = t.id " +
        "JOIN rel_team__members rtm ON t.id = rtm.team_id " +
        "WHERE rtm.members_id = :userId"
    )
    Flux<Project> findByTeamMembersId(@Param("userId") String userId);

    default Flux<Project> findAllByTeamMemberWithEagerRelationships(String userId) {
        return findProjectIdsByTeamMemberId(userId).flatMap(this::findOneWithEagerRelationships);
    }

    @Query(
        value = "SELECT DISTINCT p.id FROM project p " +
        "JOIN team t ON t.id = p.team_id " +
        "JOIN rel_team__members rtm ON t.id = rtm.team_id " +
        "WHERE rtm.members_id = :userId"
    )
    Flux<Long> findProjectIdsByTeamMemberId(String userId);

    @Query(
        "SELECT p.* FROM project p " + "JOIN rel_project__favoritedby rpf ON p.id = rpf.project_id " + "WHERE rpf.favoritedby_id = :userId"
    )
    Flux<Project> findByFavoritedById(@Param("userId") String userId);

    @Query("SELECT t.* FROM tag t " + "JOIN rel_project__tags rpt ON t.id = rpt.tags_id " + "WHERE rpt.project_id = :projectId")
    Flux<Tag> findTagsByProjectId(@Param("projectId") Long projectId);

    @Query(
        "SELECT p.id, p.title, p.description, p.showcase, p.status, p.created_at, " +
        "p.open_to_collaboration, p.open_to_funding, p.type, p.total_likes, " +
        "p.total_shares, p.total_views, p.total_comments, p.total_favorites " +
        "FROM project p " +
        "JOIN team t ON p.team_id = t.id " +
        "WHERE t.id = :teamId"
    )
    Flux<Project> findProjectsByTeamId(@Param("teamId") Long teamId);

    @Query("UPDATE project SET total_views = total_views + 1 WHERE id = :projectId")
    Mono<Void> incrementTotalViews(@Param("projectId") Long projectId);

    @Query("UPDATE project SET total_likes = total_likes + 1 WHERE id = :id")
    Mono<Void> incrementTotalLikes(@Param("id") Long id);

    @Query("UPDATE project SET total_shares = total_shares + 1 WHERE id = :id")
    Mono<Void> incrementTotalShares(@Param("id") Long id);

    @Query("UPDATE project SET total_comments = total_comments + 1 WHERE id = :id")
    Mono<Void> incrementTotalComments(@Param("id") Long id);

    @Query("INSERT INTO rel_project__favoritedby (project_id, favoritedby_id) VALUES (:projectId, :userId)")
    Mono<Void> addToFavorites(@Param("projectId") Long projectId, @Param("userId") String userId);

    @Query("DELETE FROM rel_project__favoritedby WHERE project_id = :projectId AND favoritedby_id = :userId")
    Mono<Void> removeFromFavorites(@Param("projectId") Long projectId, @Param("userId") String userId);

    @Query("SELECT COUNT(*) > 0 FROM rel_project__favoritedby WHERE project_id = :projectId AND favoritedby_id = :userId")
    Mono<Boolean> isFavorite(@Param("projectId") Long projectId, @Param("userId") String userId);

    @Query("UPDATE project SET total_favorites = total_favorites + 1 WHERE id = :id")
    Mono<Void> incrementTotalFavorites(@Param("id") Long id);

    @Query("UPDATE project SET total_favorites = total_favorites - 1 WHERE id = :id")
    Mono<Void> decrementTotalFavorites(@Param("id") Long id);

    @Query("UPDATE project SET total_likes = total_likes - 1 WHERE id = :id")
    Mono<Void> decrementTotalLikes(@Param("id") Long id);

    @Query("UPDATE project SET status = 'DELETED' WHERE id = :id")
    Mono<Void> updateProjectStatusToDeleted(@Param("id") Long id);

    @Query(
        "SELECT * FROM project " +
        "WHERE status = 'PUBLISHED' AND is_deleted = false " +
        "ORDER BY (total_views + total_likes + total_favorites) DESC " +
        "LIMIT 10"
    )
    Flux<Project> findTop10PopularProjects();

    // Ajouter ces méthodes dans ProjectRepository

    @Query(
        "SELECT p.* FROM project p " +
        "JOIN rel_project__tags rpt ON p.id = rpt.project_id " +
        "JOIN tag t ON rpt.tags_id = t.id " +
        "WHERE t.name = :tagName " +
        "LIMIT :limit OFFSET :offset"
    )
    Flux<Project> findByTagName(@Param("tagName") String tagName, @Param("limit") int limit, @Param("offset") int offset);

    @Query(
        "SELECT COUNT(DISTINCT p.id) FROM project p " +
        "JOIN rel_project__tags rpt ON p.id = rpt.project_id " +
        "JOIN tag t ON rpt.tags_id = t.id " +
        "WHERE t.name = :tagName"
    )
    Mono<Long> countByTagName(@Param("tagName") String tagName);
}

interface ProjectRepositoryInternal {
    <S extends Project> Mono<S> save(S entity);

    Flux<Project> findAllBy(Pageable pageable);

    Flux<Project> findAll();

    Mono<Project> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Project> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Project> findByCriteria(ProjectCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(ProjectCriteria criteria);

    Mono<Project> findOneWithEagerRelationships(Long id);

    Flux<Project> findAllWithEagerRelationships();

    Flux<Project> findAllWithEagerRelationships(Pageable page);

    Mono<Void> deleteById(Long id);
}
