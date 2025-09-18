package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.domain.criteria.TeamCriteria;
import com.senprojectbackend1.repository.TeamRepository;
import com.senprojectbackend1.service.TeamService;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.TeamDTO;
import com.senprojectbackend1.service.dto.TeamDetailsDTO;
import com.senprojectbackend1.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.util.ForwardedHeaderUtils;
import reactor.core.publisher.Mono;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.reactive.ResponseUtil;

/**
 * REST controller for managing {@link com.senprojectbackend1.domain.Team}.
 */
@RestController
@RequestMapping("/api/teams")
public class TeamResource {

    private static final Logger LOG = LoggerFactory.getLogger(TeamResource.class);

    private static final String ENTITY_NAME = "senProjectBackend1Team";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final TeamService teamService;

    private final TeamRepository teamRepository;

    private final UserProfileService userProfileService;

    public TeamResource(TeamService teamService, TeamRepository teamRepository, UserProfileService userProfileService) {
        this.teamService = teamService;
        this.teamRepository = teamRepository;
        this.userProfileService = userProfileService;
    }

    /**
     * {@code POST  /teams} : Create a new team.
     *
     * @param teamDTO the teamDTO to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new teamDTO, or with status {@code 400 (Bad Request)} if the team has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public Mono<ResponseEntity<TeamDTO>> createTeam(@Valid @RequestBody TeamDTO teamDTO) throws URISyntaxException {
        LOG.debug("REST request to save Team : {}", teamDTO);
        if (teamDTO.getId() != null) {
            throw new BadRequestAlertException("A new team cannot already have an ID", ENTITY_NAME, "idexists");
        }
        LOG.debug("Creating team with name: {}", teamDTO.getName());
        LOG.debug("Creating team with description: {}", teamDTO.getDescription());
        LOG.debug("Creating team with createdAt: {}", teamDTO.getCreatedAt());
        LOG.debug("Creating team with updatedAt: {}", teamDTO.getUpdatedAt());

        if (teamDTO.getName() == null || teamDTO.getName().isEmpty()) {
            throw new BadRequestAlertException("Team name cannot be null or empty", ENTITY_NAME, "nameempty");
        }
        if (teamDTO.getName().length() > 50) {
            throw new BadRequestAlertException("Team name cannot exceed 50 characters", ENTITY_NAME, "nametoolong");
        }
        if (teamDTO.getDescription() != null && teamDTO.getDescription().length() > 255) {
            throw new BadRequestAlertException("Team description cannot exceed 255 characters", ENTITY_NAME, "descriptiontoolong");
        }
        if (teamDTO.getCreatedAt() != null) {
            throw new BadRequestAlertException("Team creation date cannot be set manually", ENTITY_NAME, "createdatset");
        }
        if (teamDTO.getUpdatedAt() != null) {
            throw new BadRequestAlertException("Team update date cannot be set manually", ENTITY_NAME, "updatedatset");
        }

        return teamService
            .save(teamDTO)
            .handle((result, sink) -> {
                try {
                    sink.next(
                        ResponseEntity.created(new URI("/api/teams/" + result.getId()))
                            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
                } catch (URISyntaxException e) {
                    sink.error(new RuntimeException(e));
                }
            });
    }

    /**
     * {@code PUT  /teams/:id} : Updates an existing team.
     *
     * @param id the id of the teamDTO to save.
     * @param teamDTO the teamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamDTO,
     * or with status {@code 400 (Bad Request)} if the teamDTO is not valid,
     * or with status {@code 500 (Internal Server Error)} if the teamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public Mono<ResponseEntity<TeamDTO>> updateTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @Valid @RequestBody TeamDTO teamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to update Team : {}, {}", id, teamDTO);
        if (teamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teamRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                return teamService
                    .update(teamDTO)
                    .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND)))
                    .map(result ->
                        ResponseEntity.ok()
                            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                            .body(result)
                    );
            });
    }

    /**
     * {@code PATCH  /teams/:id} : Partial updates given fields of an existing team, field will ignore if it is null
     *
     * @param id the id of the teamDTO to save.
     * @param teamDTO the teamDTO to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated teamDTO,
     * or with status {@code 400 (Bad Request)} if the teamDTO is not valid,
     * or with status {@code 404 (Not Found)} if the teamDTO is not found,
     * or with status {@code 500 (Internal Server Error)} if the teamDTO couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public Mono<ResponseEntity<TeamDTO>> partialUpdateTeam(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody TeamDTO teamDTO
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Team partially : {}, {}", id, teamDTO);
        if (teamDTO.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, teamDTO.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        return teamRepository
            .existsById(id)
            .flatMap(exists -> {
                if (!exists) {
                    return Mono.error(new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound"));
                }

                Mono<TeamDTO> result = teamService.partialUpdate(teamDTO);

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
     * {@code GET  /teams} : get all the teams.
     *
     * @param pageable the pagination information.
     * @param request a {@link ServerHttpRequest} request.
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of teams in body.
     */
    @GetMapping(value = "", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<ResponseEntity<List<TeamDTO>>> getAllTeams(
        TeamCriteria criteria,
        @org.springdoc.core.annotations.ParameterObject Pageable pageable,
        ServerHttpRequest request
    ) {
        LOG.debug("REST request to get Teams by criteria: {}", criteria);
        return teamService
            .countByCriteria(criteria)
            .zipWith(teamService.findByCriteria(criteria, pageable).collectList())
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
     * {@code GET  /teams/count} : count all the teams.
     *
     * @param criteria the criteria which the requested entities should match.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the count in body.
     */
    @GetMapping("/count")
    public Mono<ResponseEntity<Long>> countTeams(TeamCriteria criteria) {
        LOG.debug("REST request to count Teams by criteria: {}", criteria);
        return teamService.countByCriteria(criteria).map(count -> ResponseEntity.status(HttpStatus.OK).body(count));
    }

    /**
     * {@code GET  /teams/:id} : get the "id" team.
     *
     * @param id the id of the teamDTO to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamDTO, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public Mono<ResponseEntity<TeamDTO>> getTeam(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Team : {}", id);
        Mono<TeamDTO> teamDTO = teamService.findOne(id);
        return ResponseUtil.wrapOrNotFound(teamDTO);
    }

    /**
     * {@code DELETE  /teams/:id} : delete the "id" team.
     *
     * @param id the id of the teamDTO to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> deleteTeam(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Team : {}", id);
        return teamService
            .delete(id)
            .then(
                Mono.just(
                    ResponseEntity.noContent()
                        .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
                        .build()
                )
            );
    }

    @GetMapping("/myteams")
    public Mono<ResponseEntity<List<TeamDTO>>> getMyTeams() {
        LOG.debug("REST request to get teams of current user");

        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName) // => Mono<String> login
            .flatMap(userProfileService::getUserProfileSimpleByLogin) // => Mono<UserProfileSimpleDTO>
            .flatMap(
                user ->
                    teamService
                        .findAllByMemberLogin(user.getLogin()) // => Flux<TeamDTO>
                        .collectList() // Convertit Flux en Mono<List>
            )
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @GetMapping("/myteams/{id}")
    public Mono<ResponseEntity<TeamDetailsDTO>> getMyTeamById(@PathVariable Long id) {
        LOG.debug("REST request to get team details for ID: {}", id);

        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName)
            .flatMap(userProfileService::getUserProfileSimpleByLogin)
            .flatMap(user ->
                teamService
                    .findOneByIdAndMemberLogin(id, user.getLogin())
                    .flatMap(team ->
                        teamService
                            .getMemberRole(id, user.getId())
                            .flatMap(role -> {
                                boolean canSeeAll = "LEAD".equals(role) || "MODIFY".equals(role) || "READ".equals(role);
                                if (canSeeAll) {
                                    return teamService.getTeamProjects(id).collectList();
                                } else {
                                    // Filtrer uniquement les projets published
                                    return teamService
                                        .getTeamProjects(id)
                                        .filter(p -> p.getStatus() != null && p.getStatus().equals("PUBLISHED"))
                                        .collectList();
                                }
                            })
                            .map(projects -> {
                                TeamDetailsDTO detailsDTO = new TeamDetailsDTO(team);
                                detailsDTO.setProjects(new HashSet<>(projects));
                                return ResponseEntity.ok(detailsDTO);
                            })
                    )
            )
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code GET  /teams/project/{id}} : get the team of a project with all members details.
     *
     * @param id the id of the project to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the teamDTO with members, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/project/{id}")
    public Mono<ResponseEntity<TeamDetailsDTO>> getProjectTeam(@PathVariable Long id) {
        LOG.debug("REST request to get team for Project : {}", id);
        return teamService
            .findByProjectId(id)
            .flatMap(team ->
                // Enrichir avec les projets de l'équipe
                teamService
                    .getTeamProjects(team.getId())
                    .collectList()
                    .map(projects -> {
                        TeamDetailsDTO detailsDTO = new TeamDetailsDTO(team);
                        detailsDTO.setProjects(new HashSet<>(projects));
                        return detailsDTO;
                    })
            )
            .map(ResponseEntity::ok)
            .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    /**
     * {@code DELETE  /teams/{id}/delete} : Supprime une équipe et met à jour ses projets si l'utilisateur est un membre accepté.
     *
     * @param id l'ID de l'équipe à supprimer
     * @return le {@link ResponseEntity} avec status {@code 200 (OK)}
     */
    @DeleteMapping("/{id}/delete")
    public Mono<ResponseEntity<Void>> deleteTeamAndUpdateProjects(@PathVariable Long id) {
        LOG.debug("REST request to delete Team and update its projects : {}", id);
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login ->
                userProfileService
                    .getUserProfileSimpleByLogin(login)
                    .flatMap(user ->
                        teamService
                            .getMemberRole(id, user.getId())
                            .flatMap(role -> {
                                if (!"LEAD".equals(role)) {
                                    return Mono.error(
                                        new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul un LEAD peut supprimer l'équipe")
                                    );
                                }
                                return teamService
                                    .deleteTeamAndUpdateProjects(id, login)
                                    .then(Mono.just(ResponseEntity.noContent().build()));
                            })
                    )
            );
    }

    /**
     * {@code POST  /teams/create} : Crée une nouvelle équipe et ajoute des membres à partir de logins.
     *
     * Le créateur de la requête sera ajouté comme membre LEAD avec statut ACCEPTED (sans notification).
     * Les autres logins seront ajoutés comme membres READ avec statut PENDING (et recevront une notification d'invitation).
     *
     * @param teamDTO les infos de l'équipe à créer
     * @param targetLogins les logins des membres à ajouter
     * @return la réponse avec l'équipe créée
     * @throws URISyntaxException si la syntaxe de l'URI de localisation est incorrecte
     */
    @PostMapping("/create")
    public Mono<ResponseEntity<TeamDTO>> createTeamWithMembers(
        @Valid @RequestBody TeamDTO teamDTO,
        @RequestParam List<String> targetLogins
    ) throws URISyntaxException {
        LOG.debug("REST request to create Team with members : {}", targetLogins);
        if (teamDTO.getId() != null) {
            throw new BadRequestAlertException("A new team cannot already have an ID", ENTITY_NAME, "idexists");
        }
        return teamService
            .createTeamWithMembers(teamDTO, targetLogins)
            .map(result -> {
                try {
                    return ResponseEntity.created(new URI("/api/teams/" + result.getId()))
                        .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, result.getId().toString()))
                        .body(result);
                } catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            })
            .onErrorMap(e -> {
                LOG.error("Error creating team: {}", e.getMessage());

                // Gestion spécifique des erreurs de contrainte d'unicité
                String errorMessage = e.getMessage();
                if (errorMessage != null && errorMessage.contains("ux_team__name")) {
                    return new BadRequestAlertException("Une équipe avec ce nom existe déjà", ENTITY_NAME, "nameexists");
                }

                // Autres erreurs de données
                if (
                    errorMessage != null &&
                    (errorMessage.contains("DataIntegrityViolationException") ||
                        errorMessage.contains("ConstraintViolationException") ||
                        errorMessage.contains("Failure during data access"))
                ) {
                    return new BadRequestAlertException(
                        "Erreur lors de la création de l'équipe : données invalides",
                        ENTITY_NAME,
                        "datainvalid"
                    );
                }

                return e;
            });
    }

    /**
     * {@code PATCH  /teams/{teamId}/members/{userId}/role} : Change le rôle d'un membre d'une équipe.
     * Seul un membre avec le rôle "LEAD" peut modifier les rôles, et il doit toujours rester au moins un LEAD.
     */
    @PatchMapping("/{teamId}/members/{userId}/role")
    public Mono<ResponseEntity<Void>> changeMemberRole(
        @PathVariable Long teamId,
        @PathVariable String userId,
        @RequestParam String newRole
    ) {
        // Vérifier que le demandeur est LEAD
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName)
            .flatMap(userProfileService::getUserProfileSimpleByLogin)
            .flatMap(currentUser ->
                teamService
                    .isMember(teamId, currentUser.getId())
                    .flatMap(isMember -> {
                        if (Boolean.FALSE.equals(isMember)) return Mono.error(
                            new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas membre de cette équipe")
                        );
                        // Vérifier le rôle du demandeur
                        return teamService
                            .getMemberRole(teamId, currentUser.getId())
                            .flatMap(role -> {
                                if (!"LEAD".equals(role)) {
                                    return Mono.error(
                                        new ResponseStatusException(HttpStatus.FORBIDDEN, "Seul un LEAD peut modifier les rôles")
                                    );
                                }
                                // Vérifier qu'on ne retire pas le dernier LEAD
                                if ("LEAD".equals(newRole)) {
                                    return teamService
                                        .changeMemberRole(teamId, userId, newRole)
                                        .then(Mono.just(ResponseEntity.<Void>ok().build()));
                                } else {
                                    return teamService
                                        .countLeads(teamId)
                                        .flatMap(countLeads -> {
                                            if (countLeads <= 1 && userId.equals(currentUser.getId())) {
                                                return Mono.error(
                                                    new ResponseStatusException(
                                                        HttpStatus.BAD_REQUEST,
                                                        "Il doit rester au moins un LEAD dans l'équipe"
                                                    )
                                                );
                                            }
                                            return teamService
                                                .changeMemberRole(teamId, userId, newRole)
                                                .then(Mono.just(ResponseEntity.<Void>ok().build()));
                                        });
                                }
                            });
                    })
            );
    }

    /**
     * PATCH /teams/{id}/edit : Met à jour le nom, la description et le logo d'une équipe.
     *
     * Seuls les champs name, description et logo sont modifiables via cet endpoint.
     * Vérifie la validité des champs (nom non vide, longueurs max, etc).
     *
     * @param id l'identifiant de l'équipe à modifier
     * @param teamDTO les champs à mettre à jour (name, description, logo)
     * @return l'équipe mise à jour ou une erreur de validation
     */
    @PatchMapping("/{id}/edit")
    public Mono<ResponseEntity<TeamDTO>> patchTeamInfo(@PathVariable Long id, @RequestBody TeamDTO teamDTO) {
        LOG.debug("REST request to patch Team info : {}", id);
        if (teamDTO.getName() != null) {
            if (teamDTO.getName().isEmpty()) {
                throw new BadRequestAlertException("Le nom de l'équipe ne peut pas être vide", ENTITY_NAME, "nameempty");
            }
            if (teamDTO.getName().length() > 50) {
                throw new BadRequestAlertException("Le nom de l'équipe ne peut pas dépasser 50 caractères", ENTITY_NAME, "nametoolong");
            }
        }
        if (teamDTO.getDescription() != null && teamDTO.getDescription().length() > 255) {
            throw new BadRequestAlertException("La description ne peut pas dépasser 255 caractères", ENTITY_NAME, "descriptiontoolong");
        }

        // Vérifier si l'utilisateur est membre de l'équipe et a le rôle LEAD
        return ReactiveSecurityContextHolder.getContext()
            .map(securityContext -> securityContext.getAuthentication().getName())
            .flatMap(login -> userProfileService.getUserProfileSimpleByLogin(login))
            .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "Profil utilisateur non trouvé")))
            .flatMap(userProfile ->
                teamService
                    .isMember(id, userProfile.getId())
                    .flatMap(isMember -> {
                        if (Boolean.FALSE.equals(isMember)) {
                            return Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN, "Vous n'êtes pas membre de cette équipe"));
                        }
                        return teamService
                            .getMemberRole(id, userProfile.getId())
                            .flatMap(role -> {
                                if (!"LEAD".equals(role)) {
                                    return Mono.error(
                                        new ResponseStatusException(
                                            HttpStatus.FORBIDDEN,
                                            "Seul un LEAD peut modifier les informations de l'équipe"
                                        )
                                    );
                                }
                                // On récupère l'équipe existante pour ne mettre à jour que les champs fournis
                                return teamService
                                    .findOne(id)
                                    .switchIfEmpty(
                                        Mono.error(new BadRequestAlertException("Équipe non trouvée", ENTITY_NAME, "idnotfound"))
                                    )
                                    .flatMap(existing -> {
                                        String name = teamDTO.getName() != null ? teamDTO.getName() : existing.getName();
                                        String description = teamDTO.getDescription() != null
                                            ? teamDTO.getDescription()
                                            : existing.getDescription();
                                        String logo = teamDTO.getLogo() != null ? teamDTO.getLogo() : existing.getLogo();
                                        return teamService.updateTeamInfo(id, name, description, logo);
                                    })
                                    .map(ResponseEntity::ok);
                            });
                    })
            );
    }

    /**
     * {@code GET /teams/check-name} : Vérifie si un nom d'équipe existe déjà.
     *
     * @param name le nom à vérifier
     * @param edit l'ID de l'équipe en cours d'édition (optionnel)
     * @return {@code true} si le nom existe déjà, {@code false} sinon
     */
    @GetMapping("/check-name")
    public Mono<ResponseEntity<Boolean>> checkTeamName(@RequestParam String name, @RequestParam(required = false) Long edit) {
        LOG.debug("REST request to check if Team name exists : {}, edit: {}", name, edit);
        return teamService
            .checkTeamNameExists(name, edit)
            .map(ResponseEntity::ok)
            .onErrorResume(e -> {
                LOG.error("Error checking team name: {}", e.getMessage());
                return Mono.just(ResponseEntity.badRequest().body(false));
            });
    }
}
