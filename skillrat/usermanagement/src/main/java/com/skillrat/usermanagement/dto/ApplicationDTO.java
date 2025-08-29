package com.skillrat.usermanagement.dto;

import com.skillrat.usermanagement.dto.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Getter
@Setter
public class ApplicationDTO {

    private Long id;

    private Long b2bUnitId;

    private Long requirementId;

    private Long applicantUserId;

    private ApplicationStatus status;

    private String coverLetter;

    private Set<MediaDTO> mediaFiles;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
