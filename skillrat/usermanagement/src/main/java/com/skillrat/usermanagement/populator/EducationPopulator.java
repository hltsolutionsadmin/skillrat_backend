package com.skillrat.usermanagement.populator;

import org.springframework.stereotype.Component;

import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.utils.Populator;

@Component
public class EducationPopulator implements Populator<EducationModel, EducationDTO> {

	@Override
	public void populate(EducationModel source, EducationDTO target) {
		if (source == null || target == null) {
			return;
		}

		target.setId(source.getId());
		target.setInstitution(source.getInstitution());
		target.setCgpa(source.getCgpa());
		target.setMarks(source.getMarks());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());


        if (source.getEducationLevel() != null) {
			target.setLevel(source.getEducationLevel().name());
		}

	}

	public EducationDTO toDTO(EducationModel source) {
		if (source == null) {
			return null;
		}
		EducationDTO dto = new EducationDTO();
		populate(source, dto);
		return dto;
	}
}
