package com.hlt.usermanagement.model;

import com.hlt.auth.EncryptedStringConverter;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.*;

@Entity
@Table(
        name = "B2B_USER",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"USERNAME"}),
                @UniqueConstraint(columnNames = {"EMAIL"}),
                @UniqueConstraint(columnNames = {"EMAIL_HASH"}),
                @UniqueConstraint(columnNames = {"PRIMARY_CONTACT_HASH"})
        },
        indexes = {
                @Index(name = "idx_username", columnList = "USERNAME", unique = true),
                @Index(name = "idx_email", columnList = "EMAIL", unique = true),
                @Index(name = "idx_email_hash", columnList = "EMAIL_HASH", unique = true),
                @Index(name = "idx_mobile_hash", columnList = "PRIMARY_CONTACT_HASH", unique = true)
        }
)
@Getter
@Setter
public class UserModel extends GenericModel {

    @Column(name = "FULL_NAME", nullable = false)
    private String fullName;

    @Size(max = 20)
    @Column(name = "USERNAME", nullable = false, unique = true)
    private String username;

    @Email
    @Size(max = 50)
    @Column(name = "EMAIL", nullable = false, unique = true)
    private String email;

    @Column(name = "EMAIL_HASH", nullable = false, unique = true)
    private String emailHash;

    @NotBlank
    @Column(name = "PRIMARY_CONTACT", nullable = false)
    private String primaryContact;

    @Column(name = "PRIMARY_CONTACT_HASH", nullable = false, unique = true)
    private String primaryContactHash;

    @Column(name = "GENDER")
    private String gender;

    @Column(name = "PROFILE_PICTURE_ID")
    private Long profilePictureId;

    @Column(name = "FCM_TOKEN")
    private String fcmToken;

    @Column(name = "JUVI_ID")
    private String juviId;

    /** Many-to-Many with RoleModel */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "USER_ROLES",
            joinColumns = @JoinColumn(name = "USER_ID"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private Set<RoleModel> roles = new HashSet<>();

    /** One-to-Many with AddressModel */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AddressModel> addresses = new ArrayList<>();

    @Column(name = "RECENT_ACTIVITY_DATE")
    private LocalDate recentActivityDate;

    @Convert(converter = EncryptedStringConverter.class)
    @Column(name = "PASSWORD", nullable = false)
    private String password;

    @Column(name = "LAST_LOGOUT_DATE")
    private LocalDate lastLogoutDate;

    /** Many-to-One with B2BUnit */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "B2B_UNIT_ID")
    private B2BUnitModel b2bUnit;

    /** One-to-Many with ExperienceModel */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ExperienceModel> experiences = new ArrayList<>();
}
