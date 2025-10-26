package com.skillrat.usermanagement.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import software.amazon.awssdk.thirdparty.jackson.core.sym.Name;

@Data
@Entity
@Table(name = "PLACEMENT_CELL", indexes = {
        @Index(name = "idx_placementcellid", columnList = "id", unique = true)})
public class PlacementCellModel extends AuditableModel{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "DESCRIPTION")
    private String description;

    @OneToOne
    @JoinColumn(name = "B2B_UNIT")
    private B2BUnitModel b2bUnit;

    @OneToOne
    @JoinColumn(name = "COORDINATOR")
    private UserModel coordinator;

}
