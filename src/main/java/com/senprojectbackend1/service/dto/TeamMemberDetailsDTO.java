package com.senprojectbackend1.service.dto;

import java.io.Serializable;

public class TeamMemberDetailsDTO extends UserProfileSimpleDTO implements Serializable {

    private String status;
    private String role;

    public TeamMemberDetailsDTO() {}

    public TeamMemberDetailsDTO(UserProfileSimpleDTO user, String status, String role) {
        if (user != null) {
            this.setId(user.getId());
            this.setLogin(user.getLogin());
            this.setFirstName(user.getFirstName());
            this.setLastName(user.getLastName());
            this.setEmail(user.getEmail());
            this.setImageUrl(user.getImageUrl());
            this.setActivated(user.getActivated());
            this.setLangKey(user.getLangKey());
            this.setCreatedBy(user.getCreatedBy());
            this.setCreatedDate(user.getCreatedDate());
            this.setLastModifiedBy(user.getLastModifiedBy());
            this.setLastModifiedDate(user.getLastModifiedDate());
            this.setProfileLink(user.getProfileLink());
            this.setBiography(user.getBiography());
            this.setBirthDate(user.getBirthDate());
            this.setJob(user.getJob());
            this.setSexe(user.getSexe());
        }
        this.status = status;
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
