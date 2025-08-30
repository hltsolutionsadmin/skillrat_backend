package com.skillrat.usermanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "business_attribute")
@Getter
@Setter
public class BusinessAttributeModel extends GenericModel {

    @Column(name = "attribute_name", nullable = false, length = 100)
    private String attributeName;

    @Column(name = "attribute_value", length = 255)
    private String attributeValue;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_unit_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_businessattribute_b2bunit"))
    private B2BUnitModel b2bUnit;
}
