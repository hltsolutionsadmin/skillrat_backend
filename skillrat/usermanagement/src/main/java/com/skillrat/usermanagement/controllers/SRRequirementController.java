package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.services.SRRequirementService;
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

    private static final String MSG_CREATE_SUCCESS = "Requirement created successfully";
    private static final String MSG_FETCH_SUCCESS = "Requirement fetched successfully";
    private static final String MSG_LIST_SUCCESS = "Requirements listed successfully";
    private static final String MSG_UPDATE_SUCCESS = "Requirement updated successfully";
    private static final String MSG_DELETE_SUCCESS = "Requirement deleted successfully";

    /**
     * Create a new requirement
     */
    @PostMapping
    public ResponseEntity<StandardResponse<RequirementDTO>> createRequirement(
            @RequestBody RequirementDTO requirementDTO) {

        RequirementDTO savedRequirement = srRequirementService.createRequirement(requirementDTO);
        return ResponseEntity.ok(StandardResponse.single(MSG_CREATE_SUCCESS, savedRequirement));
    }

    /**
     * Get requirement by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<StandardResponse<RequirementDTO>> getRequirementById(@PathVariable Long id) {
        RequirementDTO requirement = srRequirementService.getRequirementById(id);
        return ResponseEntity.ok(StandardResponse.single(MSG_FETCH_SUCCESS, requirement));
    }

    /**
     * Get all requirements
     */
    @GetMapping
    public ResponseEntity<StandardResponse<Page<RequirementDTO>>> getAllRequirements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<RequirementDTO> requirements = srRequirementService.getAllRequirements(pageable);
        return ResponseEntity.ok(StandardResponse.page(MSG_LIST_SUCCESS, requirements));
    }

    /**
     * Update requirement
     */
    @PutMapping("/{id}")
    public ResponseEntity<StandardResponse<RequirementDTO>> updateRequirement(
            @PathVariable Long id,
            @RequestBody RequirementDTO requirementDTO) {

        RequirementDTO updatedRequirement = srRequirementService.updateRequirement(id, requirementDTO);
        return ResponseEntity.ok(StandardResponse.single(MSG_UPDATE_SUCCESS, updatedRequirement));
    }

    /**
     * Delete requirement
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<StandardResponse<Void>> deleteRequirement(@PathVariable Long id) {
        srRequirementService.deleteRequirement(id);
        return ResponseEntity.ok(StandardResponse.message(MSG_DELETE_SUCCESS));
    }
}