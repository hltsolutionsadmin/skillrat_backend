package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.ApplicationDTO;

import com.skillrat.usermanagement.model.ApplicationModel;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.RequirementModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.ApplicationPopulator;
import com.skillrat.usermanagement.repository.ApplicationRepository;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SRRequirementRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class SRSRApplicationServiceImpl implements SRApplicationService {

    private final ApplicationRepository applicationRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final SRRequirementRepository SRRequirementRepository;
    private final UserRepository userRepository;
    private final ApplicationPopulator applicationPopulator;

    @Override
    public ApplicationDTO createApplication(ApplicationDTO applicationDTO) {
        ApplicationModel application = new ApplicationModel();

        B2BUnitModel b2bUnit = b2bUnitRepository.findById(applicationDTO.getB2bUnitId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));

        RequirementModel requirement = SRRequirementRepository.findById(applicationDTO.getRequirementId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        UserModel applicant = userRepository.findById(applicationDTO.getApplicantUserId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        application.setB2bUnit(b2bUnit);
        application.setRequirement(requirement);
        application.setApplicant(applicant);
        application.setStatus(applicationDTO.getStatus());
        application.setCoverLetter(applicationDTO.getCoverLetter());

        ApplicationModel saved = applicationRepository.save(application);

        ApplicationDTO dto = new ApplicationDTO();
        applicationPopulator.populate(saved, dto);
        return dto;
    }

    @Override
    public ApplicationDTO updateApplication(Long id, ApplicationDTO applicationDTO) {
        ApplicationModel application = applicationRepository.findById(id)
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.APPLICATION_NOT_FOUND));

        application.setStatus(applicationDTO.getStatus());
        application.setCoverLetter(applicationDTO.getCoverLetter());

        ApplicationModel updated = applicationRepository.save(application);

        ApplicationDTO dto = new ApplicationDTO();
        applicationPopulator.populate(updated, dto);
        return dto;
    }

    @Override
    public ApplicationDTO getApplicationById(Long id) {
        ApplicationModel application = applicationRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.APPLICATION_NOT_FOUND));

        ApplicationDTO dto = new ApplicationDTO();
        applicationPopulator.populate(application, dto);
        return dto;
    }

    @Override
    public Page<ApplicationDTO> getApplicationsByRequirement(Long requirementId, Pageable pageable) {
        return applicationRepository.findByRequirement_Id(requirementId, pageable)
                .map(app -> {
                    ApplicationDTO dto = new ApplicationDTO();
                    applicationPopulator.populate(app, dto);
                    return dto;
                });
    }

    @Override
    public Page<ApplicationDTO> getApplicationsByApplicant(Long applicantUserId, Pageable pageable) {
        return applicationRepository.findByApplicant_Id(applicantUserId, pageable)
                .map(app -> {
                    ApplicationDTO dto = new ApplicationDTO();
                    applicationPopulator.populate(app, dto);
                    return dto;
                });
    }

    @Override
    public void deleteApplication(Long id) {
        ApplicationModel application = applicationRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.APPLICATION_NOT_FOUND));
        applicationRepository.delete(application);
    }
}
