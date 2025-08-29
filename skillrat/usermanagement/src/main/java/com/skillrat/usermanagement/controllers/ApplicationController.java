package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.services.ApplicationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class ApplicationController {

    private final ApplicationService applicationService;

    private static final String MSG_CREATE_SUCCESS = "Application created successfully";
    private static final String MSG_FETCH_SUCCESS = "Application fetched successfully";
    private static final String MSG_LIST_SUCCESS = "Applications listed successfully";
    private static final String MSG_UPDATE_SUCCESS = "Application updated successfully";
    private static final String MSG_DELETE_SUCCESS = "Application deleted successfully";

    /**
     * Create a new application
     */
    @PostMapping
    public ResponseEntity<StandardResponse<ApplicationDTO>> createApplication(
            @RequestBody ApplicationDTO applicationDTO) {

        ApplicationDTO savedApplication = applicationService.createApplication(applicationDTO);
        return ResponseEntity.ok(StandardResponse.single(MSG_CREATE_SUCCESS, savedApplication));
    }

    /**
     * Get application by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<ApplicationDTO>> getApplicationById(@PathVariable Long id) {
        ApplicationDTO application = applicationService.getApplicationById(id);
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
        Page<ApplicationDTO> applications = applicationService.getApplicationsByRequirement(requirementId, pageable);
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
        Page<ApplicationDTO> applications = applicationService.getApplicationsByApplicant(applicantUserId, pageable);
        return ResponseEntity.ok(StandardResponse.page(MSG_LIST_SUCCESS, applications));
    }

    /**
     * Update application
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<ApplicationDTO>> updateApplication(
            @PathVariable Long id,
            @RequestBody ApplicationDTO applicationDTO) {

        ApplicationDTO updatedApplication = applicationService.updateApplication(id, applicationDTO);
        return ResponseEntity.ok(StandardResponse.single(MSG_UPDATE_SUCCESS, updatedApplication));
    }

    /**
     * Delete application
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteApplication(@PathVariable Long id) {
        applicationService.deleteApplication(id);
        return ResponseEntity.ok(StandardResponse.message(MSG_DELETE_SUCCESS));
    }
}
