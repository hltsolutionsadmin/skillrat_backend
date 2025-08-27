package com.skillrat.usermanagement.services;


import com.skillrat.usermanagement.dto.MediaDTO;
import com.skillrat.usermanagement.model.MediaModel;

public interface MediaService {

    MediaModel saveMedia(MediaModel mediaModel);

    MediaModel findByJtcustomerAndMediaType(Long userId, String mediaType);

    void uploadMedia(Long b2bUnitId, MediaDTO dto);





}
