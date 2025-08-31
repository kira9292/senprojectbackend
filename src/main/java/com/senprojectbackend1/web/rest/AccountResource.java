package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.service.dto.AdminUserDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing the current user's account.
 */
@RestController
@RequestMapping("/api")
public class AccountResource {

    private static final Logger logger = LoggerFactory.getLogger(AccountResource.class);

    /**
     * GET /api/account : get the current user account information.
     * 
     * Cette méthode vérifie si un token JWT valide est présent.
     * Si OUI: retourne les informations utilisateur depuis le JWT
     * Si NON: Spring Security retourne automatiquement 401 Unauthorized
     *
     * @return the current user account information.
     */
    @GetMapping("/account")
    public Mono<ResponseEntity<AdminUserDTO>> getAccount() {
        return ReactiveSecurityContextHolder
            .getContext()
            .cast(org.springframework.security.core.context.SecurityContext.class)
            .map(org.springframework.security.core.context.SecurityContext::getAuthentication)
            .cast(JwtAuthenticationToken.class)
            .map(jwtToken -> {
                Jwt jwt = jwtToken.getToken();
                
                // Log pour debugging
                logger.debug("JWT Token found for user: {}", jwt.getClaimAsString("preferred_username"));
                logger.debug("JWT Claims: {}", jwt.getClaims());
                
                // Créer DTO utilisateur depuis le JWT
                AdminUserDTO userDTO = new AdminUserDTO();
                
                // Extraire les informations du JWT
                userDTO.setLogin(jwt.getClaimAsString("preferred_username"));
                userDTO.setEmail(jwt.getClaimAsString("email"));
                userDTO.setFirstName(jwt.getClaimAsString("given_name"));
                userDTO.setLastName(jwt.getClaimAsString("family_name"));
                userDTO.setActivated(true);
                userDTO.setLangKey(jwt.getClaimAsString("locale"));
                
                // Extraire les rôles depuis le JWT
                if (jwt.getClaimAsMap("realm_access") != null) {
                    @SuppressWarnings("unchecked")
                    java.util.List<String> roles = (java.util.List<String>) 
                        ((java.util.Map<String, Object>) jwt.getClaimAsMap("realm_access")).get("roles");
                    
                    if (roles != null) {
                        userDTO.setAuthorities(new java.util.HashSet<>(roles));
                    }
                }
                
                logger.info("Account info retrieved for user: {}", userDTO.getLogin());
                return ResponseEntity.ok(userDTO);
            })
            .doOnError(error -> {
                logger.warn("Error retrieving account info: {}", error.getMessage());
            })
            .onErrorReturn(ResponseEntity.status(401).build());
    }

    /**
     * GET /api/authenticate : check if the user is authenticated, and return its login.
     *
     * @param authentication the authentication.
     * @return the login if the user is authenticated.
     */
    @GetMapping("/authenticate")
    public Mono<String> isAuthenticated(Authentication authentication) {
        logger.debug("REST request to check if the current user is authenticated");
        
        return ReactiveSecurityContextHolder
            .getContext()
            .map(org.springframework.security.core.context.SecurityContext::getAuthentication)
            .cast(JwtAuthenticationToken.class)
            .map(jwtToken -> {
                Jwt jwt = jwtToken.getToken();
                String username = jwt.getClaimAsString("preferred_username");
                logger.debug("User authenticated: {}", username);
                return username;
            })
            .switchIfEmpty(Mono.empty())
            .doOnError(error -> {
                logger.debug("User not authenticated: {}", error.getMessage());
            })
            .onErrorReturn("");
    }
}