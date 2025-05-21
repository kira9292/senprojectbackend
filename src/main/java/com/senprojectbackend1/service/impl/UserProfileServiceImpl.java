package com.senprojectbackend1.service.impl;

import com.senprojectbackend1.config.Constants;
import com.senprojectbackend1.domain.Project;
import com.senprojectbackend1.domain.UserProfile;
import com.senprojectbackend1.domain.criteria.UserProfileCriteria;
import com.senprojectbackend1.repository.*;
import com.senprojectbackend1.security.SecurityUtils;
import com.senprojectbackend1.service.UserProfileService;
import com.senprojectbackend1.service.dto.*;
import com.senprojectbackend1.service.mapper.ProjectMapper;
import com.senprojectbackend1.service.mapper.TeamMapper;
import com.senprojectbackend1.service.mapper.UserProfileMapper;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Implementation for managing {@link com.senprojectbackend1.domain.UserProfile}.
 */
@Service
@Transactional
public class UserProfileServiceImpl implements UserProfileService {

    private static final Logger LOG = LoggerFactory.getLogger(UserProfileServiceImpl.class);

    private final UserProfileRepository userProfileRepository;
    private final UserProfileMapper userProfileMapper;

    private final TeamRepository teamRepository;
    private final TeamMapper teamMapper;

    private final ProjectRepository projectRepository;
    private final ProjectMapper projectMapper;

    private final TransactionalOperator transactionalOperator;
    private final TagRepository tagRepository;

    private final com.senprojectbackend1.service.CloudinaryService cloudinaryService;

    public UserProfileServiceImpl(
        UserProfileRepository userProfileRepository,
        UserProfileMapper userProfileMapper,
        TeamRepository teamRepository,
        TeamMapper teamMapper,
        ProjectRepository projectRepository,
        ProjectMapper projectMapper,
        TransactionalOperator transactionalOperator,
        TagRepository tagRepository,
        com.senprojectbackend1.service.CloudinaryService cloudinaryService
    ) {
        this.userProfileRepository = userProfileRepository;
        this.userProfileMapper = userProfileMapper;
        this.teamRepository = teamRepository;
        this.teamMapper = teamMapper;
        this.projectRepository = projectRepository;
        this.projectMapper = projectMapper;
        this.transactionalOperator = transactionalOperator;
        this.tagRepository = tagRepository;
        this.cloudinaryService = cloudinaryService;
    }

    @Override
    public Mono<UserProfileDTO> save(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to save UserProfile : {}", userProfileDTO);
        return userProfileRepository.save(userProfileMapper.toEntity(userProfileDTO)).map(userProfileMapper::toDto);
    }

    @Override
    public Mono<UserProfileDTO> update(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to update UserProfile : {}", userProfileDTO);
        return userProfileRepository.save(userProfileMapper.toEntity(userProfileDTO).setIsPersisted()).map(userProfileMapper::toDto);
    }

    @Override
    public Mono<UserProfileDTO> partialUpdate(UserProfileDTO userProfileDTO) {
        LOG.debug("Request to partially update UserProfile : {}", userProfileDTO);

        return userProfileRepository
            .findById(userProfileDTO.getId())
            .map(existingUserProfile -> {
                userProfileMapper.partialUpdate(existingUserProfile, userProfileDTO);
                existingUserProfile.setIsPersisted();
                return existingUserProfile;
            })
            .flatMap(userProfileRepository::save)
            .map(userProfileMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Flux<UserProfileDTO> findByCriteria(UserProfileCriteria criteria, Pageable pageable) {
        LOG.debug("Request to get all UserProfiles by Criteria");
        return userProfileRepository.findByCriteria(criteria, pageable).map(userProfileMapper::toDto);
    }

    /**
     * Find the count of userProfiles by criteria.
     *
     * @param criteria filtering criteria
     * @return the count of userProfiles
     */
    public Mono<Long> countByCriteria(UserProfileCriteria criteria) {
        LOG.debug("Request to get the count of all UserProfiles by Criteria");
        return userProfileRepository.countByCriteria(criteria);
    }

    public Flux<UserProfileDTO> findAllWithEagerRelationships(Pageable pageable) {
        return userProfileRepository.findAllWithEagerRelationships(pageable).map(userProfileMapper::toDto);
    }

    public Mono<Long> countAll() {
        return userProfileRepository.count();
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<UserProfileDTO> findOne(String id) {
        LOG.debug("Request to get UserProfile : {}", id);
        return userProfileRepository.findOneWithEagerRelationships(id).map(userProfileMapper::toDto);
    }

    @Override
    public Mono<Void> delete(String id) {
        LOG.debug("Request to delete UserProfile : {}", id);
        return userProfileRepository.deleteById(id);
    }

    /**
     * Synchronize user profile with IDP data from token details
     *
     * @param idpDetails the token details from IDP (Keycloak)
     * @return the synchronized UserProfile
     */
    @Override
    public Mono<UserProfileDTO> syncWithIdp(Map<String, Object> idpDetails) {
        LOG.debug("Request to sync UserProfile with IDP details");

        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .flatMap(auth -> {
                Map<String, Object> tokenAttributes;
                if (auth instanceof JwtAuthenticationToken) {
                    tokenAttributes = ((JwtAuthenticationToken) auth).getTokenAttributes();
                } else if (auth instanceof OAuth2AuthenticationToken) {
                    tokenAttributes = ((OAuth2AuthenticationToken) auth).getPrincipal().getAttributes();
                } else {
                    return Mono.error(new IllegalArgumentException("Unsupported authentication type"));
                }

                String login = auth.getName();
                if (login == null || login.isEmpty()) {
                    return Mono.error(new IllegalArgumentException("Login is required in token"));
                }

                return userProfileRepository
                    .findOneByLogin(login)
                    .switchIfEmpty(createNewUserProfile(tokenAttributes))
                    .flatMap(existingUser -> updateUserProfileIfNeeded(existingUser, tokenAttributes))
                    .map(userProfileMapper::toDto)
                    .doOnSuccess(dto -> LOG.info("User profile synchronized successfully: {}", dto.getLogin()))
                    .doOnError(error -> LOG.error("Error synchronizing user profile: {}", error.getMessage()));
            });
    }

    private Mono<UserProfile> createNewUserProfile(Map<String, Object> tokenAttributes) {
        LOG.debug("Creating new user profile from token attributes");

        UserProfile userProfile = new UserProfile();
        userProfile.setId((String) tokenAttributes.get("sub"));
        userProfile.setLogin((String) tokenAttributes.get("preferred_username"));
        userProfile.setEmail((String) tokenAttributes.get("email"));
        userProfile.setFirstName((String) tokenAttributes.get("given_name"));
        userProfile.setLastName((String) tokenAttributes.get("family_name"));
        userProfile.setLangKey(Optional.ofNullable((String) tokenAttributes.get("locale")).orElse("fr"));
        userProfile.setActivated(true);
        userProfile.setCreatedDate(Instant.now());
        userProfile.setCreatedBy(Constants.SYSTEM);
        userProfile.setLastModifiedBy(Constants.SYSTEM);
        userProfile.setLastModifiedDate(Instant.now());

        return userProfileRepository
            .save(userProfile)
            .doOnSuccess(saved -> LOG.info("New user profile created: {}", saved.getLogin()))
            .doOnError(error -> LOG.error("Error creating new user profile: {}", error.getMessage()));
    }

    private Mono<UserProfile> updateUserProfileIfNeeded(UserProfile existingUser, Map<String, Object> tokenAttributes) {
        LOG.debug("Checking if user profile needs update: {}", existingUser.getLogin());

        boolean needsUpdate = false;

        // Créer une copie de l'utilisateur existant
        UserProfile updatedUser = new UserProfile();
        updatedUser.setId(existingUser.getId());
        updatedUser.setLogin(existingUser.getLogin());

        // Récupérer les nouvelles valeurs du token
        String newEmail = (String) tokenAttributes.get("email");
        String newFirstName = (String) tokenAttributes.get("given_name");
        String newLastName = (String) tokenAttributes.get("family_name");
        String newLangKey = Optional.ofNullable((String) tokenAttributes.get("locale")).orElse(existingUser.getLangKey());

        // Vérifier les différences et mettre à jour uniquement les champs qui ont changé
        if (!Objects.equals(existingUser.getEmail(), newEmail)) {
            updatedUser.setEmail(newEmail);
            needsUpdate = true;
        } else {
            updatedUser.setEmail(existingUser.getEmail());
        }

        if (!Objects.equals(existingUser.getFirstName(), newFirstName)) {
            updatedUser.setFirstName(newFirstName);
            needsUpdate = true;
        } else {
            updatedUser.setFirstName(existingUser.getFirstName());
        }

        if (!Objects.equals(existingUser.getLastName(), newLastName)) {
            updatedUser.setLastName(newLastName);
            needsUpdate = true;
        } else {
            updatedUser.setLastName(existingUser.getLastName());
        }

        if (!Objects.equals(existingUser.getLangKey(), newLangKey)) {
            updatedUser.setLangKey(newLangKey);
            needsUpdate = true;
        } else {
            updatedUser.setLangKey(existingUser.getLangKey());
        }

        // Copier les autres champs inchangés
        updatedUser.setActivated(true);
        updatedUser.setCreatedDate(existingUser.getCreatedDate());
        updatedUser.setCreatedBy(existingUser.getCreatedBy());
        updatedUser.setLastModifiedBy(Constants.SYSTEM);
        updatedUser.setLastModifiedDate(Instant.now());
        updatedUser.setImageUrl(existingUser.getImageUrl());
        updatedUser.setProfileLink(existingUser.getProfileLink());
        updatedUser.setBiography(existingUser.getBiography());
        updatedUser.setJob(existingUser.getJob());
        updatedUser.setBirthDate(existingUser.getBirthDate());
        updatedUser.setSexe(existingUser.getSexe());

        if (needsUpdate) {
            LOG.info("Updating user profile: {}", existingUser.getLogin());
            return userProfileRepository
                .update(updatedUser)
                .thenReturn(updatedUser)
                .doOnSuccess(saved -> LOG.info("User profile updated successfully: {}", saved.getLogin()))
                .doOnError(error -> LOG.error("Error updating user profile: {}", error.getMessage()));
        }

        LOG.debug("No updates needed for user profile: {}", existingUser.getLogin());
        return Mono.just(existingUser);
    }

    /**
     * Get the complete profile of the current authenticated user.
     *
     * @return the entity
     */
    @Override
    @Transactional(readOnly = true)
    public Mono<UserProfileDTO> getCurrentUserProfileComplete() {
        LOG.debug("Request to get current user's complete profile");

        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .map(Authentication::getName)
            .flatMap(this::getUserProfileCompleteByLogin);
    }

    /**
     * Get the complete profile of a user by their login.
     */
    @Override
    @Transactional(readOnly = true)
    public Mono<UserProfileDTO> getUserProfileCompleteByLogin(String login) {
        LOG.debug("Request to get complete profile for login: {}", login);
        return userProfileRepository.findOneByLogin(login).flatMap(this::buildCompleteUserProfileDTO);
    }

    @Override
    public Mono<UserProfileSimpleDTO> getUserProfileSimpleByLogin(String login) {
        LOG.debug("Request to get complete profile for login: {}", login);
        return userProfileRepository.findOneByLogin(login).flatMap(this::buildCompleteUserProfileSimpleDTO);
    }

    /**
     * Constructs the complete UserProfileDTO from a UserProfile entity
     */
    private Mono<UserProfileDTO> buildCompleteUserProfileDTO(UserProfile userProfile) {
        LOG.debug("Building complete profile DTO for login: {}", userProfile.getLogin());
        UserProfileDTO dto = new UserProfileDTO();

        // Mapper le profil utilisateur de base
        userProfileMapper.userProfileToUserProfileDTO(userProfile, dto);

        // 1. Récupérer les équipes de l'utilisateur
        Mono<Set<TeamSimpleDTO>> teamsMono = getTeamsForUser(userProfile.getId());

        // 2. Récupérer les projets de l'utilisateur
        Mono<Set<ProjectDTO>> userProjectsMono = getProjectsForUser(userProfile.getId());

        // 3. Récupérer les projets favoris avec leurs détails
        Mono<Set<ProjectDTO>> favoriteProjectsMono = getFavoriteProjectsForUser(userProfile.getId());

        // 4. Fusionner les résultats et construire le DTO final
        return Mono.zip(userProjectsMono, favoriteProjectsMono, teamsMono).map(tuple -> {
            dto.setProjects(tuple.getT1());
            dto.setFavoriteProjects(tuple.getT2());
            dto.setTeams(tuple.getT3());
            LOG.debug("User profile complete DTO created for login: {}", userProfile.getLogin());
            return dto;
        });
    }

    private Mono<UserProfileSimpleDTO> buildCompleteUserProfileSimpleDTO(UserProfile userProfile) {
        LOG.debug("Building complete profile DTO for login: {}", userProfile.getLogin());
        UserProfileSimpleDTO dto = new UserProfileSimpleDTO();

        // Mapper le profil utilisateur de base
        userProfileMapper.userProfileToUserProfileSimpleDTO(userProfile, dto);

        // 1. Récupérer les équipes de l'utilisateur
        Mono<Set<TeamSimpleDTO>> teamsMono = getTeamsForUser(userProfile.getId());

        // 2. Récupérer les projets de l'utilisateur
        Mono<Set<ProjectDTO>> userProjectsMono = getProjectsForUser(userProfile.getId());

        // 3. Récupérer les projets favoris avec leurs détails
        Mono<Set<ProjectDTO>> favoriteProjectsMono = getFavoriteProjectsForUser(userProfile.getId());

        // 4. Fusionner les résultats et construire le DTO final
        return Mono.zip(userProjectsMono, favoriteProjectsMono, teamsMono).map(tuple -> {
            LOG.debug("User profile complete DTO created for login: {}", userProfile.getLogin());
            return dto;
        });
    }

    private Mono<Set<TeamSimpleDTO>> getTeamsForUser(String userId) {
        return teamRepository
            .findByMembers(userId)
            .map(team -> {
                TeamSimpleDTO teamDTO = new TeamSimpleDTO();
                teamDTO.setId(team.getId());
                teamDTO.setName(team.getName());
                return teamDTO;
            })
            .collect(Collectors.toSet())
            .doOnNext(teams -> LOG.debug("User teams found: {}", teams));
    }

    private Mono<Set<ProjectDTO>> getProjectsForUser(String userId) {
        return projectRepository
            .findByTeamMembersIdWithEagerRelationships(userId)
            .flatMap(this::mapProjectWithTeamAndTags)
            .collect(Collectors.toSet())
            .doOnNext(projects -> LOG.debug("User projects found: {}", projects.size()));
    }

    private Mono<Set<ProjectDTO>> getFavoriteProjectsForUser(String userId) {
        return projectRepository
            .findByFavoritedById(userId)
            .flatMap(this::mapProjectWithTeamAndTags) // Même traitement que les projets normaux
            .collect(Collectors.toSet())
            .doOnNext(favorites -> LOG.debug("Favorite projects found: {}", favorites.size()));
    }

    private Mono<ProjectDTO> mapProjectWithTeamAndTags(Project project) {
        ProjectDTO projectDTO = new ProjectDTO();
        projectDTO.setId(project.getId());
        projectDTO.setTitle(project.getTitle());
        projectDTO.setTotalComments(project.getTotalComments());
        projectDTO.setTotalLikes(project.getTotalLikes());
        projectDTO.setTotalShares(project.getTotalShares());
        projectDTO.setTotalViews(project.getTotalViews());
        projectDTO.setTotalFavorites(project.getTotalFavorites());
        projectDTO.setType(project.getType());
        projectDTO.setDescription(project.getDescription());
        projectDTO.setShowcase(project.getShowcase());
        projectDTO.setOpenToCollaboration(project.getOpenToCollaboration());
        projectDTO.setOpenToFunding(project.getOpenToFunding());

        // Récupérer l'équipe du projet
        Mono<ProjectDTO> projectWithTeam = teamRepository
            .findById(project.getTeamId())
            .map(teamMapper::toDto)
            .map(teamDTO -> {
                projectDTO.setTeam(teamDTO);
                return projectDTO;
            });

        // Ajouter les tags au projet
        return projectWithTeam.flatMap(projectDTOWithTeam ->
            tagRepository
                .findByProjectId(project.getId())
                .map(tag -> {
                    TagDTO tagDTO = new TagDTO();
                    tagDTO.setId(tag.getId());
                    tagDTO.setName(tag.getName());
                    return tagDTO;
                })
                .collectList()
                .map(tags -> {
                    projectDTOWithTeam.setTags(new HashSet<>(tags));
                    return projectDTOWithTeam;
                })
        );
    }

    @Override
    public Mono<UserProfileDTO> updateCurrentUserProfile(Map<String, Object> updateDTO) {
        return SecurityUtils.getCurrentUserLogin()
            .flatMap(login -> userProfileRepository.findOneByLogin(login))
            .flatMap(userProfile -> {
                boolean needUpdate = false;
                if (updateDTO.containsKey("firstName")) {
                    userProfile.setFirstName((String) updateDTO.get("firstName"));
                    needUpdate = true;
                }
                if (updateDTO.containsKey("lastName")) {
                    userProfile.setLastName((String) updateDTO.get("lastName"));
                    needUpdate = true;
                }
                if (updateDTO.containsKey("email")) {
                    userProfile.setEmail((String) updateDTO.get("email"));
                    needUpdate = true;
                }
                if (updateDTO.containsKey("langKey")) {
                    userProfile.setLangKey((String) updateDTO.get("langKey"));
                    needUpdate = true;
                }
                if (updateDTO.containsKey("profileLink")) {
                    userProfile.setProfileLink((String) updateDTO.get("profileLink"));
                    needUpdate = true;
                }
                if (updateDTO.containsKey("biography")) {
                    userProfile.setBiography((String) updateDTO.get("biography"));
                    needUpdate = true;
                }
                if (updateDTO.containsKey("birthDate")) {
                    Object birthDateObj = updateDTO.get("birthDate");
                    if (birthDateObj instanceof String) {
                        userProfile.setBirthDate(Instant.parse((String) birthDateObj));
                        needUpdate = true;
                    }
                }
                if (updateDTO.containsKey("job")) {
                    userProfile.setJob((String) updateDTO.get("job"));
                    needUpdate = true;
                }
                if (updateDTO.containsKey("sexe")) {
                    Object sexeObj = updateDTO.get("sexe");
                    if (sexeObj instanceof String) {
                        try {
                            userProfile.setSexe(com.senprojectbackend1.domain.enumeration.Genre.valueOf((String) sexeObj));
                            needUpdate = true;
                        } catch (Exception ignored) {}
                    }
                }
                Mono<UserProfile> imageMono = Mono.just(userProfile);
                if (updateDTO.containsKey("imageUrl")) {
                    String imageUrl = (String) updateDTO.get("imageUrl");
                    if (imageUrl != null && !imageUrl.isBlank() && !imageUrl.startsWith("http")) {
                        imageMono = cloudinaryService
                            .uploadBase64Image(imageUrl, "profile")
                            .map(url -> {
                                userProfile.setImageUrl(url);
                                return userProfile;
                            })
                            .onErrorResume(e -> {
                                userProfile.setImageUrl("");
                                return Mono.just(userProfile);
                            });
                        needUpdate = true;
                    } else if (imageUrl != null && imageUrl.startsWith("http")) {
                        userProfile.setImageUrl(imageUrl);
                        needUpdate = true;
                    }
                }
                if (!needUpdate) {
                    return Mono.just(userProfileMapper.toDto(userProfile));
                }
                return imageMono
                    .flatMap(up -> {
                        up.setLastModifiedDate(Instant.now());
                        up.setLastModifiedBy("self");
                        return userProfileRepository.save(up);
                    })
                    .map(userProfileMapper::toDto);
            });
    }
}
