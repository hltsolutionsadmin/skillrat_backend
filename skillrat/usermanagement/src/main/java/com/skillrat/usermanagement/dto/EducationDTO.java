package com.skillrat.usermanagement.dto;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EducationDTO {
	private Long id;

	private String level; 
	private String institution;
	private Float cgpa;
	private Integer marks;
	private String studentId;
	private LocalDate startDate;
	private LocalDate endDate;

}
