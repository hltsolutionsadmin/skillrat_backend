package com.hlt.usermanagement.model;

import com.hlt.usermanagement.dto.enums.ExperienceType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

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
    @Column(name = "TYPE", nullable = false, length = 30)
    private ExperienceType type;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "B2B_UNIT_ID", nullable = false)
    private B2BUnitModel b2bUnit;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE")
    private LocalDate endDate;
}
