package com.hlt.usermanagement.dto.enums;

public enum RewardEventType {
    USER_CREATED(10),
    PROFILE_UPDATED(10),
    CAMPAIGN_CREATED(20),
    EXAM_COMPLETED(50);

    private final int points;

    RewardEventType(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
