package com.skillrat.usermanagement.services.impl;

import org.springframework.stereotype.Service;

import com.skillrat.auth.exception.handling.ErrorCode;
import com.skillrat.auth.exception.handling.HltCustomerException;
import com.skillrat.usermanagement.dto.MediaDTO;
import com.skillrat.usermanagement.model.B2BUnitModel;
import com.skillrat.usermanagement.model.MediaModel;
import com.skillrat.usermanagement.populator.MediaPopulator;
import com.skillrat.usermanagement.repository.B2BUnitRepository;
import com.skillrat.usermanagement.repository.MediaRepository;
import com.skillrat.usermanagement.services.MediaService;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class MediaServiceImpl implements MediaService {

    private final MediaRepository mediaRepository;
    private final B2BUnitRepository b2bUnitRepository;
    private final MediaPopulator mediaPopulator;


    @Override
    @Transactional
    public MediaModel saveMedia(MediaModel mediaModel) {
        return mediaRepository.save(mediaModel);
    }

    @Override
    public MediaModel findByJtcustomerAndMediaType(Long userId, String mediaType) {
        return mediaRepository.findByCustomerIdAndMediaType(userId, mediaType);
    }

    @Override
    public void uploadMedia(Long b2bUnitId, MediaDTO dto) {
        B2BUnitModel b2b = b2bUnitRepository.findById(b2bUnitId)
                .orElseThrow(() -> new HltCustomerException(ErrorCode.BUSINESS_NOT_FOUND));

        MediaModel media = new MediaModel();
//        media.setB2bUnitModel(b2b);
        media.setUrl(dto.getUrl());
        media.setFileName(dto.getFileName());
        media.setMediaType(dto.getMediaType());
        media.setDescription(dto.getDescription());
        media.setExtension(dto.getExtension());
        media.setActive(dto.isActive());
        media.setCreatedBy(dto.getCreatedBy());
        media.setCustomerId(dto.getCustomerId());

        mediaRepository.save(media);
    }
}


