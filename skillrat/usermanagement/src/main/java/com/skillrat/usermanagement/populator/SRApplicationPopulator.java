package com.skillrat.usermanagement.populator;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.dto.MediaDTO;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.dto.AddressDTO;
import com.skillrat.usermanagement.model.*;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.SRRequirementRepository;
import com.skillrat.usermanagement.services.UserService;
import com.skillrat.utils.Populator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SRApplicationPopulator implements Populator<ApplicationModel, ApplicationDTO> {

    private final UserService userService;
    private final SRRequirementRepository requirementRepository;
    private final B2BUnitRepository b2bUnitRepository;

    @Override
    public void populate(ApplicationModel source, ApplicationDTO target) {
        if (source == null || target == null) return;

        target.setId(source.getId());
        target.setStatus(source.getStatus());
        target.setCoverLetter(source.getCoverLetter());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());

        if (source.getB2bUnit() != null) target.setB2bUnitId(source.getB2bUnit().getId());

        if (source.getApplicant() != null) target.setApplicantUserId(source.getApplicant().getId());

        if (source.getMediaFiles() != null && !source.getMediaFiles().isEmpty()) {
            Set<MediaDTO> mediaDTOs = source.getMediaFiles().stream()
                    .map(this::convertMedia)
                    .collect(Collectors.toSet());
            target.setMediaFiles(mediaDTOs);
        }

        if (source.getRequirement() != null) {
            target.setRequirementId(source.getRequirement().getId());
            RequirementDTO reqDTO = new RequirementDTO();
            populateRequirement(source.getRequirement(), reqDTO);
            target.setRequirement(reqDTO);
        }
    }

    public ApplicationModel populate(ApplicationDTO dto, ApplicationModel target) {
        if (dto == null || target == null) return target;

        target.setId(dto.getId());
        target.setStatus(dto.getStatus());
        target.setCoverLetter(dto.getCoverLetter());

        if (dto.getApplicantUserId() != null) {
            UserModel applicant = userService.findById(dto.getApplicantUserId());
            if (applicant == null) {
                throw new HltCustomerException(ErrorCode.USER_NOT_FOUND);
            }
            target.setApplicant(applicant);
        }

        if (dto.getRequirementId() != null) {
            RequirementModel requirement = requirementRepository.findById(dto.getRequirementId())
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.REQUIREMENT_NOT_FOUND));
            target.setRequirement(requirement);

            if (dto.getB2bUnitId() == null && requirement.getB2bUnit() != null) {
                target.setB2bUnit(requirement.getB2bUnit());
            }
        }

        if (dto.getB2bUnitId() != null) {
            B2BUnitModel b2bUnit = b2bUnitRepository.findById(dto.getB2bUnitId())
                    .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));
            target.setB2bUnit(b2bUnit);
        }

        return target;
    }

    private MediaDTO convertMedia(MediaModel media) {
        MediaDTO dto = new MediaDTO();
        dto.setId(media.getId());
        dto.setMediaUrls(List.of(media.getUrl()));
        dto.setMediaType(media.getMediaType());
        return dto;
    }

    private void populateRequirement(RequirementModel source, RequirementDTO target) {
        if (source == null || target == null) return;

        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setDesignation(source.getDesignation());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setIsActive(source.getIsActive());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setCreatedByUserId(source.getCreatedBy() != null ? source.getCreatedBy().getId() : null);
        target.setB2bUnitId(source.getB2bUnit() != null ? source.getB2bUnit().getId() : null);
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());

        target.setSkillsRequired(source.getSkillsRequired());
        target.setCode(source.getCode());
        target.setBusinessName(source.getBusinessName());
        target.setDepartment(source.getDepartment());
        target.setStipend(source.getStipend());
        target.setRemote(source.getRemote());
        target.setEligibilityCriteria(source.getEligibilityCriteria());
        target.setResponsibilities(source.getResponsibilities());
        target.setBenefits(source.getBenefits());

        if (source.getAddresses() != null && !source.getAddresses().isEmpty()) {
            List<AddressDTO> addresses = source.getAddresses().stream()
                    .map(addr -> {
                        AddressDTO dto = new AddressDTO();
                        dto.setId(addr.getId());
                        dto.setStreet(addr.getStreet());
                        dto.setCity(addr.getCity());
                        dto.setState(addr.getState());
                        dto.setCountry(addr.getCountry());
                        return dto;
                    })
                    .toList();
            target.setAddresses(addresses);
        }

    }
}
