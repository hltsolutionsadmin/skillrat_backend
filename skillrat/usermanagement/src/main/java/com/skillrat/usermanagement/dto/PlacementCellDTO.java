
package com.skillrat.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skillrat.usermanagement.dto.enums.PlacementCellStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Data Transfer Object representing Placement Cell details.
 * This is used in API responses to show placement cell info.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlacementCellDTO {

    private Long id;

    private B2BUnitDTO b2bUnit; // optional field to show minimal B2B info

    private Long b2bUnitId;

    private UserDTO coordinator;

    private String coordinatorName;

    private Integer totalStudentsRegistered;

    private Integer yearOfEstablishment;

    private String remarks;

    private PlacementCellStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

