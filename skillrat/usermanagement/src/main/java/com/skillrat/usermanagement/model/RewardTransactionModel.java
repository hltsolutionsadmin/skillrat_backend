package com.skillrat.usermanagement.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

import com.skillrat.usermanagement.dto.enums.RewardEventType;
import com.skillrat.usermanagement.event.RewardEvent;

@Entity
@Table(name = "REWARD_TRANSACTIONS")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RewardTransactionModel extends GenericModel {

    @Column(name = "USER_ID", nullable = false)
    private Long userId;

    @Column(name = "USER_TYPE", nullable = false)
    private String userType;

    @Column(name = "POINTS", nullable = false)
    private Integer points;

    @Column(name = "TRANSACTION_TYPE", nullable = false)
    private String transactionType; // EARNED, REDEEMED

    @Column(name = "EVENT_TYPE", nullable = false)
    private RewardEventType eventType;

    @Column(name = "EVENT_REF_ID")
    private Long eventRefId;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "TRANSACTION_DATE", nullable = false)
    private LocalDateTime transactionDate = LocalDateTime.now();
}
