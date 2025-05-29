package com.senprojectbackend1.service.exception;

public class ProjectBusinessException extends RuntimeException {

    private String entity;
    private String errorKey;

    public ProjectBusinessException(String message) {
        super(message);
    }

    public ProjectBusinessException(String message, String entity, String errorKey) {
        super(message);
        this.entity = entity;
        this.errorKey = errorKey;
    }

    public String getEntity() {
        return entity;
    }

    public String getErrorKey() {
        return errorKey;
    }
}
