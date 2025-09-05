package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.InternshipOrJobDTO;
import com.skillrat.usermanagement.model.InternshipOrJobModel;
import com.skillrat.utils.Populator;
import org.springframework.stereotype.Component;

@Component
public class InternshipOrJobPopulator implements Populator<InternshipOrJobModel, InternshipOrJobDTO> {

    @Override
    public void populate(InternshipOrJobModel source, InternshipOrJobDTO target) {
        if (source == null || target == null) return;
        target.setId(source.getId());
        target.setCompanyName(source.getCompanyName());
        target.setRoleTitle(source.getRoleTitle());
    }

    public InternshipOrJobDTO toDTO(InternshipOrJobModel source) {
        if (source == null) return null;
        var dto = new InternshipOrJobDTO();
        populate(source, dto);
        return dto;
    }
}
