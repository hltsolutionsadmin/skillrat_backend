package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.model.*;
import com.skillrat.usermanagement.repository.*;
import com.skillrat.usermanagement.services.SRApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SRApplicationServiceImpl implements SRApplicationService {

    private final ApplicationRepository applicationRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final SRRequirementRepository srRequirementRepository;

    @Override
    public ApplicationModel createApplication(ApplicationModel app) {
        srRequirementRepository.findById(app.getRequirement().getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        if (applicationRepository.existsByRequirement_IdAndApplicant_Id (
                app.getRequirement().getId(), app.getApplicant().getId())) {
            throw new HltCustomerException(ErrorCode.APPLICATION_ALREADY_EXISTS);
        }

        return applicationRepository.save(app);
    }

    @Override
    public ApplicationModel updateApplication(ApplicationModel app) {
        ApplicationModel existing = applicationRepository.findById(app.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.APPLICATION_NOT_FOUND));

        existing.setStatus(app.getStatus());
        existing.setCoverLetter(app.getCoverLetter());

        return applicationRepository.save(existing);
    }

    @Override
    public ApplicationModel getApplicationById(Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.APPLICATION_NOT_FOUND));
    }

    @Override
    public Page<ApplicationModel> getApplicationsByRequirement(Long requirementId, Pageable pageable) {
        return applicationRepository.findByRequirement_Id(requirementId, pageable);
    }

    @Override
    public Page<ApplicationModel> getApplicationsByApplicant(Long applicantUserId, Pageable pageable) {
        return applicationRepository.findByApplicant_Id(applicantUserId, pageable);
    }

    @Override
    public void deleteApplication(Long id) {
        ApplicationModel app = getApplicationById(id);
        applicationRepository.delete(app);
    }

    @Override
    public Page<ApplicationModel> getApplicationsForStartup(Long b2bUnitId, UserModel currentUser, Pageable pageable) {
        if (!b2bUnitRepository.existsByIdAndAdmin_Id(b2bUnitId, currentUser.getId())) {
            throw new HltCustomerException(ErrorCode.APPLICATION_UNAUTHORIZED_ACCESS);
        }
        return applicationRepository.findByB2bUnit_Id(b2bUnitId, pageable);
    }
}


