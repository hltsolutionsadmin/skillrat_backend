package com.skillrat.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillrat.usermanagement.model.RewardTransactionModel;

import java.util.List;

public interface RewardTransactionRepository extends JpaRepository<RewardTransactionModel, Long> {

    List<RewardTransactionModel> findByUserId(Long userId);
}
