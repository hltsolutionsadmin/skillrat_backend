package com.skillrat.usermanagement.model;

import java.time.LocalDate;

import com.skillrat.usermanagement.dto.enums.ExperienceType;
import jakarta.persistence.*;

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
    @JoinColumn(name = "EXPERIENCE_ID", nullable = true)
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

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", length = 30)
    private ExperienceType type;
}