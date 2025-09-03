package com.skillrat.usermanagement.model;

import java.time.LocalDate;
import java.util.List;

import com.skillrat.usermanagement.dto.enums.ExperienceType;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
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
    
    
    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<EducationModel> education;


    @Column(name = "START_DATE")
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;

    @OneToMany(mappedBy = "experience", cascade = CascadeType.ALL, orphanRemoval = true)
    @Valid
    private List<InternshipOrJobModel> internshipsAndJobs;

}
