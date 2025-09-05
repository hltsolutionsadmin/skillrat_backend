package com.skillrat.usermanagement.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.skillrat.usermanagement.model.RewardPointModel;

import java.util.Optional;

public interface SRRewardPointRepository extends JpaRepository<RewardPointModel, Long> {

    Optional<RewardPointModel> findByUserIdAndUserType(Long userId, String userType);
}
