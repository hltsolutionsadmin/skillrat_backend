package com.skillrat.usermanagement.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InternshipOrJobDTO {

    private Long id;
    private String companyName;
    private String roleTitle;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
}
