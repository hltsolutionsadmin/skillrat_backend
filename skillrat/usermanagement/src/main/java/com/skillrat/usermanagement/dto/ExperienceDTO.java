package com.skillrat.usermanagement.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ExperienceDTO {
	private Long id;
	private String type;
	
	private boolean addingEducation;

	private Long userId;
	private Long b2bUnitId;

	private LocalDate startDate;
	private LocalDate endDate;
	private EducationDTO educationDTO;
	private List<EducationDTO> academics;

    private List<InternshipDTO> internships;

    private List<JobDTO> jobs;

}
