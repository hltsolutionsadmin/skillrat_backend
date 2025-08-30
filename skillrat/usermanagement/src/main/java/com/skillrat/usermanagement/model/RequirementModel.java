package com.skillrat.usermanagement.model;
import com.skillrat.usermanagement.dto.enums.RequirementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
@Entity
@Table(name = "REQUIREMENT")
@Getter
@Setter
public class RequirementModel extends GenericModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "REQUIREMENT_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "B2B_UNIT_ID", nullable = false)
    private B2BUnitModel b2bUnit;

    @Column(name = "TITLE", nullable = false, length = 255)
    private String title;

    @Column(name = "DESCRIPTION", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "TYPE", nullable = false, length = 50)
    private RequirementType type;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CREATED_BY_USER_ID", nullable = false)
    private UserModel createdBy;

    @Column(name = "LOCATION", length = 255)
    private String location;

    @Column(name = "IS_ACTIVE", nullable = false)
    private Boolean isActive = true;

    @Column(name = "START_DATE", nullable = true)
    private LocalDateTime startDate;

    @Column(name = "END_DATE", nullable = true)
    private LocalDateTime endDate;


}