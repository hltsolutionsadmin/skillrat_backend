package com.skillrat.usermanagement.controllers;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.services.SRApplicationService;
import com.skillrat.usermanagement.services.UserService;
import com.skillrat.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class SRApplicationController {

    private final SRApplicationService srApplicationService;

    private  final UserService userService;

    private static final String MSG_CREATE_SUCCESS = "Application created successfully";
    private static final String MSG_FETCH_SUCCESS = "Application fetched successfully";
    private static final String MSG_LIST_SUCCESS = "Applications listed successfully";
    private static final String MSG_UPDATE_SUCCESS = "Application updated successfully";
    private static final String MSG_DELETE_SUCCESS = "Application deleted successfully";

    /**
     * Create a new application
     */
    @PreAuthorize("hasAnyAuthority('ROLE_STUDENT','ROLE_USER_ADMIN')")
    @PostMapping
    public ResponseEntity<StandardResponse<ApplicationDTO>> createApplication(
            @RequestBody ApplicationDTO applicationDTO) {

        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        UserModel userModel = Optional.ofNullable(userService.findById(loggedInUser.getId()))
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        validateAccess(userModel, loggedInUser);

        ApplicationDTO savedApplication = srApplicationService.createApplication(applicationDTO);
        return ResponseEntity.ok(StandardResponse.single(MSG_CREATE_SUCCESS, savedApplication));
    }


    private void validateAccess(UserModel userModel, UserDetailsImpl loggedInUser) {
        boolean isStudent = loggedInUser.getAuthorities().stream()
                .anyMatch(auth -> "ROLE_STUDENT".equals(auth.getAuthority()));

        if (isStudent && Boolean.FALSE.equals(userModel.getProfileCompleted())) {
            throw new HltCustomerException(ErrorCode.PROFILE_NOT_COMPLETED);
        }
    }



    /**
     * Get application by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ApplicationDTO>> getApplicationById(@PathVariable Long id) {
        ApplicationDTO application = srApplicationService.getApplicationById(id);
        return ResponseEntity.ok(StandardResponse.single(MSG_FETCH_SUCCESS, application));
    }

    /**
     * Get applications by requirement
     */
    @GetMapping("/requirement/{requirementId}")
    public ResponseEntity<StandardResponse<Page<ApplicationDTO>>> getApplicationsByRequirement(
            @PathVariable Long requirementId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationDTO> applications = srApplicationService.getApplicationsByRequirement(requirementId, pageable);
        return ResponseEntity.ok(StandardResponse.page(MSG_LIST_SUCCESS, applications));
    }

    /**
     * Get applications by applicant
     */
    @GetMapping("/applicant/{applicantUserId}")
    public ResponseEntity<StandardResponse<Page<ApplicationDTO>>> getApplicationsByApplicant(
            @PathVariable Long applicantUserId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<ApplicationDTO> applications = srApplicationService.getApplicationsByApplicant(applicantUserId, pageable);
        return ResponseEntity.ok(StandardResponse.page(MSG_LIST_SUCCESS, applications));
    }

    /**
     * Update application
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<ApplicationDTO>> updateApplication(
            @PathVariable Long id,
            @RequestBody ApplicationDTO applicationDTO) {

        ApplicationDTO updatedApplication = srApplicationService.updateApplication(id, applicationDTO);
        return ResponseEntity.ok(StandardResponse.single(MSG_UPDATE_SUCCESS, updatedApplication));
    }

    /**
     * Delete application
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteApplication(@PathVariable Long id) {
        srApplicationService.deleteApplication(id);
        return ResponseEntity.ok(StandardResponse.message(MSG_DELETE_SUCCESS));
    }

    @PreAuthorize("hasAuthority('ROLE_SUPER_ADMIN')")
    @GetMapping("/startup/{b2bUnitId}/applications")
    public ResponseEntity<StandardResponse<Page<ApplicationDTO>>> getApplicationsForStartup(
            @PathVariable("b2bUnitId") Long b2bUnitId,
            Pageable pageable) {

        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        log.info("Fetching applications for startup [unitId={}] by admin [userId={}]", b2bUnitId, loggedInUser.getId());

        Page<ApplicationDTO> applications = srApplicationService.getApplicationsForStartup(b2bUnitId, pageable);

        return ResponseEntity.ok(StandardResponse.page("Applications fetched successfully", applications));
    }

}
