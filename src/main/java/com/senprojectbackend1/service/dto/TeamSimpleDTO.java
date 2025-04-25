package com.senprojectbackend1.service.dto;

import java.io.Serializable;
import java.util.Objects;

/**
 * A simplified DTO for the {@link com.senprojectbackend1.domain.Team} entity
 * with only ID and name fields.
 */
public class TeamSimpleDTO implements Serializable {

    private Long id;
    private String name;

    public TeamSimpleDTO() {
        // Empty constructor needed for Jackson
    }

    public TeamSimpleDTO(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TeamSimpleDTO)) {
            return false;
        }

        TeamSimpleDTO that = (TeamSimpleDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    @Override
    public String toString() {
        return "TeamSimpleDTO{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
