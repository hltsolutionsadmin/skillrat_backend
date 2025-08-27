package com.skillrat.usermanagement.event;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import com.skillrat.usermanagement.dto.enums.RewardEventType;

@Component
@RequiredArgsConstructor
public class RewardEventPublisher {

    private final ApplicationEventPublisher eventPublisher;

    public void publishRewardEvent(Long userId, String userType,
                                   RewardEventType eventType, Long refId, String description) {
        int points = eventType.getPoints();
        RewardEvent event = new RewardEvent(userId, userType, points, eventType, refId, description);
        eventPublisher.publishEvent(event);
    }
}
