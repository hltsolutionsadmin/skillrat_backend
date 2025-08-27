package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.dto.enums.RewardEventType;
import com.skillrat.usermanagement.model.RewardTransactionModel;

public interface RewardService {

    /**
     * Add reward points to a user asynchronously.
     */
    void addPoints(Long userId, String userType, int points,
                   RewardEventType eventType, Long refId, String description);

    /**
     * Deduct reward points from a user asynchronously.
     */
    void deductPoints(Long userId, String userType, int points,
                      RewardEventType eventType, Long refId, String description);

    /**
     * Get total reward points for a user.
     */
    int getTotalPoints(Long userId, String userType);

    /**
     * Save a reward transaction manually.
     */
    RewardTransactionModel saveTransaction(RewardTransactionModel transaction);
}
