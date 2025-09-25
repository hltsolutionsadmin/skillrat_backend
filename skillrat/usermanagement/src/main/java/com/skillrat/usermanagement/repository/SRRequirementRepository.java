package com.skillrat.usermanagement.repository;

import com.google.common.io.Files;
import com.skillrat.usermanagement.dto.enums.RequirementType;
import com.skillrat.usermanagement.model.RequirementModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SRRequirementRepository extends JpaRepository<RequirementModel, Long> {

    Page<RequirementModel> findByB2bUnit(Long b2bUnitId, Pageable pageable);

    Page<RequirementModel> findByB2bUnit_Id(Long b2bUnitId, Pageable pageable);

    Page<RequirementModel> findByB2bUnit_IdAndType(Long b2bUnitId, RequirementType type, Pageable pageable);

    @Query("""
    SELECT r FROM RequirementModel r
    WHERE (:type IS NULL OR r.type = :type)
""")
    Page<RequirementModel> findByTypeOptional(@Param("type") RequirementType type, Pageable pageable);

}