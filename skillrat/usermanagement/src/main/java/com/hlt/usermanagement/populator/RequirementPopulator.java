package com.hlt.usermanagement.populator;

import com.hlt.usermanagement.dto.RequirementDTO;
import com.hlt.usermanagement.model.RequirementModel;
import com.hlt.utils.Populator;
import org.springframework.stereotype.Component;

@Component
public class RequirementPopulator implements Populator<RequirementModel, RequirementDTO> {

    @Override
    public void populate(RequirementModel source, RequirementDTO target) {
        if (source == null || target == null) {
            return;
        }

        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setLocation(source.getLocation());
        target.setIsActive(source.getIsActive());

        if (source.getB2bUnit() != null) {
            target.setB2bUnitId(source.getB2bUnit().getId());
        }
        if (source.getCreatedBy() != null) {
            target.setCreatedByUserId(source.getCreatedBy().getId());
        }

        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());

        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
    }
}
