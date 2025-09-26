package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.SkillDTO;
import com.skillrat.usermanagement.model.SkillModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.SRSkillPopulator;
import com.skillrat.usermanagement.repository.SRSkillRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRSkillService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SRSkillServiceImpl implements SRSkillService {

    private final SRSkillRepository skillRepository;
    private final UserRepository userRepository;
    private final SRSkillPopulator skillPopulator;


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
        return skillPopulator.toDTO(saved);
    }

    @Override
    public SkillDTO getSkillById(Long id) {
        SkillModel skill = skillRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));
        return skillPopulator.toDTO(skill);
    }

    @Override
    public Page<SkillDTO> getAllSkills(Pageable pageable) {
        return skillRepository.findAll(pageable)
                .map(skillPopulator::toDTO);
    }

    @Override
    public void deleteSkill(Long id) {
        SkillModel skill = skillRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));
        skillRepository.delete(skill);
    }


    @Override
    public List<SkillDTO> addOrAssignSkill(Long userId, String skillName) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        SkillModel skill = skillRepository.findByNameIgnoreCase(skillName)
                .orElseGet(() -> skillRepository.save(new SkillModel(skillName)));

        if (!user.getSkills().contains(skill)) {
            user.getSkills().add(skill);
            userRepository.save(user);
        }

        return user.getSkills().stream()
                .map(skillPopulator::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillDTO> addOrAssignSkills(Long userId, List<String> skillNames) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        for (String name : skillNames) {
            SkillModel skill = skillRepository.findByNameIgnoreCase(name)
                    .orElseGet(() -> skillRepository.save(new SkillModel(name)));

            user.getSkills().add(skill); // Set ensures no duplicates
        }

        userRepository.save(user);

        return user.getSkills().stream()
                .map(skillPopulator::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillDTO> removeSkill(Long userId, Long skillId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        SkillModel skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));

        user.getSkills().remove(skill);
        userRepository.save(user);

        return user.getSkills().stream()
                .map(skillPopulator::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<SkillDTO> getUserSkills(Long userId) {
        UserModel user = userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));

        return user.getSkills().stream()
                .map(skillPopulator::toDTO)
                .collect(Collectors.toList());
    }
}
