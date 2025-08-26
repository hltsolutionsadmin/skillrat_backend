package com.hlt.usermanagement.dto;

import com.hlt.usermanagement.dto.enums.RequirementType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class RequirementDTO {

    private Long id;
    private String title;
    private String description;
    private RequirementType type;
    private String location;
    private Boolean isActive;
    private Long b2bUnitId;
    private Long createdByUserId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
