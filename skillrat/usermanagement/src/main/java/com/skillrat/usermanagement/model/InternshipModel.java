package com.skillrat.usermanagement.model;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "INTERNSHIP")
@Getter
@Setter
public class InternshipModel extends GenericModel {

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "USER_ID", nullable = false)
    private UserModel user;

    @Column(name = "COMPANY_NAME", length = 1000, nullable = false)
    private String companyName;

    @Column(name = "ROLE", length = 500, nullable = false)
    private String role;

    @Column(name = "DESCRIPTION", length = 2000, nullable = true)
    private String description;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "END_DATE", nullable = false)
    private LocalDate endDate;
}
