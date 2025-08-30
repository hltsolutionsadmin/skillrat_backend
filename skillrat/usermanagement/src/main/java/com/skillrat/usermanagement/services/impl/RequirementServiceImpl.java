package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.model.RequirementModel;
import com.skillrat.usermanagement.populator.RequirementPopulator;
import com.skillrat.usermanagement.services.RequirementService;
import com.skillrat.usermanagement.repository.RequirementRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RequirementServiceImpl implements RequirementService {

    private final RequirementRepository requirementRepository;
    private final RequirementPopulator requirementPopulator;

    @Override
    @Transactional
    public RequirementDTO createRequirement(RequirementDTO requirementDTO) {
        RequirementModel model = new RequirementModel();
        model.setTitle(requirementDTO.getTitle());
        model.setDescription(requirementDTO.getDescription());
        model.setType(requirementDTO.getType());
        model.setLocation(requirementDTO.getLocation());
        model.setIsActive(Boolean.TRUE);

        RequirementModel saved = requirementRepository.save(model);
        RequirementDTO response = new RequirementDTO();
        requirementPopulator.populate(saved, response);
        return response;
    }

    @Override
    @Transactional
    public RequirementDTO updateRequirement(Long id, RequirementDTO requirementDTO) {
        RequirementModel model = requirementRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        model.setTitle(requirementDTO.getTitle());
        model.setDescription(requirementDTO.getDescription());
        model.setType(requirementDTO.getType());
        model.setLocation(requirementDTO.getLocation());

        RequirementModel updated = requirementRepository.save(model);
        RequirementDTO response = new RequirementDTO();
        requirementPopulator.populate(updated, response);
        return response;
    }

    @Override
    public RequirementDTO getRequirementById(Long id) {
        RequirementModel model = requirementRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        RequirementDTO response = new RequirementDTO();
        requirementPopulator.populate(model, response);
        return response;
    }

    @Override
    public Page<RequirementDTO> getAllRequirements(Pageable pageable) {
        return requirementRepository.findAll(pageable)
                .map(model -> {
                    RequirementDTO dto = new RequirementDTO();
                    requirementPopulator.populate(model, dto);
                    return dto;
                });
    }

    @Override
    @Transactional
    public void deleteRequirement(Long id) {
        RequirementModel model = requirementRepository.findById(id)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));

        model.setIsActive(false);
        requirementRepository.save(model);
    }
}