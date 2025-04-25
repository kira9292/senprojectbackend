package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.Notification;
import com.senprojectbackend1.domain.criteria.NotificationCriteria;
import com.senprojectbackend1.domain.enumeration.NotificationType;
import com.senprojectbackend1.repository.NotificationRepository;
import com.senprojectbackend1.repository.UserProfileRepository;
import com.senprojectbackend1.security.SecurityUtils;
import com.senprojectbackend1.service.NotificationService;
import com.senprojectbackend1.service.dto.NotificationDTO;
import com.senprojectbackend1.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.jhipster.service.filter.StringFilter;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link Notification}.
 */
@RestController
@RequestMapping("/api/notifications")
public class NotificationResource {

    private static final Logger LOG = LoggerFactory.getLogger(NotificationResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1Notification";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final NotificationService notificationService;

    private final NotificationRepository notificationRepository;

    private final UserProfileRepository userProfileRepository;

    public NotificationResource(
        NotificationService notificationService,
        NotificationRepository notificationRepository,
        UserProfileRepository userProfileRepository
    ) {
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.userProfileRepository = userProfileRepository;
    }

    /**
     * {@code POST  /notifications} : Create a new notification.
     *
     * @param notificationDTO the notificationDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new notificationDTO, or with status {@code 400 (Bad Request)} if the notification has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<NotificationDTO>> createNotification(@Valid @RequestBody NotificationDTO notificationDTO)
        throws URISyntaxException {
        LOG.debug("REST request to save Notification : {}", notificationDTO);
        if (notificationDTO.getId() != null) {
            throw new BadRequestAlertException("A new notification cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return notificationService
            .save(notificationDTO)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/notifications/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            });
    }

    /**
     * {@code PUT  /notifications/:id} : Updates an existing notification.
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<NotificationDTO>> updateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Notification : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return notificationService
                    .update(notificationDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /notifications/:id} : Partial updates given fields of an existing notification, field will ignore if it is null
     *
     * @param id the id of the notificationDTO to save.
     * @param notificationDTO the notificationDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notificationDTO,
     * or with status {@code 400 (Bad Request)} if the notificationDTO is not valid,
     * or with status {@code 404 (Not Found)} if the notificationDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the notificationDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<NotificationDTO>> partialUpdateNotification(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody NotificationDTO notificationDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Notification partially : {}, {}", id, notificationDTO);
        if (notificationDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, notificationDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return notificationRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<NotificationDTO> result = notificationService.partialUpdate(notificationDTO);

                return result
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(res ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, res.getId().toString()))
                            .body(res)
                    );
            });
    }

    /**
     * {@code GET  /notifications} : get all the notifications.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<NotificationDTO>>> getAllNotifications(
        NotificationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Notifications by criteria: {}", criteria);
        return notificationService
            .countByCriteria(criteria)
            .zipWith(notificationService.findByCriteria(criteria, pageable).collectList())
            .map(countWithEntities ->
                ResponseEntity.ok()
                    .headers(
                        PaginationUtil.generatePaginationHttpHeaders(
                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                        )
                    )
                    .body(countWithEntities.getT2())
            );
    }

    /**
     * {@code GET  /notifications/count} : count all the notifications.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countNotifications(NotificationCriteria criteria) {
        LOG.debug("REST request to count Notifications by criteria: {}", criteria);
        return notificationService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /notifications/:id} : get the "id" notification.
     *
     * @param id the id of the notificationDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notificationDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<NotificationDTO>> getNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Notification : {}", id);
        Mono<NotificationDTO> notificationDTO = notificationService.findOne(id);
        return ResponseUtil.wrapOrNotFound(notificationDTO);
    }

    /**
     * {@code DELETE  /notifications/:id} : delete the "id" notification.
     *
     * @param id the id of the notificationDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteNotification(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Notification : {}", id);
        return notificationService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    /**
     * {@code GET /notifications/user/{userId}} : Get all notifications for a user
     *
     * @param userId The ID of the user
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the notifications
     */
    @GetMapping("/user/{userId}")
    public Flux<Notification> getAllNotificationsForUser(@PathVariable String userId) {
        LOG.debug("REST request to get all notifications for user: {}", userId);
        return notificationService.getAllNotificationsForUser(userId);
    }

    /**
     * {@code GET /notifications/user/{userId}/unread} : Get all unread notifications for a user
     *
     * @param userId The ID of the user
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the unread notifications
     */
    @GetMapping("/user/{userId}/unread")
    public Flux<Notification> getUnreadNotificationsForUser(@PathVariable String userId) {
        LOG.debug("REST request to get unread notifications for user: {}", userId);
        return notificationService.getUnreadNotificationsForUser(userId);
    }

    /**
     * {@code PUT /notifications/{id}/read} : Mark a notification as read
     *
     * @param id The ID of the notification to mark as read
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated notification,
     * or with status {@code 404 (Not Found)} if the notification doesn't exist
     */
    @PutMapping("/{id}/read")
    public Mono<ResponseEntity<Notification>> markNotificationAsRead(@PathVariable Long id) {
        LOG.debug("REST request to mark notification as read: {}", id);
        return notificationService.markAsRead(id).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /notifications/saved/me} : get all notifications for the current user.
     *
     * @param criteria the criteria which the requested entities should match.
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of notifications in body.
     */
    @GetMapping("/saved/me")
    public Mono<ResponseEntity<List<NotificationDTO>>> getCurrentUserNotifications(
        NotificationCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get current user notifications with criteria: {}", criteria);
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login ->
                userProfileRepository
                    .findOneByLogin(login)
                    .flatMap(user -> {
                        // Ajouter le filtre sur l'ID de l'utilisateur
                        criteria.setUserId((StringFilter) new StringFilter().setEquals(user.getId()));

                        return notificationService
                            .countByCriteria(criteria)
                            .zipWith(notificationService.findByCriteria(criteria, pageable).collectList())
                            .map(countWithEntities ->
                                ResponseEntity.ok()
                                    .headers(
                                        PaginationUtil.generatePaginationHttpHeaders(
                                            ForwardedHeaderUtils.adaptFromForwardedHeaders(request.getURI(), request.getHeaders()),
                                            new PageImpl<>(countWithEntities.getT2(), pageable, countWithEntities.getT1())
                                        )
                                    )
                                    .body(countWithEntities.getT2())
                            );
                    })
            )
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code PUT  /notifications/mark-all-read} : mark all notifications as read for the current user.
     *
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @PutMapping("/mark-all-read")
    public Mono<ResponseEntity<Object>> markAllNotificationsAsRead() {
        LOG.debug("REST request to mark all notifications as read for current user");
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login ->
                userProfileRepository
                    .findOneByLogin(login)
                    .flatMap(user ->
                        notificationService.markAllAsRead(user.getId()).then(Mono.just(ResponseEntity.<Void>noContent().build()))
                    )
            )
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code POST  /notify} : Create notifications for specific users or all users.
     *
     * @param content the content of the notification
     * @param type the type of notification
     * @param entityId the logins of the related entities (optional)
     * @param action the action to perform (optional)
     * @param targetLogins array of user IDs or "all" for all users
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the created notifications
     */
    @PostMapping("/notify")
    public Mono<ResponseEntity<List<Notification>>> createNotifications(
        @RequestParam String content,
        @RequestParam NotificationType type,
        @RequestParam(required = false) String entityId,
        @RequestParam(required = false) String action,
        @RequestParam String[] targetLogins
    ) {
        LOG.debug("REST request to create notifications for targets: {}", targetLogins);

        // Si targetIds contient "all", envoyer à tous les utilisateurs
        if (targetLogins != null && targetLogins.length == 1 && "all".equalsIgnoreCase(targetLogins[0])) {
            return notificationService
                .createNotifications(content, type, entityId, action, new String[0])
                .collectList()
                .map(notifications -> ResponseEntity.status(HttpStatus.CREATED).body(notifications));
        }

        // Sinon, envoyer aux utilisateurs spécifiés
        return notificationService
            .createNotifications(content, type, entityId, action, targetLogins)
            .collectList()
            .map(notifications -> ResponseEntity.status(HttpStatus.CREATED).body(notifications));
    }
}
