package com.hlt.usermanagement.repository;

import com.hlt.usermanagement.model.RewardPointModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RewardPointRepository extends JpaRepository<RewardPointModel, Long> {

    Optional<RewardPointModel> findByUserIdAndUserType(Long userId, String userType);
}
