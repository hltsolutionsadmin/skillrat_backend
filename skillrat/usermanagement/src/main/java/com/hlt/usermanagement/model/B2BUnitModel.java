package com.hlt.usermanagement.model;

import com.hlt.usermanagement.dto.enums.BusinessType;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "b2b_unit",
        indexes = {
                @Index(name = "idx_business_name", columnList = "business_name"),
                @Index(name = "idx_business_type", columnList = "business_type"),
                @Index(name = "idx_admin_user_id", columnList = "admin_user_id"),
                @Index(name = "idx_category_id", columnList = "category_id")
        })
@Getter
@Setter
public class B2BUnitModel extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "admin_user_id", nullable = false)
    private UserModel admin;

    @Column(name = "business_code", nullable = false, length = 50, unique = true)
    private String businessCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "business_type", nullable = false, length = 50)
    private BusinessType type = BusinessType.OTHER;

    @Column(name = "business_name", nullable = false, length = 150, unique = true)
    private String businessName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private BusinessCategoryModel category;

    @Column(name = "contact_number", nullable = false, length = 20)
    private String contactNumber;

    @Column(name = "email", length = 150, unique = true)
    private String email;

    @Column(name = "business_latitude")
    private Double businessLatitude;

    @Column(name = "business_longitude")
    private Double businessLongitude;

    @OneToMany(mappedBy = "b2bUnitModel", fetch = FetchType.LAZY,
            cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<BusinessAttributeModel> attributes = new HashSet<>();

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

    public void addAttribute(BusinessAttributeModel attribute) {
        attribute.setB2bUnitModel(this);
        this.attributes.add(attribute);
    }

    public void removeAttribute(BusinessAttributeModel attribute) {
        attribute.setB2bUnitModel(null);
        this.attributes.remove(attribute);
    }
}
