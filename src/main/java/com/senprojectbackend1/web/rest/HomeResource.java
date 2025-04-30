package com.senprojectbackend1.web.rest;

import com.senprojectbackend1.service.ProjectService;
import com.senprojectbackend1.service.dto.PageDTO;
import com.senprojectbackend1.service.dto.ProjectDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller pour la pagination des projets.
 */
@RestController
@RequestMapping("/api")
public class HomeResource {

    private final Logger log = LoggerFactory.getLogger(HomeResource.class);
    private final ProjectService projectService;

    public HomeResource(ProjectService projectService) {
        this.projectService = projectService;
    }

    /**
     * {@code GET /projects/paginated} : Récupère une page de projets.
     *
     * @param page numéro de la page (commence à 0)
     * @param size taille de la page (nombre d'éléments par page, par défaut à 3)
     * @return la réponse avec statut {@code 200 (OK)} et la liste des projets dans le corps
     */
    @GetMapping("/projects/paginated")
    public Mono<ResponseEntity<PageDTO<ProjectDTO>>> getPaginatedProjects(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "3") int size,
        @RequestParam(required = false) String category
    ) {
        log.debug("REST request to get paginated Projects - page: {}, size: {}, category: {}", page, size, category);

        return projectService.getPaginatedProjects(page, size, category).map(ResponseEntity::ok);
    }

    /**
     * {@code GET /projects/popular} : Récupère les 10 projets les plus populaires.
     *
     * @return la réponse avec statut {@code 200 (OK)} et la liste des projets populaires dans le corps
     */
    @GetMapping("/projects/popular")
    public Mono<ResponseEntity<Flux<ProjectDTO>>> getTop10PopularProjects() {
        log.debug("REST request to get top 10 popular projects");
        return Mono.just(ResponseEntity.ok(projectService.getTop10PopularProjects()));
    }
}
