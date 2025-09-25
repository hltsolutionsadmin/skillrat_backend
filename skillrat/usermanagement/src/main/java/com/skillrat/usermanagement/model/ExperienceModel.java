package com.skillrat.usermanagement.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

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

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "B2B_UNIT_ID", nullable = true)
    private B2BUnitModel b2bUnit;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "experience_skills",
            joinColumns = @JoinColumn(name = "experience_id"),
            inverseJoinColumns = @JoinColumn(name = "skill_id")
    )
    private Set<SkillModel> skills = new HashSet<>();


    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private Set<EducationModel> education = new HashSet<>();

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private Set<InternshipModel> internships = new HashSet<>();

    @ManyToMany
    @JoinTable(name = "experiences_jobs",
            joinColumns = @JoinColumn(name = "experience_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"experience_id", "job_id"}))
    private Set<JobModel> jobs = new HashSet<>();


    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

}
