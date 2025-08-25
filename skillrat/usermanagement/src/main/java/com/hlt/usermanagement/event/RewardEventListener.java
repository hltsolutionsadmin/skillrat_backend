package com.hlt.usermanagement.event;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class RewardEventListener {

    private final RewardService rewardService;

    public RewardEventListener(RewardService rewardService) {
        this.rewardService = rewardService;
    }

    @Async
    @EventListener
    public void handleRewardEvent(RewardEvent event) {
        rewardService.awardPoints(event);
    }
}
