package com.skillrat.usermanagement.repository;

import com.skillrat.usermanagement.dto.enums.ApplicationStatus;
import com.skillrat.usermanagement.model.ApplicationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ApplicationModel, Long> {

    Page<ApplicationModel> findByRequirement_Id(Long requirementId, Pageable pageable);

    Page<ApplicationModel> findByApplicant_Id(Long applicantUserId, Pageable pageable);

    Page<ApplicationModel> findByApplicant_IdAndStatus(Long applicantUserId, ApplicationStatus status, Pageable pageable);
}
