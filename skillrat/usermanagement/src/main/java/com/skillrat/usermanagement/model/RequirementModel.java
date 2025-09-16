package com.skillrat.usermanagement.model;

import com.skillrat.usermanagement.dto.enums.RequirementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(
        name = "requirement",
        indexes = {
                @Index(name = "idx_requirement_b2bunit", columnList = "b2b_unit_id"),
                @Index(name = "idx_requirement_createdby", columnList = "created_by_user_id"),
                @Index(name = "idx_requirement_type", columnList = "type")
        }
)
@Getter
@Setter
@AttributeOverride(name = "id", column = @Column(name = "requirement_id"))
public class RequirementModel extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "b2b_unit_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_requirement_b2bunit")
    )
    private B2BUnitModel b2bUnit;

    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "designation", length = 255)
    private String designation;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 50)
    private RequirementType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "created_by_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_requirement_createdby")
    )
    private UserModel createdBy;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "skills_required", length = 500)
    private String skillsRequired;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "business_name", length = 255)
    private String businessName;

    @Column(name = "department", length = 255)
    private String department;

    @Column(name = "stipend")
    private Double stipend;

    @Column(name = "remote", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean remote = Boolean.FALSE;

    @Column(name = "eligibility_criteria", length = 500)
    private String eligibilityCriteria;

    @Column(name = "responsibilities", columnDefinition = "TEXT")
    private String responsibilities;

    @Column(name = "benefits", columnDefinition = "TEXT")
    private String benefits;

    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "requirement_addresses",
            joinColumns = @JoinColumn(name = "requirement_id"),
            inverseJoinColumns = @JoinColumn(name = "address_id")
    )
    private List<AddressModel> addresses;

}
