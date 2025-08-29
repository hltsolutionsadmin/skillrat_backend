package com.skillrat.usermanagement.model;

import java.util.List;

import com.skillrat.usermanagement.dto.enums.ApplicationStatus;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "application", uniqueConstraints = {
		@UniqueConstraint(name = "uk_application_requirement_applicant", columnNames = { "requirement_id",
				"applicant_user_id" }) })
@Getter
@Setter
public class ApplicationModel extends GenericModel {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "application_id", updatable = false, nullable = false)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "b2b_unit_id", nullable = false, foreignKey = @ForeignKey(name = "fk_application_b2bunit"))
	private B2BUnitModel b2bUnit;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "requirement_id", nullable = false, foreignKey = @ForeignKey(name = "fk_application_requirement"))
	private RequirementModel requirement;

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "applicant_user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_application_applicant"))
	private UserModel applicant;

	@Enumerated(EnumType.STRING)
	@Column(name = "status", nullable = false, length = 50)
	private ApplicationStatus status = ApplicationStatus.PENDING;

	@Column(name = "cover_letter", columnDefinition = "TEXT")
	private String coverLetter;

	/**
	 * Media references for resumes, portfolios, etc.
	 */
	@OneToMany
	@JoinTable(name = "application_media", joinColumns = @JoinColumn(name = "application_id"), inverseJoinColumns = @JoinColumn(name = "media_id"))
	private List<MediaModel> mediaFiles;

}
