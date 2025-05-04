package com.senprojectbackend1.service.dto;

/**
 * DTO pour représenter un tag avec son nombre de projets associés
 */
public class TagWithCountDTO {

    private Long id;
    private String name;
    private long count;

    public TagWithCountDTO() {}

    public TagWithCountDTO(Long id, String name, long count) {
        this.id = id;
        this.name = name;
        this.count = count;
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

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }
}
