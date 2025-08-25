package com.hlt.usermanagement.services;


import com.hlt.usermanagement.model.RewardTransactionModel;

import com.hlt.usermanagement.model.RewardTransactionModel;

public interface RewardService {

    /**
     * Add reward points to a user asynchronously.
     */
    void addPoints(Long userId, String userType, int points, String eventType, Long refId, String description);

    /**
     * Deduct reward points from a user asynchronously.
     */
    void deductPoints(Long userId, String userType, int points, String eventType, Long refId, String description);

    /**
     * Get total reward points for a user.
     */
    int getTotalPoints(Long userId, String userType);

    /**
     * Save a reward transaction manually.
     */
    RewardTransactionModel saveTransaction(RewardTransactionModel transaction);
}

