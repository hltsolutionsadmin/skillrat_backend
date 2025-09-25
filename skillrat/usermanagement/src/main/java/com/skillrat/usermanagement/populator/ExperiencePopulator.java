package com.skillrat.usermanagement.populator;

import java.util.ArrayList;
import java.util.List;

import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.dto.JobDTO;
import com.skillrat.usermanagement.dto.SkillDTO;

import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.usermanagement.model.InternshipModel;
import com.skillrat.usermanagement.model.JobModel;
import com.skillrat.usermanagement.model.SkillModel;

import com.skillrat.utils.Populator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

@Component
@RequiredArgsConstructor
public class ExperiencePopulator implements Populator<ExperienceModel, ExperienceDTO> {

	private final EducationPopulator educationPopulator;
	private final InternshipPopulator internshipPopulator;
	private final JobPopulator jobPopulator;
	private final SRSkillPopulator skillPopulator;

	public ExperienceDTO toDTO(ExperienceModel source) {
		if (source == null) return null;
		ExperienceDTO dto = new ExperienceDTO();
		populate(source, dto);
		return dto;
	}

	@Override
	public void populate(ExperienceModel source, ExperienceDTO target) {
		if (source == null) return;

		target.setId(source.getId());
		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());

		if (source.getType() != null) {
			target.setType(source.getType().name());
		}

		if (source.getUser() != null) {
			target.setUserId(source.getUser().getId());
		}

		if (source.getB2bUnit() != null) {
			target.setB2bUnitId(source.getB2bUnit().getId());
		}

		// Education
		if (!CollectionUtils.isEmpty(source.getEducation())) {
			List<EducationDTO> educationDTOs = new ArrayList<>();
			for (EducationModel model : source.getEducation()) {
				EducationDTO dto = new EducationDTO();
				educationPopulator.populate(model, dto);
				educationDTOs.add(dto);
			}
			target.setAcademics(educationDTOs);
		}

		// Internships
		if (!CollectionUtils.isEmpty(source.getInternships())) {
			List<InternshipDTO> internshipDTOs = new ArrayList<>();
			for (InternshipModel model : source.getInternships()) {
				InternshipDTO dto = new InternshipDTO();
				internshipPopulator.populate(model, dto);
				internshipDTOs.add(dto);
			}
			target.setInternships(internshipDTOs);
		}

		// Jobs
		if (!CollectionUtils.isEmpty(source.getJobs())) {
			List<JobDTO> jobDTOs = new ArrayList<>();
			for (JobModel model : source.getJobs()) {
				JobDTO dto = new JobDTO();
				jobPopulator.populate(model, dto);
				jobDTOs.add(dto);
			}
			target.setJobs(jobDTOs);
		}

		// Skills
		if (!CollectionUtils.isEmpty(source.getSkills())) {
			List<SkillDTO> skillDTOs = new ArrayList<>();
			for (SkillModel skill : source.getSkills()) {
				SkillDTO dto = new SkillDTO();
				skillPopulator.populate(skill, dto);
				skillDTOs.add(dto);
			}
			target.setSkills(skillDTOs);
		}
	}
}
