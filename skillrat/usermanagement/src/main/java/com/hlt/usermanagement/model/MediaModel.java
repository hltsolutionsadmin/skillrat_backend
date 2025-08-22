package com.hlt.usermanagement.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Entity
@Table(name = "media", indexes = {
        @Index(name = "idx_mediaid", columnList = "id", unique = true)
})
@Getter
@Setter
public class MediaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "business_id", nullable = false)
    private B2BUnitModel b2bUnitModel;

    @Column(name = "url", nullable = false)
    private String url;

    @Column(name = "file_name")
    private String fileName;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "description")
    private String description;

    @Column(name = "extension")
    private String extension;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "created_by")
    private Long createdBy;

    @Column(name = "creation_time", nullable = false)
    private Date creationTime;

    @Column(name = "modification_time")
    private Date modificationTime;

    @Column(name = "name")
    private String name;


    @PrePersist
    protected void onCreate() {
        this.creationTime = new Date();
    }

    @PreUpdate
    protected void onUpdate() {
        this.modificationTime = new Date();
    }
}
