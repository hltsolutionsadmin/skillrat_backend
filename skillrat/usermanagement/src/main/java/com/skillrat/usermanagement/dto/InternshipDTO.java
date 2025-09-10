package com.skillrat.usermanagement.dto;

import java.time.LocalDate;
import lombok.Data;

@Data
public class InternshipDTO {
    private Long id;                // optional: useful for future updates
    private String companyName;
    private String role;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
}
