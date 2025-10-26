package com.skillrat.usermanagement.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PlacementCellDTO {

    private Long id;

    @NotBlank(message = "Description is required")
    @Size(max = 500, message = "Description must not exceed 500 characters")
    private String description;

    @NotNull(message = "B2B Unit is required")
    private B2BUnitDTO b2bUnit;

    private UserDTO coordinator;

    private String coordinatorName;

}
