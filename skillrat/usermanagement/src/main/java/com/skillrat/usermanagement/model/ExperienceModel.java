package com.skillrat.usermanagement.model;

import java.time.LocalDate;
import java.util.List;

import com.skillrat.usermanagement.dto.enums.ExperienceType;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "EXPERIENCES",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"USER_ID", "B2B_UNIT_ID", "TYPE"})
        },
        indexes = {
                @Index(name = "idx_user_exp", columnList = "USER_ID"),
                @Index(name = "idx_b2bunit_exp", columnList = "B2B_UNIT_ID"),
                @Index(name = "idx_type_exp", columnList = "TYPE")
        })
@Getter
@Setter
public class ExperienceModel extends GenericModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = true, length = 30)
    private ExperienceType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "B2B_UNIT_ID", nullable = true)
    private B2BUnitModel b2bUnit;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "experience_skills",
            joinColumns = @JoinColumn(name = "experience_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private List<SkillModel> skills;


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<EducationModel> education;

    // Internship Experiences
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<InternshipModel> internships;

    //  Job Experiences
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<JobModel> jobs;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;


}
