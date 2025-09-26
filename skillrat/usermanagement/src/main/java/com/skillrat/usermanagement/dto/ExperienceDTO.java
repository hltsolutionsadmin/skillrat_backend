package com.skillrat.usermanagement.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperienceDTO {

    private Long id;

    @NotNull(message = "Experience type cannot be null")
    @Size(max = 30, message = "Experience type must be at most 30 characters")
    private String type;

    private boolean addingEducation;


    private Long userId;

    private Long b2bUnitId;

    @PastOrPresent(message = "Start date cannot be in the future")
    private LocalDate startDate;

    @PastOrPresent(message = "End date cannot be in the future")
    private LocalDate endDate;

    @Valid
    private EducationDTO educationDTO;

    @Valid
    private List<EducationDTO> academics;

    @Valid
    private List<InternshipDTO> internships;

    @Valid
    private List<JobDTO> jobs;

}
