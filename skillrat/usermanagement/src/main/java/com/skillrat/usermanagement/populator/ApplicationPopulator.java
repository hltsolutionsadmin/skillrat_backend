package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.dto.MediaDTO;
import com.skillrat.usermanagement.dto.RequirementDTO;
import com.skillrat.usermanagement.model.ApplicationModel;
import com.skillrat.usermanagement.model.MediaModel;
import com.skillrat.usermanagement.model.RequirementModel;
import com.skillrat.utils.Populator;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class ApplicationPopulator implements Populator<ApplicationModel, ApplicationDTO> {

    @Override
    public void populate(ApplicationModel source, ApplicationDTO target) {
        if (source == null || target == null) {
            return;
        }

        // Basic fields
        target.setId(source.getId());
        target.setStatus(source.getStatus());
        target.setCoverLetter(source.getCoverLetter());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());

        // B2B Unit
        if (source.getB2bUnit() != null) {
            target.setB2bUnitId(source.getB2bUnit().getId());
        }

        // Applicant
        if (source.getApplicant() != null) {
            target.setApplicantUserId(source.getApplicant().getId());
        }

        // Media files
        if (source.getMediaFiles() != null && !source.getMediaFiles().isEmpty()) {
            Set<MediaDTO> mediaDTOs = source.getMediaFiles().stream()
                    .map(this::convertMedia)
                    .collect(Collectors.toSet());
            target.setMediaFiles(mediaDTOs);
        }

        // Requirement (populate full RequirementDTO)
        if (source.getRequirement() != null) {
            RequirementDTO reqDTO = new RequirementDTO();
            populateRequirement(source.getRequirement(), reqDTO);
            target.setRequirement(reqDTO);
        }
    }

    private MediaDTO convertMedia(MediaModel media) {
        MediaDTO dto = new MediaDTO();
        dto.setId(media.getId());
        dto.setMediaUrls(List.of(media.getUrl())); // single URL as list
        dto.setMediaType(media.getMediaType());
        return dto;
    }

    private void populateRequirement(RequirementModel source, RequirementDTO target) {
        if (source == null || target == null) {
            return;
        }
        target.setId(source.getId());
        target.setTitle(source.getTitle());
        target.setDesignation(source.getDesignation());
        target.setDescription(source.getDescription());
        target.setType(source.getType());
        target.setLocation(source.getLocation());
        target.setIsActive(source.getIsActive());
        target.setStartDate(source.getStartDate());
        target.setEndDate(source.getEndDate());
        target.setCreatedByUserId(source.getCreatedBy() != null ? source.getCreatedBy().getId() : null);
        target.setB2bUnitId(source.getB2bUnit() != null ? source.getB2bUnit().getId() : null);
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());
    }
}
