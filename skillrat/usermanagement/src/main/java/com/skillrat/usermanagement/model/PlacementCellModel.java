package com.skillrat.usermanagement.model;

import com.skillrat.usermanagement.dto.enums.PlacementCellStatus;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

/**
 * Represents the Placement Cell of a B2B Unit (e.g., College, School, or Institute).
 * A Placement Cell manages placement-related activities like campus drives,
 * student registrations, and recruiter interactions.
 */
@Entity
@Table(
        name = "placement_cells",
        indexes = {
                @Index(name = "idx_placementcell_b2bunit", columnList = "b2b_unit_id"),
                @Index(name = "idx_placementcell_officer_email", columnList = "placement_officer_email")
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
@ToString(exclude = {"b2bUnit"})
public class PlacementCellModel extends AuditableModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "placement_cell_id")
    private Long id;

    /**
     * Reference to the associated B2B Unit (College, School, etc.)
     * Optional – not every B2B Unit must have a placement cell.
     */

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "b2b_unit_id", unique = true, foreignKey = @ForeignKey(name = "fk_placementcell_b2bunit"))
    private B2BUnitModel b2bUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "placement_officer_id", foreignKey = @ForeignKey(name = "fk_placementcell_user"))
    private UserModel coordinator;

    @Column(name = "total_students_registered")
    private Integer totalStudentsRegistered;

    @Column(name = "year_of_establishment")
    private Integer yearOfEstablishment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private PlacementCellStatus status = PlacementCellStatus.ACTIVE;

    @Column(name = "remarks", length = 255)
    private String remarks;
}
