package com.skillrat.usermanagement.dto;

import com.skillrat.usermanagement.dto.enums.RequirementType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequirementDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Description is required")
    @Size(max = 2000, message = "Description must not exceed 2000 characters")
    private String description;

    @NotNull(message = "Requirement type is required")
    private RequirementType type;

    @NotBlank(message = "Location is required")
    @Size(max = 255, message = "Location must not exceed 255 characters")
    private String location;

    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotNull(message = "B2B Unit ID is required")
    private Long b2bUnitId;

    @NotNull(message = "Created by User ID is required")
    private Long createdByUserId;

    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDateTime startDate;

    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
