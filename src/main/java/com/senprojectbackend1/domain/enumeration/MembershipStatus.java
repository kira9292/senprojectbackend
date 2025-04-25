package com.senprojectbackend1.domain.enumeration;

/**
 * Les statuts possibles pour un membre d'une équipe.
 */
public enum MembershipStatus {
    PENDING("En attente"),
    ACCEPTED("Accepté"),
    REJECTED("Refusé");

    private final String description;

    MembershipStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
