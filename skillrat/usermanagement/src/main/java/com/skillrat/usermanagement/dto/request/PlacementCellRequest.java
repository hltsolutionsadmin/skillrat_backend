package com.skillrat.usermanagement.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skillrat.usermanagement.dto.UserDTO;
import com.skillrat.usermanagement.dto.enums.PlacementCellStatus;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * Request object for creating or updating a Placement Cell.
 * This can be used standalone or as an optional nested object
 * inside B2BUnit onboarding requests.
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PlacementCellRequest {

    private Long id;

    private Long b2bUnitId; // Optional when used during B2BUnit onboarding

    //private UserDTO coordinator;

    private Long coordinatorId;

    private Integer totalStudentsRegistered;

    private Integer yearOfEstablishment;

    @Size(max = 255, message = "Remarks cannot exceed 255 characters")
    private String remarks;

    private Boolean active = Boolean.TRUE;

    private PlacementCellStatus status;
}
