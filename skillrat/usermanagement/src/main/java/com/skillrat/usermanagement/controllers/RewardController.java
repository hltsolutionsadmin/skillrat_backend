package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.enums.RewardEventType;
import com.skillrat.usermanagement.model.RewardTransactionModel;
import com.skillrat.usermanagement.services.RewardService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/rewards")
@RequiredArgsConstructor
public class RewardController {

    private final RewardService rewardService;

    private static final String MSG_ADD_SUCCESS = "Reward points added successfully";
    private static final String MSG_DEDUCT_SUCCESS = "Reward points deducted successfully";
    private static final String MSG_TOTAL_SUCCESS = "Total reward points fetched successfully";
    private static final String MSG_TXN_SAVE_SUCCESS = "Reward transaction saved successfully";

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

        rewardService.addPoints(userId, userType, points, eventType, refId, description);
        return ResponseEntity.ok(StandardResponse.message(MSG_ADD_SUCCESS));
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

        rewardService.deductPoints(userId, userType, points, eventType, refId, description);
        return ResponseEntity.ok(StandardResponse.message(MSG_DEDUCT_SUCCESS));
    }

    /**
     * Get total reward points of a user
     */
    @GetMapping("/{userId}/total")
    public ResponseEntity<StandardResponse<Integer>> getTotalPoints(
            @PathVariable Long userId,
            @RequestParam String userType) {

        int totalPoints = rewardService.getTotalPoints(userId, userType);
        return ResponseEntity.ok(StandardResponse.single(MSG_TOTAL_SUCCESS, totalPoints));
    }

    /**
     * Save a reward transaction manually
     */
    @PostMapping("/transaction")
    public ResponseEntity<StandardResponse<RewardTransactionModel>> saveTransaction(
            @RequestBody RewardTransactionModel transaction) {

        RewardTransactionModel saved = rewardService.saveTransaction(transaction);
        return ResponseEntity.ok(StandardResponse.single(MSG_TXN_SAVE_SUCCESS, saved));
    }
}
