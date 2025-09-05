package com.skillrat.usermanagement.azure.service.impl;

import com.azure.storage.blob.*;
import com.skillrat.usermanagement.azure.service.AzureBlobService;
import com.skillrat.usermanagement.model.MediaModel;
import com.skillrat.usermanagement.model.UserModel;
import com.skillrat.usermanagement.services.MediaService;
import com.skillrat.usermanagement.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AzureBlobServiceImpl implements AzureBlobService {

    private static final String PROFILE_PICTURE = "PROFILE_PICTURE";

    @Value("${azure.storage.account-name}")
    private String accountName;

    @Value("${azure.storage.container-name}")
    private String containerName;

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Lazy
    private final MediaService mediaService;

    private final UserService userService;

    private BlobContainerClient getContainerClient() {
        BlobServiceClient blobServiceClient =
                new BlobServiceClientBuilder().connectionString(connectionString).buildClient();

        BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
        }
        return containerClient;
    }

    @Override
    public MediaModel uploadFile(MultipartFile file) throws IOException {
        MediaModel mediaModel = new MediaModel();
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        BlobClient blobClient = getContainerClient().getBlobClient(fileName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        mediaModel.setFileName(fileName);
        mediaModel.setUrl(getBlobUrl(fileName));
        return mediaService.saveMedia(mediaModel);
    }

    @Override
    public List<MediaModel> uploadFiles(List<MultipartFile> files) throws IOException {
        List<MediaModel> uploadedFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            uploadedFiles.add(uploadFile(file));
        }
        return uploadedFiles;
    }

    @Override
    public MediaModel uploadCustomerPictureFile(Long customerId, MultipartFile file, Long createdUser)
            throws IOException {

        UserModel userModel = userService.findById(customerId);
        if (userModel == null) {
            return null;
        }

        MediaModel picture = mediaService.findByJtcustomerAndMediaType(customerId, PROFILE_PICTURE);
        if (picture == null) {
            picture = new MediaModel();
        }

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        BlobClient blobClient = getContainerClient().getBlobClient(fileName);
        blobClient.upload(file.getInputStream(), file.getSize(), true);

        picture.setUrl(getBlobUrl(fileName));
        picture.setCustomerId(customerId);
        picture.setMediaType(PROFILE_PICTURE);
        picture.setCreatedBy(createdUser);

        return mediaService.saveMedia(picture);
    }

    private String getBlobUrl(String fileName) {
        return "https://" + accountName + ".blob.core.windows.net/" + containerName + "/" + fileName;
    }
}
