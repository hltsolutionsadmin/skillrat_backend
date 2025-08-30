package com.skillrat.usermanagement.model;

import java.time.LocalDate;

import com.skillrat.usermanagement.dto.enums.EducationLevel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Education")
@Getter
@Setter
public class EducationModel extends GenericModel{

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false) // <-- foreign key column
    private UserModel user;

	@Column(name = "INSTITUTION", length = 1500, nullable = false)
	private String institution;

	@Enumerated(EnumType.STRING)
	@Column(name = "EDUCATION_LEVEL")
	private EducationLevel educationLevel;

	@Min(3)
	@Max(10)
	@Column(name = "CGPA",nullable = true)
	private Float cgpa;
	
	@Min(200)
	@Max(1000)
	@Column(name = "MARKS",nullable = true)
	private int marks;
	
	@Column(name = "START_DATE",nullable = false)
	private LocalDate startDate;
	
	@Column(name = "END_DATE",nullable = false)
	private LocalDate endDate;
}
