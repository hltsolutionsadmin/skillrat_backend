package com.hlt.usermanagement.event;

import com.hlt.usermanagement.dto.enums.RewardEventType;

public class RewardEvent {
    private Long userId;
    private String userType; // STUDENT, EMPLOYEE, COMPANY
    private RewardEventType eventType;
    private Long refId;       // e.g. campaignId, profileId
    private String description;

    public RewardEvent(Long userId, String userType, RewardEventType eventType, Long refId, String description) {
        this.userId = userId;
        this.userType = userType;
        this.eventType = eventType;
        this.refId = refId;
        this.description = description;
    }

    public Long getUserId() { return userId; }
    public String getUserType() { return userType; }
    public RewardEventType getEventType() { return eventType; }
    public Long getRefId() { return refId; }
    public String getDescription() { return description; }
}
