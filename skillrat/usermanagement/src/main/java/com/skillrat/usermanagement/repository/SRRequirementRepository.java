package com.skillrat.usermanagement.repository;

import com.skillrat.usermanagement.dto.enums.RequirementType;
import com.skillrat.usermanagement.model.RequirementModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SRRequirementRepository extends JpaRepository<RequirementModel, Long> {

    Page<RequirementModel> findByB2bUnit(Long b2bUnitId, Pageable pageable);

    Page<RequirementModel> findByB2bUnit_Id(Long b2bUnitId, Pageable pageable);

    Page<RequirementModel> findByB2bUnit_IdAndType(Long b2bUnitId, RequirementType type, Pageable pageable);

}