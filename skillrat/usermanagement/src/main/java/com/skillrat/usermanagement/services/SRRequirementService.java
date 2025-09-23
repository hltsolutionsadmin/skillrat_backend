package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.dto.enums.RequirementType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SRRequirementService {

    /**
     * Create a new requirement
     */
   RequirementDTO createRequirement(RequirementDTO requirementDTO);

    /**
     * Update an existing requirement
     */
   RequirementDTO updateRequirement(Long id, RequirementDTO requirementDTO);

    /**
     * Get requirement by id
     */
    RequirementDTO getRequirementById(Long id);

    /**
     * Get all requirements with pagination
     */
    Page<RequirementDTO> getAllRequirements(Pageable pageable, Long userId);

    /**
     * Delete requirement by id (soft delete with isActive=false)
     */
    void deleteRequirement(Long id);

     Page<RequirementDTO> getRequirementsByB2bUnit(Long b2bUnitId, RequirementType type, Pageable pageable);
}