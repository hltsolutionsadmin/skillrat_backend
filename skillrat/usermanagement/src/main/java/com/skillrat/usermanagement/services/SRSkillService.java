package com.skillrat.usermanagement.services;

import com.skillrat.usermanagement.dto.SkillDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SRSkillService {

    /**
     * Create or update a skill in the catalog
     */
    SkillDTO saveSkill(SkillDTO dto);

    /**
     * Get a skill by its ID
     */
    SkillDTO getSkillById(Long id);

    /**
     * Get all skills with pagination
     */
    Page<SkillDTO> getAllSkills(String search, Pageable pageable);

    /**
     * Delete a skill from the catalog
     */
    void deleteSkill(Long id);

    /**
     * Add or assign a skill to user.
     * If skill exists, reuse it.
     * If not, create and assign.
     * Returns updated skills of the user.
     */
    List<SkillDTO> addOrAssignSkill(Long userId, String skillName);

    /**
     * Add or assign multiple skills to user.
     * Returns updated skills of the user.
     */
    List<SkillDTO> addOrAssignSkills(Long userId, List<String> skillNames);

    /**
     * Remove a skill from a user
     */
    List<SkillDTO> removeSkill(Long userId, Long skillId);

    /**
     * Get all skills of a user
     */
    List<SkillDTO> getUserSkills(Long userId);

    Page<SkillDTO> searchAndAssignSkill(Long userId, String search, Pageable pageable);

}
