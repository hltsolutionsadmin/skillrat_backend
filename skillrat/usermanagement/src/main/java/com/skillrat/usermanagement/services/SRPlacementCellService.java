//package com.skillrat.usermanagement.services;
//
//import com.skillrat.usermanagement.dto.PlacementCellDTO;
//import com.skillrat.usermanagement.dto.request.PlacementCellRequest;
//import org.springframework.data.domain.Page;
//import org.springframework.data.domain.Pageable;
//
//import java.util.List;
//import java.util.Optional;
//
//public interface PlacementCellService {
//
//    /**
//     * Creates a new Placement Cell entry.
//     */
//    PlacementCellDTO createPlacementCell(PlacementCellRequest request);
//
//    /**
//     * Updates an existing Placement Cell entry.
//     */
//    PlacementCellDTO updatePlacementCell(Long placementCellId, PlacementCellRequest request);
//
//    /**
//     * Deletes a placement cell by its ID.
//     */
//    void deletePlacementCell(Long placementCellId);
//
//    /**
//     * Fetch a placement cell by ID.
//     */
//    Optional<PlacementCellDTO> getPlacementCellById(Long placementCellId);
//
//    /**
//     * Fetch all placement cells with pagination.
//     */
//    Page<PlacementCellDTO> getAllPlacementCells(Pageable pageable);
//
//    /**
//     * Fetch all placement cells for a specific B2B Unit.
//     */
//    List<PlacementCellDTO> getPlacementCellsByB2BUnitId(Long b2bUnitId);
//
//    /**
//     * Check if a placement cell exists for a given B2B unit.
//     */
//    boolean existsByB2BUnitId(Long b2bUnitId);
//
//    /**
//     * Optional onboarding method – if placement cell data is provided
//     * during B2B unit onboarding, it should be created automatically.
//     */
//    PlacementCellDTO onboardPlacementCellIfProvided(Long b2bUnitId, PlacementCellRequest request);
//}

package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.dto.PlacementCellDTO;
import com.skillrat.usermanagement.dto.request.PlacementCellRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SRPlacementCellService {

    PlacementCellDTO createPlacementCell(PlacementCellRequest request);

    PlacementCellDTO updatePlacementCell(Long placementCellId, PlacementCellRequest request);

    void deletePlacementCell(Long placementCellId);

    Optional<PlacementCellDTO> getPlacementCellById(Long placementCellId);

    Page<PlacementCellDTO> getAllPlacementCells(Pageable pageable);

    List<PlacementCellDTO> getPlacementCellsByB2BUnitId(Long b2bUnitId);

    boolean existsByB2BUnitId(Long b2bUnitId);

    PlacementCellDTO onboardPlacementCellIfProvided(Long b2bUnitId, PlacementCellRequest request);
}

