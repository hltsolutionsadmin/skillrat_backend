package com.hlt.usermanagement.repository;

import com.hlt.usermanagement.model.RequirementModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequirementRepository extends JpaRepository<RequirementModel, Long> {

    Page<RequirementModel> findByB2BUnitId(Long b2bUnitId, Pageable pageable);
}
