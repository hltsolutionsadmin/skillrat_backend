package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.RequirementModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.SRRequirementPopulator;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.UserRepository;
import com.skillrat.usermanagement.services.SRRequirementService;
import com.skillrat.usermanagement.repository.SRRequirementRepository;
import com.skillrat.utils.SecurityUtils;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class SRRequirementServiceImpl implements SRRequirementService {

    private final SRRequirementRepository srRequirementRepository;
    private final SRRequirementPopulator srRequirementPopulator;
    private final B2BUnitRepository b2BUnitRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public RequirementDTO createRequirement(RequirementDTO requirementDTO) {
        RequirementModel model = mapToModel(requirementDTO);
        RequirementModel saved = srRequirementRepository.save(model);

        RequirementDTO response = new RequirementDTO();
        srRequirementPopulator.populate(saved, response);
        return response;
    }

    private RequirementModel mapToModel(RequirementDTO dto) {
        RequirementModel model = new RequirementModel();
        model.setTitle(dto.getTitle());
        model.setDescription(dto.getDescription());
        model.setType(dto.getType());
        model.setLocation(dto.getLocation());
        model.setIsActive(Boolean.TRUE);

        model.setB2bUnit(fetchB2BUnit(dto.getB2bUnitId()));
        model.setCreatedBy(fetchCurrentUser());
        return model;
    }

    private B2BUnitModel fetchB2BUnit(Long b2bUnitId) {
        return b2BUnitRepository.findById(b2bUnitId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));
    }

    private UserModel fetchCurrentUser() {
        Long userId = SecurityUtils.getCurrentUserDetails().getId();
        return userRepository.findById(userId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Transactional
    public RequirementDTO updateRequirement(Long id, RequirementDTO requirementDTO) {
        RequirementModel model = srRequirementRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        model.setTitle(requirementDTO.getTitle());
        model.setDescription(requirementDTO.getDescription());
        model.setType(requirementDTO.getType());
        model.setLocation(requirementDTO.getLocation());

        RequirementModel updated = srRequirementRepository.save(model);
        RequirementDTO response = new RequirementDTO();
        srRequirementPopulator.populate(updated, response);
        return response;
    }

    @Override
    public RequirementDTO getRequirementById(Long id) {
        RequirementModel model = srRequirementRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        RequirementDTO response = new RequirementDTO();
        srRequirementPopulator.populate(model, response);
        return response;
    }

    @Override
    public Page<RequirementDTO> getAllRequirements(Pageable pageable) {
        return srRequirementRepository.findAll(pageable)
                .map(model -> {
                    RequirementDTO dto = new RequirementDTO();
                    srRequirementPopulator.populate(model, dto);
                    return dto;
                });
    }

    @Override
    @Transactional
    public void deleteRequirement(Long id) {
        RequirementModel model = srRequirementRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        model.setIsActive(false);
        srRequirementRepository.save(model);
    }
}