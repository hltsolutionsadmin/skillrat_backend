package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.dto.SkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SRSkillService {

    SkillDTO saveSkill(SkillDTO dto);

    SkillDTO getSkillById(Long id);

    Page<SkillDTO> getAllSkills(Pageable pageable);

    void deleteSkill(Long id);
}
