package com.skillrat.usermanagement.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Data
public class PlacementCellDTO {

    private Long id;

    private String description;

    private Long b2bUnitId;

    private Long userId;

    private UserDTO user;

    private B2BUnitDTO b2bUnit;

    private LocalDateTime createdAt;
}
