package com.skillrat.usermanagement.populator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.skillrat.usermanagement.dto.EducationDTO;
import com.skillrat.usermanagement.dto.ExperienceDTO;
import com.skillrat.usermanagement.dto.InternshipOrJobDTO;
import com.skillrat.usermanagement.model.EducationModel;
import com.skillrat.usermanagement.model.ExperienceModel;
import com.skillrat.usermanagement.model.InternshipOrJobModel;
import com.skillrat.utils.Populator;

@Component
public class ExperiencePopulator implements Populator<ExperienceModel, ExperienceDTO> {

    private final EducationPopulator educationPopulator;
    private final InternshipOrJobPopulator internshipOrJobPopulator;

    @Autowired
    public ExperiencePopulator(EducationPopulator educationPopulator,
                               InternshipOrJobPopulator internshipOrJobPopulator) {
        this.educationPopulator = educationPopulator;
        this.internshipOrJobPopulator = internshipOrJobPopulator;
    }

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

        // === Education mapping ===
        if (!CollectionUtils.isEmpty(source.getEducation())) {
            List<EducationDTO> dtos = new ArrayList<>();
            for (EducationModel model : source.getEducation()) {
                dtos.add(educationPopulator.toDTO(model));
            }
            target.setAcademics(dtos);
        }

        // === Internships & Jobs mapping ===
        if (!CollectionUtils.isEmpty(source.getInternshipsAndJobs())) {
            List<InternshipOrJobDTO> internshipDtos = new ArrayList<>();
            for (InternshipOrJobModel model : source.getInternshipsAndJobs()) {
                internshipDtos.add(internshipOrJobPopulator.toDTO(model));
            }
            target.setInternships(internshipDtos);
        }

        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
    }

    // === Helper wrappers for service layer ===
    public EducationDTO toEducationDTO(EducationModel source) {
        return educationPopulator.toDTO(source);
    }

    public InternshipOrJobDTO toInternshipOrJobDTO(InternshipOrJobModel source) {
        return internshipOrJobPopulator.toDTO(source);
    }
}

