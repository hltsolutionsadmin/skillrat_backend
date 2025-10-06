package com.skillrat.usermanagement.model;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.skillrat.usermanagement.dto.enums.ExperienceType;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(
        name = "EXPERIENCES",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"USER_ID", "B2B_UNIT_ID", "TYPE"})
        },
        indexes = {
                @Index(name = "idx_user_exp", columnList = "USER_ID"),
                @Index(name = "idx_b2bunit_exp", columnList = "B2B_UNIT_ID"),
                @Index(name = "idx_type_exp", columnList = "TYPE")
        }
)
@Getter
@Setter
public class ExperienceModel extends GenericModel {

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 30)
    private ExperienceType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;

    /**
     * Each experience belongs to a specific business unit (tenant).
     * This must always be set before persisting the record.
     */
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "B2B_UNIT_ID", nullable = false)
    private B2BUnitModel b2bUnit;

    /**
     * Education entries linked to this experience.
     * Using CascadeType.MERGE ensures updates are synchronized,
     * but avoids creating transient entities automatically.
     */
    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "EXPERIENCES_EDUCATION",
            joinColumns = @JoinColumn(name = "EXPERIENCE_ID"),
            inverseJoinColumns = @JoinColumn(name = "EDUCATION_ID")
    )
    private Set<EducationModel> education = new HashSet<>();


    /**
     * Internship experience details.
     * CascadeType.ALL + orphanRemoval ensures lifecycle consistency.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "EXPERIENCE_ID")
    @Valid
    private List<InternshipModel> internships;

    /**
     * Job experience details.
     */
    @OneToMany(
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(name = "EXPERIENCE_ID")
    @Valid
    private List<JobModel> jobs;

    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;
}
