package com.skillrat.usermanagement.populator;

import com.skillrat.usermanagement.dto.ApplicationDTO;
import com.skillrat.usermanagement.dto.MediaDTO;
import com.skillrat.usermanagement.model.ApplicationModel;
import com.skillrat.usermanagement.model.MediaModel;
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

        target.setId(source.getId());
        target.setStatus(source.getStatus());
        target.setCoverLetter(source.getCoverLetter());
        target.setCreatedAt(source.getCreatedAt());
        target.setUpdatedAt(source.getUpdatedAt());

        if (source.getB2bUnit() != null) {
            target.setB2bUnitId(source.getB2bUnit().getId());
        }

        if (source.getRequirement() != null) {
            target.setRequirementId(source.getRequirement().getId());
        }

        if (source.getApplicant() != null) {
            target.setApplicantUserId(source.getApplicant().getId());
        }

        if (source.getMediaFiles() != null && !source.getMediaFiles().isEmpty()) {
            Set<MediaDTO> mediaDTOs = source.getMediaFiles().stream()
                    .map(this::convertMedia)
                    .collect(Collectors.toSet());
            target.setMediaFiles(mediaDTOs);
        }
    }

    private MediaDTO convertMedia(MediaModel media) {
        MediaDTO dto = new MediaDTO();
        dto.setId(media.getId());
        dto.setMediaUrls(List.of(media.getUrl()));
        dto.setMediaType(media.getMediaType());
        return dto;
    }

}
