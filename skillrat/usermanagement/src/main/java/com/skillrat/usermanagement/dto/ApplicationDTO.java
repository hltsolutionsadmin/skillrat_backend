package com.skillrat.usermanagement.dto;

import com.skillrat.usermanagement.dto.enums.ApplicationStatus;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class ApplicationDTO {

    private Long id;

    @NotNull(message = "Requirement ID is required")
    @Positive(message = "Requirement ID must be a positive number")
    private Long requirementId;

    @Positive(message = "B2B Unit ID must be a positive number")
    private Long b2bUnitId;

    @Positive(message = "Applicant User ID must be a positive number")
    private Long applicantUserId;

    @NotNull(message = "Application status cannot be null")
    private ApplicationStatus status;

    @Size(max = 2000, message = "Cover letter must not exceed 2000 characters")
    private String coverLetter;

    private List<MultipartFile> documents;
    private List<MediaDTO> documentsInfo;

    @PastOrPresent(message = "CreatedAt cannot be in the future")
    private LocalDateTime createdAt;

    @PastOrPresent(message = "UpdatedAt cannot be in the future")
    private LocalDateTime updatedAt;

    private RequirementDTO requirement;

    private UserDTO applicant;

}
