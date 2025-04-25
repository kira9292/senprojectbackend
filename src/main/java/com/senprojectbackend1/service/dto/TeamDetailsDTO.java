package com.senprojectbackend1.service.dto;

import java.util.HashSet;
import java.util.Set;

public class TeamDetailsDTO extends TeamDTO {

    private Set<ProjectSimple2DTO> projects = new HashSet<>();

    // Constructeur qui copie les données de base de TeamDTO
    public TeamDetailsDTO(TeamDTO team) {
        this.setId(team.getId());
        this.setName(team.getName());
        this.setDescription(team.getDescription());
        this.setLogo(team.getLogo());
        this.setCreatedAt(team.getCreatedAt());
        this.setUpdatedAt(team.getUpdatedAt());
        this.setVisibility(team.getVisibility());
        this.setTotalLikes(team.getTotalLikes());
        this.setIsDeleted(team.getIsDeleted());
        this.setCreatedBy(team.getCreatedBy());
        this.setLastUpdatedBy(team.getLastUpdatedBy());
        this.setMembers(team.getMembers()); // Copie les membres si déjà chargés
    }

    // Getters et setters pour projects
    public Set<ProjectSimple2DTO> getProjects() {
        return projects;
    }

    public void setProjects(Set<ProjectSimple2DTO> projects) {
        this.projects = projects;
    }
}
