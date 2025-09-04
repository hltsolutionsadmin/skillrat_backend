package com.skillrat.usermanagement.populator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.utils.Populator;

@Component
public class ExperiencePopulator implements Populator<ExperienceModel, ExperienceDTO> {

	@Autowired
	private EducationPopulator educationPopulator;

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
			List<EducationDTO> dtos = new ArrayList<EducationDTO>();
			for (EducationModel model : source.getEducation()) {
				EducationDTO dto = new EducationDTO();
				educationPopulator.populate(model, dto);
				dtos.add(dto);
			}
			target.setAcademics(dtos);
		}

		target.setStartDate(source.getStartDate());
		target.setEndDate(source.getEndDate());
	}
}
