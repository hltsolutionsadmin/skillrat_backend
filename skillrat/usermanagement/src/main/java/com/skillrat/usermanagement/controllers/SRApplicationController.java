package com.skillrat.usermanagement.controllers;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.model.ApplicationModel;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.RequirementModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.SRApplicationPopulator;
import com.skillrat.usermanagement.services.SRApplicationService;
import com.skillrat.usermanagement.services.UserService;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SRRequirementRepository;
import com.skillrat.utils.SecurityUtils;
import com.skillrat.utils.SRAppConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class SRApplicationController {

    private final SRApplicationService srApplicationService;
    private final UserService userService;
    private final SRApplicationPopulator SRApplicationPopulator;
    private final SRRequirementRepository srRequirementRepository;
    private final B2BUnitRepository b2bUnitRepository;

    @PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping
    public ResponseEntity<StandardResponse<ApplicationDTO>> createApplication(
            @Valid @RequestBody ApplicationDTO applicationDTO) {

        UserModel currentUser = fetchCurrentUser();

//        if (!Boolean.TRUE.equals(currentUser.getProfileCompleted())) {
//            throw new HltCustomerException(ErrorCode.PROFILE_NOT_COMPLETED);
//        }

        ApplicationModel application = dtoToEntity(applicationDTO, currentUser);

        ApplicationModel saved = srApplicationService.createApplication(application);

        log.info("Application created [appId={}] by user [userId={}]", saved.getId(), currentUser.getId());

        return ResponseEntity.status(201)
                .body(StandardResponse.single(SRAppConstants.APPLICATION_CREATE_SUCCESS, entityToDto(saved)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ApplicationDTO>> getApplicationById(@PathVariable Long id) {
        ApplicationModel app = srApplicationService.getApplicationById(id);
        return ResponseEntity.ok(StandardResponse.single(SRAppConstants.APPLICATION_FETCH_SUCCESS, entityToDto(app)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<ApplicationDTO>> updateApplication(
            @PathVariable Long id,
            @Valid @RequestBody ApplicationDTO applicationDTO) {

        ApplicationModel application = dtoToEntity(applicationDTO, null);
        application.setId(id);

        ApplicationModel updated = srApplicationService.updateApplication(application);

        log.info("Application updated [appId={}] by user [userId={}]", id, fetchCurrentUser().getId());

        return ResponseEntity.ok(StandardResponse.single(SRAppConstants.APPLICATION_UPDATE_SUCCESS, entityToDto(updated)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteApplication(@PathVariable Long id) {
        srApplicationService.deleteApplication(id);
        log.info("Application deleted [appId={}] by user [userId={}]", id, fetchCurrentUser().getId());
        return ResponseEntity.ok(StandardResponse.message(SRAppConstants.APPLICATION_DELETE_SUCCESS));
    }

    @GetMapping("/requirement/{requirementId}")
    public ResponseEntity<StandardResponse<Page<ApplicationDTO>>> getApplicationsByRequirement(
            @PathVariable Long requirementId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ApplicationModel> applications = srApplicationService.getApplicationsByRequirement(requirementId, buildPageable(page, size));

        Page<ApplicationDTO> dtoPage = applications.map(this::entityToDto);

        return ResponseEntity.ok(StandardResponse.page(SRAppConstants.APPLICATION_LIST_SUCCESS, dtoPage));
    }

    @GetMapping("/applicant/{applicantUserId}")
    public ResponseEntity<StandardResponse<Page<ApplicationDTO>>> getApplicationsByApplicant(
            @PathVariable Long applicantUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<ApplicationModel> applications = srApplicationService.getApplicationsByApplicant(applicantUserId, buildPageable(page, size));

        Page<ApplicationDTO> dtoPage = applications.map(this::entityToDto);

        return ResponseEntity.ok(StandardResponse.page(SRAppConstants.APPLICATION_LIST_SUCCESS, dtoPage));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @GetMapping("/startup/{b2bUnitId}/applications")
    public ResponseEntity<StandardResponse<Page<ApplicationDTO>>> getApplicationsForStartup(
            @PathVariable Long b2bUnitId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserModel currentUser = fetchCurrentUser();

        Page<ApplicationModel> applications = srApplicationService.getApplicationsForStartup(b2bUnitId, currentUser, buildPageable(page, size));

        log.info("Fetching applications for startup [unitId={}] by admin [userId={}]", b2bUnitId, currentUser.getId());

        return ResponseEntity.ok(StandardResponse.page(SRAppConstants.APPLICATION_LIST_SUCCESS, applications.map(this::entityToDto)));
    }

    private UserModel fetchCurrentUser() {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        return Optional.ofNullable(userService.findById(loggedInUser.getId()))
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    private Pageable buildPageable(int page, int size) {
        return PageRequest.of(page, size);
    }

    private ApplicationDTO entityToDto(ApplicationModel app) {
        ApplicationDTO dto = new ApplicationDTO();
        SRApplicationPopulator.populate(app, dto);
        return dto;
    }

    private ApplicationModel dtoToEntity(ApplicationDTO dto, UserModel applicant) {
        ApplicationModel app = new ApplicationModel();

        app.setId(dto.getId());
        app.setStatus(dto.getStatus());
        app.setCoverLetter(dto.getCoverLetter());

        if (applicant != null) {
            app.setApplicant(applicant);
        } else if (dto.getApplicantUserId() != null) {
            UserModel foundUser = userService.findById(dto.getApplicantUserId());
            if (foundUser == null) {
                throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);
            }
            app.setApplicant(foundUser);
        } else {
            throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);
        }

        if (dto.getRequirementId() != null) {
            RequirementModel requirement = srRequirementRepository.findById(dto.getRequirementId())
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

            app.setRequirement(requirement);

            if (dto.getB2bUnitId() == null && requirement.getB2bUnit() != null) {
                app.setB2bUnit(requirement.getB2bUnit());
            }
        }


        if (dto.getB2bUnitId() != null) {
            B2BUnitModel b2bUnit = b2bUnitRepository.findById(dto.getB2bUnitId())
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));
            app.setB2bUnit(b2bUnit);
        }

        return app;
    }

}
