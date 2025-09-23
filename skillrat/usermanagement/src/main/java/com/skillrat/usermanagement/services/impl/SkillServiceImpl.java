package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.SkillDTO;
import com.skillrat.usermanagement.model.SkillModel;
import com.skillrat.usermanagement.repository.SRSkillRepository;
import com.skillrat.usermanagement.services.SRSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SRSkillServiceImpl implements SRSkillService {

    private final SRSkillRepository skillRepository;

    @Override
    public SkillDTO saveSkill(SkillDTO dto) {
        SkillModel skill;
        if (dto.getId() != null) {
            skill = skillRepository.findById(dto.getId())
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));
            skill.setName(dto.getName());
        } else {
            skill = new SkillModel();
            skill.setName(dto.getName());
        }

        SkillModel saved = skillRepository.save(skill);

        SkillDTO response = new SkillDTO();
        response.setId(saved.getId());
        response.setName(saved.getName());
        return response;
    }

    @Override
    public SkillDTO getSkillById(Long id) {
        SkillModel skill = skillRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));

        SkillDTO dto = new SkillDTO();
        dto.setId(skill.getId());
        dto.setName(skill.getName());
        return dto;
    }

    @Override
    public Page<SkillDTO> getAllSkills(Pageable pageable) {
        return skillRepository.findAll(pageable)
                .map(skill -> {
                    SkillDTO dto = new SkillDTO();
                    dto.setId(skill.getId());
                    dto.setName(skill.getName());
                    return dto;
                });
    }

    @Override
    public void deleteSkill(Long id) {
        SkillModel skill = skillRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));
        skillRepository.delete(skill);
    }
}
