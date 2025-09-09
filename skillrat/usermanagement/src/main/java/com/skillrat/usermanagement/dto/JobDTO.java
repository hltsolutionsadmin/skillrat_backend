package com.skillrat.usermanagement.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class JobDTO {
    private Long id;                // optional
    private String companyName;
    private String position;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
