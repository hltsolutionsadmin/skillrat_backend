package com.skillrat.usermanagement.dto;

import java.time.LocalDate;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
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

    // INTERNSHIP / JOB
//    private InternshipOrJobDTO internshipOrJob;

    // Change this from single object → List
    private List<InternshipOrJobDTO> internships;


}
