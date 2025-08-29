package com.skillrat.usermanagement.dto;

import com.skillrat.usermanagement.dto.enums.ApplicationStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ApplicationDTO {

    private Long id;

    @NotNull(message = "B2B Unit ID is required")
    private Long b2bUnitId;

    @NotNull(message = "Requirement ID is required")
    private Long requirementId;

    @NotNull(message = "Applicant User ID is required")
    private Long applicantUserId;

    @NotNull(message = "Status is required")
    private ApplicationStatus status;

    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    private String coverLetter;

    @NotNull(message = "At least one media file is required")
    @Size(min = 1, message = "At least one media file must be provided")
    private Set<@NotNull MediaDTO> mediaFiles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
