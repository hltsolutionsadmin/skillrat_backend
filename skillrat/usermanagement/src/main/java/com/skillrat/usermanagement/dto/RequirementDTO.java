package com.skillrat.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.skillrat.usermanagement.dto.enums.RequirementType;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RequirementDTO {

    private Long id;

    @NotBlank(message = "Title is required")
    @Size(max = 255, message = "Title must not exceed 255 characters")
    private String title;

    @NotBlank(message = "Designation is required")
    @Size(max = 255, message = "Designation must not exceed 255 characters")
    private String designation;

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

    private Long createdByUserId;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @FutureOrPresent(message = "Start date must be today or in the future")
    private LocalDateTime startDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Future(message = "End date must be in the future")
    private LocalDateTime endDate;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    private List<Long> skillsRequired;

    @NotBlank(message = "Code is required")
    @Size(max = 100, message = "Code must not exceed 100 characters")
    private String code;

    @Size(max = 255, message = "Business name must not exceed 255 characters")
    private String businessName;

    @Size(max = 255, message = "Department must not exceed 255 characters")
    private String department;

    private Double stipend;

    @NotNull(message = "Remote flag is required")
    private Boolean remote;

    @Size(max = 500, message = "Eligibility criteria must not exceed 500 characters")
    private String eligibilityCriteria;

    @Size(max = 2000, message = "Responsibilities must not exceed 2000 characters")
    private String responsibilities;

    @Size(max = 2000, message = "Benefits must not exceed 2000 characters")
    private String benefits;

    private List<AddressDTO> addresses;

    private Boolean applied;

}
