package com.skillrat.usermanagement.populator;

import java.util.ArrayList;
import java.util.List;

import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.dto.JobDTO;
import com.skillrat.usermanagement.model.InternshipModel;
import com.skillrat.usermanagement.model.JobModel;
import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.utils.Populator;

@Component
@RequiredArgsConstructor
public class ExperiencePopulator implements Populator<ExperienceModel, ExperienceDTO> {

    private final EducationPopulator educationPopulator;
    private final InternshipPopulator internshipPopulator;
    private final JobPopulator jobPopulator;



    public ExperienceDTO toDTO(ExperienceModel source) {
		if (source == null) {
			return null;
		}
		ExperienceDTO dto = new ExperienceDTO();
		populate(source, dto);
		return dto;
	}

	@Override
	public void populate(ExperienceModel source, ExperienceDTO target) {
		if (source == null) {
			return;
		}

		target.setId(source.getId());

		if (source.getType() != null) {
			target.setType(source.getType().name());
		}

		if (source.getUser() != null) {
			target.setUserId(source.getUser().getId());
		}

		if (source.getB2bUnit() != null) {
			target.setB2bUnitId(source.getB2bUnit().getId());
		}
		if (!CollectionUtils.isEmpty(source.getEducation())) {
			List<EducationDTO> dtos = new ArrayList<>();
			for (EducationModel model : source.getEducation()) {
				EducationDTO dto = new EducationDTO();
				educationPopulator.populate(model, dto);
				dtos.add(dto);
			}
			target.setAcademics(dtos);
		}

        if (!CollectionUtils.isEmpty(source.getInternships())) {
            List<InternshipDTO> dtos = new ArrayList<>();
            for (InternshipModel model : source.getInternships()) {
                InternshipDTO dto = new InternshipDTO();
                internshipPopulator.populate(model, dto);
                dtos.add(dto);
            }
            target.setInternships(dtos);
        }

        if (!CollectionUtils.isEmpty(source.getJobs())) {
            List<JobDTO> dtos = new ArrayList<>();
            for (JobModel model : source.getJobs()) {
                JobDTO dto = new JobDTO();
                jobPopulator.populate(model, dto);
                dtos.add(dto);
            }
            target.setJobs(dtos);
        }


        target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
	}
}
