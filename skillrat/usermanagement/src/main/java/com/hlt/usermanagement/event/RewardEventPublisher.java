package com.hlt.usermanagement.event;

import com.hlt.usermanagement.dto.enums.RewardEventType;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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
