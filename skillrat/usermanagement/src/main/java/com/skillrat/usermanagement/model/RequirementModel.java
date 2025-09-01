package com.skillrat.usermanagement.model;

import com.skillrat.usermanagement.dto.enums.RequirementType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

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
public class RequirementModel extends GenericModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "requirement_id", updatable = false, nullable = false)
    private Long id;

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

    @Column(name = "location", length = 255)
    private String location;

    @Column(name = "is_active", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;
}
