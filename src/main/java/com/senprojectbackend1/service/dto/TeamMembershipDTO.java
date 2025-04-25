package com.senprojectbackend1.service.dto;

import java.io.Serializable;
import java.time.Instant;
import java.util.Objects;

public class TeamMembershipDTO implements Serializable {

    private Long teamId;
    private String userId;
    private String status; // PENDING, ACCEPTED, REJECTED
    private Instant invitedAt;
    private Instant respondedAt;
    private String teamName;
    private String userLogin;
    private String role;

    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getStatus() {
        return status;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Instant getInvitedAt() {
        return invitedAt;
    }

    public void setInvitedAt(Instant invitedAt) {
        this.invitedAt = invitedAt;
    }

    public Instant getRespondedAt() {
        return respondedAt;
    }

    public void setRespondedAt(Instant respondedAt) {
        this.respondedAt = respondedAt;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public String getUserLogin() {
        return userLogin;
    }

    public void setUserLogin(String userLogin) {
        this.userLogin = userLogin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TeamMembershipDTO teamMembershipDTO = (TeamMembershipDTO) o;
        if (teamMembershipDTO.getTeamId() == null || getTeamId() == null) {
            return false;
        }
        return Objects.equals(getTeamId(), teamMembershipDTO.getTeamId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTeamId());
    }

    @Override
    public String toString() {
        return (
            "TeamMembershipDTO{" +
            "teamId=" +
            getTeamId() +
            ", userId='" +
            getUserId() +
            "'" +
            ", role='" +
            getRole() +
            "'" +
            ", status='" +
            getStatus() +
            "'" +
            ", invitedAt='" +
            getInvitedAt() +
            "'" +
            ", respondedAt='" +
            getRespondedAt() +
            "'" +
            "}"
        );
    }
}
