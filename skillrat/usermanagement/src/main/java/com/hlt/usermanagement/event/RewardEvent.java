package com.hlt.usermanagement.event;

import com.hlt.usermanagement.dto.enums.RewardEventType;

public class RewardEvent {
    private final Long userId;
    private final String userType;
    private final int points;
    private final RewardEventType eventType;
    private final Long refId;
    private final String description;

    public RewardEvent(Long userId, String userType, int points,
                       RewardEventType eventType, Long refId, String description) {
        this.userId = userId;
        this.userType = userType;
        this.points = points;
        this.eventType = eventType;
        this.refId = refId;
        this.description = description;
    }

    public Long getUserId() { return userId; }
    public String getUserType() { return userType; }
    public int getPoints() { return points; }
    public RewardEventType getEventType() { return eventType; }
    public Long getRefId() { return refId; }
    public String getDescription() { return description; }
}
