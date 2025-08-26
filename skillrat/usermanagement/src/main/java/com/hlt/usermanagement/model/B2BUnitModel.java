package com.hlt.usermanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "b2b_unit",
        indexes = {@Index(name = "idx_business_name", columnList = "business_name")})
@Getter @Setter
public class B2BUnitModel extends AuditableModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "owner_user_id", nullable = false)
    private UserModel owner;  // Business belongs to a user

    @Column(name = "business_name", nullable = false, length = 150)
    private String businessName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BusinessCategoryModel category;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "business_latitude")
    private Double businessLatitude;

    @Column(name = "business_longitude")
    private Double businessLongitude;

    @OneToMany(mappedBy = "b2bUnitModel", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BusinessAttributeModel> attributes;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "address_id", nullable = false)
    private AddressModel businessAddress;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    @Column(name = "is_temporarily_closed", nullable = false)
    private Boolean isTemporarilyClosed = false;

    @Column(name = "creation_date", nullable = false, updatable = false)
    private LocalDateTime creationDate;

    @PrePersist
    protected void onCreate() {
        this.creationDate = LocalDateTime.now();
    }
}
