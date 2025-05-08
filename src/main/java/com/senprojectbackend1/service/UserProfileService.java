package com.senprojectbackend1.service;

import com.senprojectbackend1.domain.criteria.UserProfileCriteria;
import com.senprojectbackend1.service.dto.UserProfileDTO;
import com.senprojectbackend1.service.dto.UserProfileSimpleDTO;
import java.util.Map;
import org.springframework.data.domain.Pageable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service Interface for managing {@link com.senprojectbackend1.domain.UserProfile}.
 */
public interface UserProfileService {
    /**
     * Save a userProfile.
     *
     * @param userProfileDTO the entity to save.
     * @return the persisted entity.
     */
    Mono<UserProfileDTO> save(UserProfileDTO userProfileDTO);

    /**
     * Updates a userProfile.
     *
     * @param userProfileDTO the entity to update.
     * @return the persisted entity.
     */
    Mono<UserProfileDTO> update(UserProfileDTO userProfileDTO);

    /**
     * Partially updates a userProfile.
     *
     * @param userProfileDTO the entity to update partially.
     * @return the persisted entity.
     */
    Mono<UserProfileDTO> partialUpdate(UserProfileDTO userProfileDTO);

    /**
     * Find userProfiles by criteria.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<UserProfileDTO> findByCriteria(UserProfileCriteria criteria, Pageable pageable);

    /**
     * Find the count of userProfiles by criteria.
     *
     * @param criteria filtering criteria
     * @return the count of userProfiles
     */
    Mono<Long> countByCriteria(UserProfileCriteria criteria);

    /**
     * Get all the userProfiles with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Flux<UserProfileDTO> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Returns the number of userProfiles available.
     *
     * @return the number of entities in the database.
     */
    Mono<Long> countAll();

    /**
     * Get the "id" userProfile.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Mono<UserProfileDTO> findOne(String id);

    /**
     * Delete the "id" userProfile.
     *
     * @param id the id of the entity.
     * @return a Mono to signal the deletion
     */
    Mono<Void> delete(String id);

    /**
     * Synchronize user profile with IDP data from token details
     *
     * @param idpDetails the token details from IDP (Keycloak)
     * @return the synchronized UserProfile
     */
    Mono<UserProfileDTO> syncWithIdp(Map<String, Object> idpDetails);

    /**
     * Get the complete profile of the current authenticated user.
     *
     * @return the entity DTO with all related information
     */
    Mono<UserProfileDTO> getCurrentUserProfileComplete();

    /**
     * Get the complete profile of a user by their login.
     *
     * @param login the login of the user
     * @return the complete user profile
     */
    Mono<UserProfileDTO> getUserProfileCompleteByLogin(String login);

    /**
     * Get a simplified profile of a user by their login.
     *
     * @param login the login of the user
     * @return the simplified user profile
     */
    Mono<UserProfileSimpleDTO> getUserProfileSimpleByLogin(String login);

    Mono<UserProfileDTO> updateCurrentUserProfile(Map<String, Object> updateDTO);
}
