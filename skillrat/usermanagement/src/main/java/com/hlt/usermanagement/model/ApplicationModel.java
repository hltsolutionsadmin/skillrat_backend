package com.hlt.usermanagement.model;

import com.hlt.usermanagement.dto.enums.ApplicationStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "APPLICATION",
       uniqueConstraints = {
           @UniqueConstraint(columnNames = {"REQUIREMENT_ID", "APPLICANT_USER_ID"})
       })
@Getter
@Setter
public class ApplicationModel extends GenericModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "APPLICATION_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "B2B_UNIT_ID", nullable = false)
    private B2BUnitModel b2bUnit;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REQUIREMENT_ID", nullable = false)
    private RequirementModel requirement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "APPLICANT_USER_ID", nullable = false)
    private UserModel applicant;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 50)
    private ApplicationStatus status = ApplicationStatus.PENDING;

    @Column(name = "COVER_LETTER", columnDefinition = "TEXT")
    private String coverLetter;

    //TODO :add Media for resume, portfolio, etc.

}
