package com.senprojectbackend1.repository;

import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.criteria.NotificationCriteria;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the Notification entity.
 */
@SuppressWarnings("unused")
@Repository
public interface NotificationRepository extends ReactiveCrudRepository<Notification, Long>, NotificationRepositoryInternal {
    Flux<Notification> findAllBy(Pageable pageable);

    @Query("SELECT * FROM notification entity WHERE entity.user_id = :id")
    Flux<Notification> findByUser(Long id);

    @Query("SELECT * FROM notification entity WHERE entity.user_id IS NULL")
    Flux<Notification> findAllWhereUserIsNull();

    @Override
    <S extends Notification> Mono<S> save(S entity);

    @Override
    Flux<Notification> findAll();

    @Override
    Mono<Notification> findById(Long id);

    @Override
    Mono<Void> deleteById(Long id);

    @Query("SELECT * FROM notification WHERE user_id = :userId AND read_at IS NULL ORDER BY created_at DESC")
    Flux<Notification> findByUserIdAndReadAtIsNull(String userId);

    @Query("SELECT * FROM notification WHERE user_id = :userId ORDER BY created_at DESC")
    Flux<Notification> findByUserIdOrderByCreatedAtDesc(String userId);

    @Query("SELECT * FROM notification WHERE user_id = :userId ORDER BY created_at DESC LIMIT :limit")
    Flux<Notification> findRecentNotificationsByUserId(String userId, int limit);

    @Query("SELECT * FROM notification WHERE user_id = :userId ORDER BY created_at DESC")
    Flux<Notification> findByUserId(String userId, Pageable pageable);

    @Query("SELECT COUNT(*) FROM notification WHERE user_id = :userId AND read_at IS NULL")
    Mono<Long> countUnreadByUserId(String userId);

    @Modifying
    @Query("DELETE FROM notification WHERE user_id = :userId AND entity_id = :entityId AND type = :type")
    Mono<Void> deleteByUserIdAndEntityIdAndType(String userId, String entityId, NotificationType type);
}

interface NotificationRepositoryInternal {
    <S extends Notification> Mono<S> save(S entity);

    Flux<Notification> findAllBy(Pageable pageable);

    Flux<Notification> findAll();

    Mono<Notification> findById(Long id);
    // this is not supported at the moment because of https://github.com/jhipster/generator-jhipster/issues/18269
    // Flux<Notification> findAllBy(Pageable pageable, Criteria criteria);
    Flux<Notification> findByCriteria(NotificationCriteria criteria, Pageable pageable);

    Mono<Long> countByCriteria(NotificationCriteria criteria);
}
