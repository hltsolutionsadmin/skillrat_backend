package com.hlt.usermanagement.services.impl;

import com.hlt.usermanagement.model.RewardPointModel;
import com.hlt.usermanagement.model.RewardTransactionModel;
import com.hlt.usermanagement.repository.RewardPointRepository;
import com.hlt.usermanagement.repository.RewardTransactionRepository;
import com.hlt.usermanagement.services.RewardService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RewardServiceImpl implements RewardService {

    private final RewardPointRepository rewardPointRepo;
    private final RewardTransactionRepository rewardTxnRepo;

    @Override
    @Async
    @Transactional
    public void addPoints(Long userId, String userType, int points, String eventType, Long refId, String description) {

        // Update or create reward balance
        RewardPointModel rewardPoint = rewardPointRepo.findByUserIdAndUserType(userId, userType)
                .orElseGet(() -> {
                    RewardPointModel rp = new RewardPointModel();
                    rp.setUserId(userId);
                    rp.setUserType(userType);
                    rp.setTotalPoints(0);
                    return rp;
                });

        rewardPoint.setTotalPoints(rewardPoint.getTotalPoints() + points);
        rewardPoint.setLastUpdated(LocalDateTime.now());
        rewardPointRepo.save(rewardPoint);

        // Save transaction
        saveTransactionRecord(userId, userType, points, "CREDIT", eventType, refId, description);
    }

    @Override
    @Async
    @Transactional
    public void deductPoints(Long userId, String userType, int points, String eventType, Long refId, String description) {

        RewardPointModel rewardPoint = rewardPointRepo.findByUserIdAndUserType(userId, userType)
                .orElseThrow(() -> new RuntimeException("User reward balance not found"));

        if (rewardPoint.getTotalPoints() < points) {
            throw new RuntimeException("Not enough points to deduct");
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
                                       String eventType, Long refId, String description) {
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
