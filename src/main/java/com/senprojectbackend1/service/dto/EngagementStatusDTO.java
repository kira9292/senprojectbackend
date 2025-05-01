package com.senprojectbackend1.service.dto;

public class EngagementStatusDTO {

    private boolean like;
    private boolean share;

    public EngagementStatusDTO() {}

    public EngagementStatusDTO(boolean like, boolean share) {
        this.like = like;
        this.share = share;
    }

    public boolean isLike() {
        return like;
    }

    public void setLike(boolean like) {
        this.like = like;
    }

    public boolean isShare() {
        return share;
    }

    public void setShare(boolean share) {
        this.share = share;
    }
}
