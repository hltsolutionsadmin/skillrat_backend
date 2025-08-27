package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.enums.RewardEventType;
import com.skillrat.usermanagement.model.RewardPointModel;
import com.skillrat.usermanagement.model.RewardTransactionModel;
import com.skillrat.usermanagement.repository.RewardPointRepository;
import com.skillrat.usermanagement.repository.RewardTransactionRepository;
import com.skillrat.usermanagement.services.RewardService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class RewardServiceImpl implements RewardService {

    private final RewardPointRepository rewardPointRepo;
    private final RewardTransactionRepository rewardTxnRepo;

    @Override
    @Async
    @Transactional
    public void addPoints(Long userId, String userType, int points,
                          RewardEventType eventType, Long refId, String description) {
        try {
            RewardPointModel rewardPoint = rewardPointRepo.findByUserIdAndUserType(userId, userType)
                    .orElseGet(() -> {
                        RewardPointModel rp = new RewardPointModel();
                        rp.setUserId(userId);
                        rp.setUserType(userType);
                        rp.setTotalPoints(0);
                        return rp;
                    });

            int oldPoints = rewardPoint.getTotalPoints();
            rewardPoint.setTotalPoints(oldPoints + points);
            rewardPoint.setLastUpdated(LocalDateTime.now());
            rewardPointRepo.save(rewardPoint);

            saveTransactionRecord(userId, userType, points, "CREDIT", eventType, refId, description);
            log.info("Transaction record saved for userId={}, points={}", userId, points);

        } catch (Exception e) {
            log.error("Failed to add points for userId={} due to {}", userId, e.getMessage(), e);
            throw e;
        }
    }


    @Override
    @Async
    @Transactional
    public void deductPoints(Long userId, String userType, int points,
                             RewardEventType eventType, Long refId, String description) {

        RewardPointModel rewardPoint = rewardPointRepo.findByUserIdAndUserType(userId, userType)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REWARD_NOT_FOUND,
                        "User reward balance not found for userId: " + userId));

        if (rewardPoint.getTotalPoints() < points) {
            throw new HltCustomerException(ErrorCode.INSUFFICIENT_POINTS,
                    "Not enough points to deduct for userId: " + userId);
        }

        rewardPoint.setTotalPoints(rewardPoint.getTotalPoints() - points);
        rewardPoint.setLastUpdated(LocalDateTime.now());
        rewardPointRepo.save(rewardPoint);

        saveTransactionRecord(userId, userType, points, "DEBIT", eventType, refId, description);
    }

    @Override
    public int getTotalPoints(Long userId, String userType) {
        return rewardPointRepo.findByUserIdAndUserType(userId, userType)
                .map(RewardPointModel::getTotalPoints)
                .orElse(0);
    }

    @Override
    public RewardTransactionModel saveTransaction(RewardTransactionModel transaction) {
        return rewardTxnRepo.save(transaction);
    }

    private void saveTransactionRecord(Long userId, String userType, int points, String type,
                                       RewardEventType eventType, Long refId, String description) {
        RewardTransactionModel txn = new RewardTransactionModel();
        txn.setUserId(userId);
        txn.setUserType(userType);
        txn.setPoints(points);
        txn.setTransactionType(type);
        txn.setEventType(eventType);
        txn.setEventRefId(refId);
        txn.setDescription(description);
        txn.setTransactionDate(LocalDateTime.now());
        rewardTxnRepo.save(txn);
    }
}
