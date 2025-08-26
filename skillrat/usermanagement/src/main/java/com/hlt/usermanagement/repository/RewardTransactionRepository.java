package com.hlt.usermanagement.repository;

import com.hlt.usermanagement.model.RewardTransactionModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RewardTransactionRepository extends JpaRepository<RewardTransactionModel, Long> {

    List<RewardTransactionModel> findByUserId(Long userId);
}
