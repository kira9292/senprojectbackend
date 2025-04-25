package com.senprojectbackend1.domain;

import java.time.Instant;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("rel_team__members")
public class TeamMembership {

    @Column("team_id")
    private Long teamId;

    @Column("members_id")
    private String membersId;

    @Column("status")
    private String status;

    @Column("role")
    private String role;

    @Column("invited_at")
    private Instant invitedAt;

    @Column("responded_at")
    private Instant respondedAt;

    // Constructeur par défaut
    public TeamMembership() {}

    // Constructeur avec paramètres
    public TeamMembership(Long teamId, String membersId) {
        this.teamId = teamId;
        this.membersId = membersId;
    }

    // Getters et setters
    public Long getTeamId() {
        return teamId;
    }

    public void setTeamId(Long teamId) {
        this.teamId = teamId;
    }

    public String getMembersId() {
        return membersId;
    }

    public void setMembersId(String membersId) {
        this.membersId = membersId;
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
}
