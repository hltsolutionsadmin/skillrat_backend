package com.hlt.usermanagement.repository;


import com.hlt.usermanagement.dto.enums.ApplicationStatus;
import com.hlt.usermanagement.model.ApplicationModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRepository extends JpaRepository<ApplicationModel, Long> {

    Page<ApplicationModel> findByRequirementId(Long requirementId, Pageable pageable);

    Page<ApplicationModel> findByStudentId(Long studentId, Pageable pageable);

    Page<ApplicationModel> findByStudentIdAndStatus(Long studentId, ApplicationStatus status, Pageable pageable);
}
