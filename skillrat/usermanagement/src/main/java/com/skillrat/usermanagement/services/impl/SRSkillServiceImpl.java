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


    private UserModel getUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    private SkillModel findOrCreateSkill(String name) {
        return skillRepository.findByNameIgnoreCase(name)
                .orElseGet(() -> skillRepository.save(new SkillModel(name)));
    }

    private List<SkillDTO> mapUserSkills(UserModel user) {
        return user.getSkills().stream()
                .map(skillPopulator::toDTO)
                .collect(Collectors.toList());
    }


    @Override
    public SkillDTO saveSkill(SkillDTO dto) {
        SkillModel skill = (dto.getId() != null)
                ? skillRepository.findById(dto.getId())
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND))
                : new SkillModel();

        skill.setName(dto.getName());
        return skillPopulator.toDTO(skillRepository.save(skill));
    }

    @Override
    public SkillDTO getSkillById(Long id) {
        return skillPopulator.toDTO(skillRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND)));
    }

    @Override
    public Page<SkillDTO> getAllSkills(String search, Pageable pageable) {
        Page<SkillModel> skills = (search == null || search.isBlank())
                ? skillRepository.findAll(pageable)
                : skillRepository.findByNameContainingIgnoreCase(search, pageable);
        return skills.map(skillPopulator::toDTO);
    }

    @Override
    public void deleteSkill(Long id) {
        SkillModel skill = skillRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));
        skillRepository.delete(skill);
    }


    @Override
    public List<SkillDTO> addOrAssignSkill(Long userId, String skillName) {
        UserModel user = getUser(userId);
        SkillModel skill = findOrCreateSkill(skillName);

        if (user.getSkills().add(skill)) {
            userRepository.save(user);
        }

        return mapUserSkills(user);
    }

    @Override
    public List<SkillDTO> addOrAssignSkills(Long userId, List<String> skillNames) {
        UserModel user = getUser(userId);

        boolean modified = false;
        for (String name : skillNames) {
            SkillModel skill = findOrCreateSkill(name);
            modified |= user.getSkills().add(skill);
        }

        if (modified) {
            userRepository.save(user);
        }

        return mapUserSkills(user);
    }

    @Override
    public List<SkillDTO> removeSkill(Long userId, Long skillId) {
        UserModel user = getUser(userId);
        SkillModel skill = skillRepository.findById(skillId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.SKILL_NOT_FOUND));

        if (user.getSkills().remove(skill)) {
            userRepository.save(user);
        }

        return mapUserSkills(user);
    }

    @Override
    public List<SkillDTO> getUserSkills(Long userId) {
        return mapUserSkills(getUser(userId));
    }

    @Override
    public Page<SkillDTO> searchAndAssignSkill(Long userId, String search, Pageable pageable) {
        UserModel user = getUser(userId);

        if (search != null && !search.isBlank()) {
            SkillModel skill = findOrCreateSkill(search);
            if (user.getSkills().add(skill)) {
                userRepository.save(user);
            }
        }

        return skillRepository.findByNameContainingIgnoreCase(search, pageable)
                .map(skillPopulator::toDTO);
    }
}
