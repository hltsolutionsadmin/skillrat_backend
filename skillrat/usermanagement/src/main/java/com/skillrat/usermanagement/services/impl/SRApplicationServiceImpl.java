package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.dto.enums.ApplicationStatus;
import com.skillrat.usermanagement.model.*;
import com.skillrat.usermanagement.populator.ApplicationPopulator;
import com.skillrat.usermanagement.repository.ApplicationRepository;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SRRequirementRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRApplicationService;
import com.skillrat.utils.SecurityUtils;
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
    private final UserRepository userRepository;
    private final ApplicationPopulator applicationPopulator;

    @Override
    @Transactional
    public ApplicationDTO createApplication(ApplicationDTO applicationDTO) {

        UserModel currentUser = fetchCurrentUser();
        B2BUnitModel b2bUnit = fetchAndValidateBusiness(applicationDTO.getB2bUnitId(), currentUser);

        RequirementDTO reqDTO = applicationDTO.getRequirement();
        RequirementModel requirement;

        if (reqDTO.getId() != null) {
            requirement = srRequirementRepository.findById(reqDTO.getId())
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));
        } else {
            // Create new requirement
            requirement = buildRequirementFromDTO(reqDTO, b2bUnit, currentUser);
            requirement = srRequirementRepository.save(requirement);
        }

        ApplicationModel application = buildApplication(applicationDTO, currentUser, b2bUnit, requirement);
        ApplicationModel saved = applicationRepository.save(application);

        ApplicationDTO result = new ApplicationDTO();
        applicationPopulator.populate(saved, result);

        return result;
    }


    private UserModel fetchCurrentUser() {
        UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
        return userRepository.findById(userDetails.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    private B2BUnitModel fetchAndValidateBusiness(Long b2bUnitId, UserModel currentUser) {
        B2BUnitModel b2bUnit = b2bUnitRepository.findById(b2bUnitId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));

//    if (!b2bUnit.getAdmin().getId().equals(currentUser.getId())) {
//        throw new HltCustomerException(ErrorCode.APPLICATION_UNAUTHORIZED_ACCESS);
//    }
        return b2bUnit;
    }

    private RequirementModel buildRequirementFromDTO(RequirementDTO reqDTO, B2BUnitModel b2bUnit, UserModel currentUser) {
        RequirementModel requirement = new RequirementModel();
        requirement.setTitle(reqDTO.getTitle());
        requirement.setDesignation(reqDTO.getDesignation());
        requirement.setDescription(reqDTO.getDescription());
        requirement.setType(reqDTO.getType());
        requirement.setLocation(reqDTO.getLocation());
        requirement.setIsActive(reqDTO.getIsActive());
        requirement.setB2bUnit(b2bUnit);
        requirement.setCreatedBy(currentUser);
        requirement.setStartDate(reqDTO.getStartDate());
        requirement.setEndDate(reqDTO.getEndDate());
        return requirement;
    }

    private ApplicationModel buildApplication(ApplicationDTO dto, UserModel currentUser,
                                              B2BUnitModel b2bUnit, RequirementModel requirement) {
        ApplicationModel application = new ApplicationModel();
        application.setB2bUnit(b2bUnit);
        application.setRequirement(requirement);
        application.setApplicant(currentUser);
        application.setStatus(dto.getStatus() != null ? dto.getStatus() : ApplicationStatus.PENDING);
        application.setCoverLetter(dto.getCoverLetter());
        return application;
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

    @Override
    public Page<ApplicationDTO> getApplicationsForStartup(Long b2bUnitId, Pageable pageable) {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        UserModel currentUser = userRepository.findById(loggedInUser.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        validateStartupAccess(currentUser, b2bUnitId);

        return applicationRepository.findByB2bUnit_Id(b2bUnitId, pageable)
                .map(app -> {
                    ApplicationDTO dto = new ApplicationDTO();
                    applicationPopulator.populate(app, dto);
                    return dto;
                });
    }

    private void validateStartupAccess(UserModel currentUser, Long b2bUnitId) {
        boolean isOwner = b2bUnitRepository.existsByIdAndAdmin_Id(b2bUnitId, currentUser.getId());
        if (!isOwner) {
            throw new HltCustomerException(ErrorCode.APPLICATION_UNAUTHORIZED_ACCESS);
        }
    }

}
