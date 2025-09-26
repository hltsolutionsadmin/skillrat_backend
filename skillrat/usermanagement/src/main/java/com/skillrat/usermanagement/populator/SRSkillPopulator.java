package com.skillrat.usermanagement.populator;

import org.springframework.stereotype.Component;
import com.skillrat.usermanagement.dto.SkillDTO;
import com.skillrat.usermanagement.model.SkillModel;
import com.skillrat.utils.Populator;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class SRSkillPopulator implements Populator<SkillModel, SkillDTO> {

    @Override
    public void populate(SkillModel source, SkillDTO target) {
        if (source == null || target == null) {
            return;
        }
        target.setId(source.getId());
        target.setName(source.getName());
    }

    public SkillDTO toDTO(SkillModel source) {
        if (source == null) return null;
        SkillDTO dto = new SkillDTO();
        populate(source, dto);
        return dto;
    }

    public List<SkillDTO> toDTOList(List<SkillModel> sources) {
        if (sources == null || sources.isEmpty()) return Collections.emptyList();
        return sources.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Set<SkillDTO> toDTOSet(Set<SkillModel> sources) {
        if (sources == null || sources.isEmpty()) return Collections.emptySet();
        return sources.stream()
                .map(this::toDTO)
                .collect(Collectors.toSet());
    }

    public SkillModel toModel(SkillDTO dto) {
        if (dto == null) return null;
        SkillModel model = new SkillModel();
        model.setId(dto.getId());
        model.setName(dto.getName());
        return model;
    }
}
