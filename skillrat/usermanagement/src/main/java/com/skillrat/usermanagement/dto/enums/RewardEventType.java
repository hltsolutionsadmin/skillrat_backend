package com.skillrat.usermanagement.dto.enums;

public enum RewardEventType {
    USER_CREATED(10),
    PROFILE_UPDATED(10),
    CAMPAIGN_CREATED(20),
    EXAM_COMPLETED(50),
    EXPERIENCE_UPDATED(50),   // when user updates total experience
    JOB_APPLIED(10);          // when user applies for a job

    private final int points;

    RewardEventType(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
