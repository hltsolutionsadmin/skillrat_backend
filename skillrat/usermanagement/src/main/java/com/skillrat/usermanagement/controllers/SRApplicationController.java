package com.skillrat.usermanagement.controllers;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.azure.service.BlobStorageService;
import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.dto.enums.ApplicationStatus;
import com.skillrat.usermanagement.model.*;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping("/v1/applications")
@RequiredArgsConstructor
public class SRApplicationController {

    private final SRApplicationService srApplicationService;
    private final UserService userService;
    private final SRApplicationPopulator srApplicationPopulator;
    private final SRRequirementRepository srRequirementRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final BlobStorageService blobStorageService;

    //@PreAuthorize("hasAuthority('ROLE_STUDENT')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<StandardResponse<ApplicationDTO>> createApplication(
            @ModelAttribute ApplicationDTO applicationDTO) throws IOException {

        // Fetch currently logged-in user
        UserModel currentUser = fetchCurrentUser();

        boolean hasRole = currentUser.getRoles().stream()
                .anyMatch(role -> SRAppConstants.ROLE_STUDENT.equalsIgnoreCase(String.valueOf(role.getName())));
        if (!hasRole) {
            throw new HltCustomerException(ErrorCode.UNAUTHORIZED);
        }

        // Convert DTO to entity and set applicant
        ApplicationModel application = dtoToEntity(applicationDTO, currentUser);

        // Upload files (if any) and attach URLs to application
        if (applicationDTO.getDocuments() != null && !applicationDTO.getDocuments().isEmpty()) {
            List<MediaModel> uploadedFiles = blobStorageService.uploadFiles(applicationDTO.getDocuments());
            application.setMediaFiles(uploadedFiles);
        }

        // Save the application
        ApplicationModel saved = srApplicationService.createApplication(application);

        log.info("Application created [appId={}] by user [userId={}]", saved.getId(), currentUser.getId());

        // Return standardized response
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

    @GetMapping("/applicantionsByUser")
    public ResponseEntity<StandardResponse<Page<ApplicationDTO>>> getApplicationsByApplicant(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
        Page<ApplicationModel> applications = srApplicationService.getApplicationsByApplicant(loggedInUser.getId(), buildPageable(page, size));

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
        srApplicationPopulator.populate(app, dto);
        return dto;
    }

    private ApplicationModel dtoToEntity(ApplicationDTO dto, UserModel applicant) {
        ApplicationModel app = new ApplicationModel();

        app.setId(dto.getId());
        app.setStatus(dto.getStatus() != null ? dto.getStatus() : ApplicationStatus.PENDING);
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
