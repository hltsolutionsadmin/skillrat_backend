package com.skillrat.usermanagement.azure.service;

import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.skillrat.usermanagement.model.MediaModel;

import software.amazon.awssdk.services.s3.S3Client;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

@Component
public interface AwsBlobService {

    // Returns AWS S3 client (SDK v2)
    S3Client getClient();

    MediaModel uploadFile(MultipartFile file) throws FileNotFoundException, IOException;

    List<MediaModel> uploadFiles(List<MultipartFile> files) throws FileNotFoundException, IOException;

    MediaModel uploadCustomerPictureFile(Long customerId, MultipartFile file, Long createdUser)
            throws FileNotFoundException, IOException;
}
