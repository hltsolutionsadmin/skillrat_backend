package com.hlt.usermanagement.dto;

import com.hlt.usermanagement.dto.enums.ApplicationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class ApplicationDTO {

    private Long id;
    private Long requirementId;
    private Long studentId;
    private ApplicationStatus status;
    private String remarks;
    private LocalDateTime appliedAt;
    private LocalDateTime updatedAt;
}
