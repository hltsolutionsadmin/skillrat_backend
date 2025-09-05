package com.skillrat.usermanagement.azure.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.skillrat.usermanagement.model.MediaModel;

import java.io.IOException;
import java.util.List;

@Component
public interface AzureBlobService {

    MediaModel uploadFile(MultipartFile file) throws IOException;

    List<MediaModel> uploadFiles(List<MultipartFile> files) throws IOException;

    MediaModel uploadCustomerPictureFile(Long customerId, MultipartFile file, Long createdUser) throws IOException;
}
