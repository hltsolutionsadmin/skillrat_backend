
package com.skillrat.usermanagement.controllers;

import com.skillrat.commonservice.dto.StandardResponse;
import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.dto.request.PlacementCellRequest;
import com.skillrat.usermanagement.services.SRPlacementCellService;
import com.skillrat.utils.SRAppConstants;
import com.skillrat.utils.SRBaseEndpoint;

import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/placement-cell")
@Slf4j
@AllArgsConstructor
public class SRPlacementCellController extends SRBaseEndpoint {

    private final SRPlacementCellService placementCellService;

    /**
     * Create a new Placement Cell
     */
    @PreAuthorize(SRAppConstants.ROLE_SUPER_ADMIN)
    @PostMapping("/create")
    public ResponseEntity<StandardResponse<PlacementCellDTO>> createPlacementCell(
            @Valid @RequestBody PlacementCellRequest request) {

        log.info("Creating new Placement Cell for B2BUnit ID: {}", request.getB2bUnitId());
        PlacementCellDTO response = placementCellService.createPlacementCell(request);
        return ResponseEntity.ok(StandardResponse.single("Placement Cell created successfully", response));
    }

    /**
     * Update existing Placement Cell
     */
    @PreAuthorize(SRAppConstants.ROLE_SUPER_ADMIN)
    @PutMapping("/{placementCellId}")
    public ResponseEntity<StandardResponse<PlacementCellDTO>> updatePlacementCell(
            @PathVariable Long placementCellId,
            @Valid @RequestBody PlacementCellRequest request) {

        log.info("Updating Placement Cell with ID: {}", placementCellId);
        PlacementCellDTO response = placementCellService.updatePlacementCell(placementCellId, request);
        return ResponseEntity.ok(StandardResponse.single("Placement Cell updated successfully", response));
    }

    /**
     * Delete Placement Cell
     */
    @PreAuthorize(SRAppConstants.ROLE_SUPER_ADMIN)
    @DeleteMapping("/{placementCellId}")
    public ResponseEntity<StandardResponse<String>> deletePlacementCell(@PathVariable Long placementCellId) {

        log.info("Deleting Placement Cell ID: {}", placementCellId);
        placementCellService.deletePlacementCell(placementCellId);
        return ResponseEntity.ok(StandardResponse.single("Placement Cell deleted successfully", "Deleted"));
    }

    /**
     * Get Placement Cell by ID
     */
    @GetMapping("/{placementCellId}")
    public ResponseEntity<StandardResponse<PlacementCellDTO>> getPlacementCellById(@PathVariable Long placementCellId) {

        log.info("Fetching Placement Cell by ID: {}", placementCellId);
        return placementCellService.getPlacementCellById(placementCellId)
                .map(cell -> ResponseEntity.ok(StandardResponse.single("Placement Cell fetched successfully", cell)))
                .orElseGet(() -> ResponseEntity.ok(StandardResponse.single("Placement Cell not found", null)));
    }

    /**
     * Get all Placement Cells (Paginated)
     */
    @GetMapping("/list")
    public ResponseEntity<StandardResponse<Page<PlacementCellDTO>>> getAllPlacementCells(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        log.info("Fetching all Placement Cells (page={}, size={})", page, size);
        Page<PlacementCellDTO> result = placementCellService.getAllPlacementCells(pageable);
        return ResponseEntity.ok(StandardResponse.page("Placement Cells fetched successfully", result));
    }

    /**
     * Get all Placement Cells for a specific B2B Unit
     */
    @GetMapping("/b2b/{b2bUnitId}")
    public ResponseEntity<StandardResponse<List<PlacementCellDTO>>> getPlacementCellsByB2BUnitId(
            @PathVariable Long b2bUnitId) {

        log.info("Fetching Placement Cells for B2BUnit ID: {}", b2bUnitId);
        List<PlacementCellDTO> result = placementCellService.getPlacementCellsByB2BUnitId(b2bUnitId);
        return ResponseEntity.ok(StandardResponse.list("Placement Cells fetched successfully for given B2B Unit", result));
    }

    /**
     * Check if a Placement Cell exists for a B2B Unit
     */
    @GetMapping("/exists/{b2bUnitId}")
    public ResponseEntity<StandardResponse<Boolean>> existsByB2BUnitId(@PathVariable Long b2bUnitId) {

        log.info("Checking if Placement Cell exists for B2BUnit ID: {}", b2bUnitId);
        boolean exists = placementCellService.existsByB2BUnitId(b2bUnitId);
        return ResponseEntity.ok(StandardResponse.single("Existence check completed", exists));
    }

    /**
     * Optional onboarding endpoint – if Placement Cell data is provided during B2BUnit onboarding
     */
    @PreAuthorize(SRAppConstants.ROLE_SUPER_ADMIN)
    @PostMapping("/onboard/{b2bUnitId}")
    public ResponseEntity<StandardResponse<PlacementCellDTO>> onboardPlacementCellIfProvided(
            @PathVariable Long b2bUnitId,
            @Valid @RequestBody PlacementCellRequest request) {

        log.info("Optionally onboarding Placement Cell for B2BUnit ID: {}", b2bUnitId);
        PlacementCellDTO response = placementCellService.onboardPlacementCellIfProvided(b2bUnitId, request);
        return ResponseEntity.ok(StandardResponse.single("Placement Cell onboarded successfully (if provided)", response));
    }
}