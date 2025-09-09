package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.model.ApplicationModel;
import com.skillrat.usermanagement.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SRApplicationService {

    ApplicationModel createApplication(ApplicationModel application);

    ApplicationModel updateApplication(ApplicationModel application);

    ApplicationModel getApplicationById(Long id);

    Page<ApplicationModel> getApplicationsByRequirement(Long requirementId, Pageable pageable);

    Page<ApplicationModel> getApplicationsByApplicant(Long applicantUserId, Pageable pageable);

    void deleteApplication(Long id);

    Page<ApplicationModel> getApplicationsForStartup(Long b2bUnitId, UserModel currentUser, Pageable pageable);
}
