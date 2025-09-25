package com.skillrat.usermanagement.populator;

import org.springframework.stereotype.Component;
import com.skillrat.usermanagement.dto.SkillDTO;
import com.skillrat.usermanagement.model.SkillModel;
import com.skillrat.utils.Populator;

@Component
public class SRSkillPopulator implements Populator<SkillModel, SkillDTO> {

    @Override
    public void populate(SkillModel source, SkillDTO target) {
        if (source == null || target == null) return;

        target.setId(source.getId());
        target.setName(source.getName());
    }


    public SkillDTO toDTO(SkillModel source) {
        if (source == null) return null;
        SkillDTO dto = new SkillDTO();
        populate(source, dto);
        return dto;
    }
}
