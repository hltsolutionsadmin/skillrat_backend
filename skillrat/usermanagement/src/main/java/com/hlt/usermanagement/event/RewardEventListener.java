package com.hlt.usermanagement.event;

import com.hlt.usermanagement.services.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardEventListener {


    private final RewardService rewardService;

    @EventListener
    public void handleRewardEvent(RewardEvent event) {
        log.info("Processing reward event: {}", event.getEventType());
        rewardService.addPoints(
                event.getUserId(),
                event.getUserType(),
                event.getPoints(),
                event.getEventType(),
                event.getRefId(),
                event.getDescription()
        );
    }


}
