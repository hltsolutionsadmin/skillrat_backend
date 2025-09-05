package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.enums.RewardEventType;
import com.skillrat.usermanagement.model.RewardTransactionModel;
import com.skillrat.usermanagement.services.SRRewardService;
import com.skillrat.utils.SRAppConstants;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rewards")
@RequiredArgsConstructor
public class SRRewardController {

    private final SRRewardService SRRewardService;

    /**
     * Add reward points to a user
     */
    @PostMapping("/add")
    public ResponseEntity<StandardResponse<Void>> addPoints(
            @RequestParam Long userId,
            @RequestParam String userType,
            @RequestParam int points,
            @RequestParam RewardEventType eventType,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) String description) {

        SRRewardService.addPoints(userId, userType, points, eventType, refId, description);
        return ResponseEntity.ok(StandardResponse.message(SRAppConstants.REWARD_POINTS_ADD_SUCCESS));
    }

    /**
     * Deduct reward points from a user
     */
    @PostMapping("/deduct")
    public ResponseEntity<StandardResponse<Void>> deductPoints(
            @RequestParam Long userId,
            @RequestParam String userType,
            @RequestParam int points,
            @RequestParam RewardEventType eventType,
            @RequestParam(required = false) Long refId,
            @RequestParam(required = false) String description) {

        SRRewardService.deductPoints(userId, userType, points, eventType, refId, description);
        return ResponseEntity.ok(StandardResponse.message(SRAppConstants.REWARD_POINTS_DEDUCT_SUCCESS));
    }

    /**
     * Get total reward points of a user
     */
    @GetMapping("/{userId}/total")
    public ResponseEntity<StandardResponse<Integer>> getTotalPoints(
            @PathVariable Long userId,
            @RequestParam String userType) {

        int totalPoints = SRRewardService.getTotalPoints(userId, userType);
        return ResponseEntity.ok(StandardResponse.single(SRAppConstants.REWARD_POINTS_TOTAL_SUCCESS, totalPoints));
    }

    /**
     * Save a reward transaction manually
     */
    @PostMapping("/transaction")
    public ResponseEntity<StandardResponse<RewardTransactionModel>> saveTransaction(
            @RequestBody RewardTransactionModel transaction) {

        RewardTransactionModel saved = SRRewardService.saveTransaction(transaction);
        return ResponseEntity.ok(StandardResponse.single(SRAppConstants.REWARD_TXN_SAVE_SUCCESS, saved));
    }
}
