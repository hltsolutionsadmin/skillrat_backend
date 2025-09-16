package com.skillrat.usermanagement.services.impl;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.AddressDTO;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.model.AddressModel;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.RequirementModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.populator.SRRequirementPopulator;
import com.skillrat.usermanagement.repository.ApplicationRepository;
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

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SRRequirementServiceImpl implements SRRequirementService {

    private final SRRequirementRepository srRequirementRepository;
    private final SRRequirementPopulator srRequirementPopulator;
    private final B2BUnitRepository b2BUnitRepository;
    private final UserRepository userRepository;
    private final ApplicationRepository applicationRepository;
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
        model.setDesignation(dto.getDesignation());
        model.setDescription(dto.getDescription());
        model.setType(dto.getType());
        model.setIsActive(dto.getIsActive());
        model.setStartDate(dto.getStartDate());
        model.setEndDate(dto.getEndDate());

        model.setEligibilityCriteria(dto.getEligibilityCriteria());
        model.setResponsibilities(dto.getResponsibilities());
        model.setSkillsRequired(dto.getSkillsRequired());
        model.setBenefits(dto.getBenefits());
        model.setCode(dto.getCode());
        model.setB2bUnit(fetchB2BUnit(dto.getB2bUnitId()));
        model.setCreatedBy(fetchCurrentUser());

        if (dto.getAddresses() != null && !dto.getAddresses().isEmpty()) {
            List<AddressModel> addressModels = dto.getAddresses().stream()
                    .map(this::convertAddressDTOToModel)
                    .toList();
            model.setAddresses(addressModels);
        }


        return model;
    }

    private AddressModel convertAddressDTOToModel(AddressDTO dto) {
        if (dto == null) return null;

        AddressModel model = new AddressModel();
        model.setId(dto.getId());
        model.setAddressLine1(dto.getAddressLine1());
        model.setAddressLine2(dto.getAddressLine2());
        model.setStreet(dto.getStreet());
        model.setCity(dto.getCity());
        model.setState(dto.getState());
        model.setCountry(dto.getCountry());
        model.setPostalCode(dto.getPostalCode());
        model.setLatitude(dto.getLatitude());
        model.setLongitude(dto.getLongitude());
        model.setIsDefault(dto.getIsDefault());

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
    public Page<RequirementDTO> getAllRequirements(Pageable pageable, Long userId) {
        return srRequirementRepository.findAll(pageable)
                .map(model -> {
                    RequirementDTO dto = new RequirementDTO();
                    srRequirementPopulator.populate(model, dto);

                    // Check if the user has applied to this requirement
                    boolean applied = applicationRepository.existsByRequirementIdAndApplicantId(model.getId(), userId);
                    dto.setApplied(applied);

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