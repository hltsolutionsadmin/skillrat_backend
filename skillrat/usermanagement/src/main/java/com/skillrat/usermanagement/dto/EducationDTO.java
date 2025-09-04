package com.skillrat.usermanagement.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EducationDTO {
	private Long id;

	private String level; 
	private String institution;
	private Float cgpa;
	private int marks;

	private LocalDate startDate;
	private LocalDate endDate;
}
