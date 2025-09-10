package com.skillrat.usermanagement.populator;

import org.springframework.stereotype.Component;

import com.skillrat.usermanagement.dto.InternshipDTO;
import com.skillrat.usermanagement.model.InternshipModel;
import com.skillrat.utils.Populator;

@Component
public class InternshipPopulator implements Populator<InternshipModel, InternshipDTO> {

    @Override
    public void populate(InternshipModel source, InternshipDTO target) {
        if (source == null || target == null) {
            return;
        }
        target.setId(source.getId());
        target.setCompanyName(source.getCompanyName());
        target.setRole(source.getRole());
        target.setDescription(source.getDescription());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
    }

    public InternshipDTO toDTO(InternshipModel source) {
        if (source == null) {
            return null;
        }
        InternshipDTO dto = new InternshipDTO();
        populate(source, dto);
        return dto;
    }
}
