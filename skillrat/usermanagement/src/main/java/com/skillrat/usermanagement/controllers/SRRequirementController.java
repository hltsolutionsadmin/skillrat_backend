package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.commonservice.user.UserDetailsImpl;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.dto.enums.RequirementType;
import com.skillrat.usermanagement.services.SRRequirementService;
import com.skillrat.utils.SRAppConstants;
import com.skillrat.utils.SecurityUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/requirements")
@RequiredArgsConstructor
public class SRRequirementController {

    private final SRRequirementService srRequirementService;

    /**
     * Create a new requirement
     */
    @PostMapping
    public ResponseEntity<StandardResponse<RequirementDTO>> createRequirement(
            @RequestBody RequirementDTO requirementDTO) {

        RequirementDTO savedRequirement = srRequirementService.createRequirement(requirementDTO);
        return ResponseEntity.ok(StandardResponse.single(
                SRAppConstants.REQUIREMENT_CREATE_SUCCESS, savedRequirement));
    }

    /**
     * Get requirement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<RequirementDTO>> getRequirementById(@PathVariable Long id) {
        RequirementDTO requirement = srRequirementService.getRequirementById(id);
        return ResponseEntity.ok(StandardResponse.single(
                SRAppConstants.REQUIREMENT_FETCH_SUCCESS, requirement));
    }

    /**
     * Get all requirements
     */
    @GetMapping
    public ResponseEntity<StandardResponse<Page<RequirementDTO>>> getAllRequirements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

        Pageable pageable = PageRequest.of(page, size);
        Page<RequirementDTO> requirements = srRequirementService.getAllRequirements(pageable, loggedInUser.getId());
        return ResponseEntity.ok(StandardResponse.page(
                SRAppConstants.REQUIREMENT_LIST_SUCCESS, requirements));
    }


    /**
     * Update requirement
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<RequirementDTO>> updateRequirement(
            @PathVariable Long id,
            @RequestBody RequirementDTO requirementDTO) {

        RequirementDTO updatedRequirement = srRequirementService.updateRequirement(id, requirementDTO);
        return ResponseEntity.ok(StandardResponse.single(
                SRAppConstants.REQUIREMENT_UPDATE_SUCCESS, updatedRequirement));
    }

    /**
     * Delete requirement
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteRequirement(@PathVariable Long id) {
        srRequirementService.deleteRequirement(id);
        return ResponseEntity.ok(StandardResponse.message(SRAppConstants.REQUIREMENT_DELETE_SUCCESS));
    }

    @GetMapping("/b2b/{b2bUnitId}")
    public ResponseEntity<StandardResponse<Page<RequirementDTO>>> getRequirementsByB2bUnit(
            @PathVariable Long b2bUnitId,
            @RequestParam(required = false) RequirementType type,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RequirementDTO> requirements = srRequirementService.getRequirementsByB2bUnit(b2bUnitId, type, pageable);

        return ResponseEntity.ok(
                StandardResponse.page(SRAppConstants.REQUIREMENT_FETCH_SUCCESS, requirements)
        );
    }

}
