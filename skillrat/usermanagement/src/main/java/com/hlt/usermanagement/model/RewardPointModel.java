package com.hlt.usermanagement.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "REWARD_POINTS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardPointModel extends GenericModel {

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "USER_TYPE", nullable = false)
    private String userType; // STUDENT, EMPLOYEE, COMPANY

    @Column(name = "TOTAL_POINTS", nullable = false)
    private Integer totalPoints = 0;

    @Column(name = "LAST_UPDATED")
    private LocalDateTime lastUpdated;
}
