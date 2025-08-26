package com.hlt.usermanagement.model;

import com.hlt.commonservice.enums.ERole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(
        name = "ROLES",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"NAME"})
        },
        indexes = {
                @Index(name = "idx_role_id", columnList = "ID", unique = true),
                @Index(name = "idx_role_name", columnList = "NAME", unique = true)
        }
)
@Getter
@Setter
public class RoleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", nullable = false, updatable = false)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "NAME", length = 50, nullable = false, unique = true)
    private ERole name;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    private Set<UserModel> users = new HashSet<>();
}
