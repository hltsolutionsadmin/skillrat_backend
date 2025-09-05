package com.skillrat.usermanagement.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.skillrat.usermanagement.services.SRRewardService;

@Slf4j
@Component
@RequiredArgsConstructor
public class RewardEventListener {


    private final SRRewardService SRRewardService;

    @EventListener
    public void handleRewardEvent(RewardEvent event) {
        log.info("Processing reward event: {}", event.getEventType());
        SRRewardService.addPoints(
                event.getUserId(),
                event.getUserType(),
                event.getPoints(),
                event.getEventType(),
                event.getRefId(),
                event.getDescription()
        );
    }


}
