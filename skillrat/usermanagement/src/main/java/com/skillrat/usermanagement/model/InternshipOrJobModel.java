package com.skillrat.usermanagement.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "INTERNSHIP_JOB")
@Getter
@Setter
public class InternshipOrJobModel extends GenericModel {


    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "EXPERIENCE_ID", nullable = false)
    private ExperienceModel experience;

    @Column(name = "COMPANY_NAME", nullable = false, length = 500)
    private String companyName;

    @Column(name = "ROLE_TITLE", nullable = false, length = 255)
    private String roleTitle;


    @Column(name = "DESCRIPTION", length = 2000)
    private String description;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;


    @Column(name = "END_DATE", nullable = true)
    private LocalDate endDate;
}
