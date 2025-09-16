package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.AddressDTO;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.model.RequirementModel;
import com.skillrat.utils.Populator;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class SRRequirementPopulator implements Populator<RequirementModel, RequirementDTO> {

    @Override
    public void populate(RequirementModel source, RequirementDTO target) {
        if (source == null || target == null) {
            return;
        }

        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setDesignation(source.getDesignation());
        target.setIsActive(source.getIsActive());
        target.setSkillsRequired(source.getSkillsRequired());
        target.setCode(source.getCode());
        target.setBusinessName(source.getBusinessName());
        target.setDepartment(source.getDepartment());
        target.setStipend(source.getStipend());
        target.setRemote(source.getRemote());
        target.setEligibilityCriteria(source.getEligibilityCriteria());
        target.setResponsibilities(source.getResponsibilities());
        target.setBenefits(source.getBenefits());

        if (source.getB2bUnit() != null) {
            target.setB2bUnitId(source.getB2bUnit().getId());
        }
        if (source.getCreatedBy() != null) {
            target.setCreatedByUserId(source.getCreatedBy().getId());
        }

        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());

        // Populate addresses if present
        if (source.getAddresses() != null && !source.getAddresses().isEmpty()) {
            target.setAddresses(
                    source.getAddresses().stream().map(addr -> {
                        AddressDTO dto = new AddressDTO();
                        dto.setId(addr.getId());
                        dto.setAddressLine1(addr.getAddressLine1());
                        dto.setAddressLine2(addr.getAddressLine2());
                        dto.setStreet(addr.getStreet());
                        dto.setCity(addr.getCity());
                        dto.setState(addr.getState());
                        dto.setCountry(addr.getCountry());
                        dto.setPostalCode(addr.getPostalCode());
                        dto.setLatitude(addr.getLatitude());
                        dto.setLongitude(addr.getLongitude());
                        dto.setIsDefault(addr.getIsDefault());
                        return dto;
                    }).collect(Collectors.toList())
            );
        }
    }
}
