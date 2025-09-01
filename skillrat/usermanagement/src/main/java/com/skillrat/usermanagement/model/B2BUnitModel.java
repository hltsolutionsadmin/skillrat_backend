package com.skillrat.usermanagement.model;

import com.skillrat.usermanagement.dto.enums.BusinessType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Set;

@Entity
@Table(
        name = "b2b_unit",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_b2bunit_business_name", columnNames = {"business_name"}),
                @UniqueConstraint(name = "uk_b2bunit_business_code", columnNames = {"business_code"})
        },
        indexes = {
                @Index(name = "idx_b2bunit_admin", columnList = "admin_user_id"),
                @Index(name = "idx_b2bunit_category", columnList = "category_id")
        }
)
@Getter
@Setter
public class B2BUnitModel extends AuditableModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "admin_user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_b2bunit_admin")
    )
    private UserModel admin;

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @Column(name = "business_code", nullable = false, length = 50)
    private String businessCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false, length = 50)
    private BusinessType type = BusinessType.OTHER;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "category_id",
            foreignKey = @ForeignKey(name = "fk_b2bunit_category")
    )
    private BusinessCategoryModel category;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "business_latitude")
    private BigDecimal businessLatitude;

    @Column(name = "business_longitude")
    private BigDecimal businessLongitude;

    @OneToMany(
            mappedBy = "b2bUnit",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<BusinessAttributeModel> attributes;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(
            name = "address_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_b2bunit_address")
    )
    private AddressModel businessAddress;

    @Column(name = "enabled", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean enabled = Boolean.FALSE;

    @Column(name = "is_temporarily_closed", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    private Boolean temporarilyClosed = Boolean.FALSE;
}
