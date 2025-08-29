package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.dto.ApplicationDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ApplicationService {

    ApplicationDTO createApplication(ApplicationDTO applicationDTO);

    ApplicationDTO updateApplication(Long id, ApplicationDTO applicationDTO);

    ApplicationDTO getApplicationById(Long id);

    Page<ApplicationDTO> getApplicationsByRequirement(Long requirementId, Pageable pageable);

    Page<ApplicationDTO> getApplicationsByApplicant(Long applicantUserId, Pageable pageable);

    void deleteApplication(Long id);
}
