package com.hlt.usermanagement.azure.service.impl;

import com.hlt.usermanagement.azure.service.AwsBlobService;
import com.hlt.usermanagement.model.MediaModel;
import com.hlt.usermanagement.model.UserModel;
import com.hlt.usermanagement.services.MediaService;
import com.hlt.usermanagement.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class AwsBlobServiceImpl implements AwsBlobService {

    private static final String PROFILE_PICTURE = "PROFILE_PICTURE";

    @Value("${secretKey}")
    private String secretKey;

    @Value("${accessKey}")
    private String accessKey;

    @Value("${bucketName}")
    private String bucketName;

    @Value("${region}")
    private String region;

    @Autowired
    @Lazy
    private MediaService mediaService;

    @Autowired
    private UserService userService;

    @Override
    public S3Client getClient() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        return S3Client.builder()
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .region(Region.of(region))
                .build();
    }

    @Override
    public MediaModel uploadFile(MultipartFile file) throws IOException {
        MediaModel mediaModel = new MediaModel();
        File tempFile = convertMultipartFileToFile(file);
        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

        PutObjectRequest putRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .build();

        getClient().putObject(putRequest, RequestBody.fromFile(tempFile));
        tempFile.delete();

        mediaModel.setFileName(fileName);
        mediaModel.setUrl(getS3Url(fileName));
        return mediaService.saveMedia(mediaModel);
    }

    @Override
    public List<MediaModel> uploadFiles(List<MultipartFile> files) throws IOException {
        List<MediaModel> uploadFiles = new ArrayList<>();
        for (MultipartFile file : files) {
            MediaModel media = uploadFile(file);
            uploadFiles.add(media);
        }
        return uploadFiles;
    }

    @Override
    public MediaModel uploadCustomerPictureFile(Long customerId, MultipartFile file, Long createdUser)
            throws IOException {
        UserModel userModel = userService.findById(customerId);
        if (userModel != null) {
            MediaModel picture = mediaService.findByJtcustomerAndMediaType(customerId, PROFILE_PICTURE);
            if (picture == null) {
                picture = new MediaModel();
            }

            File tempFile = convertMultipartFileToFile(file);
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();

            PutObjectRequest putRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(fileName)
                    .build();

            getClient().putObject(putRequest, RequestBody.fromFile(tempFile));
            tempFile.delete();

            picture.setUrl(getS3Url(fileName));
            picture.setCustomerId(customerId);
            picture.setMediaType(PROFILE_PICTURE);
            picture.setCreatedBy(createdUser);
            return mediaService.saveMedia(picture);
        }
        return null;
    }

    private String getS3Url(String fileName) {
        return "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + fileName;
    }

    private File convertMultipartFileToFile(MultipartFile file) throws IOException {
        File convFile = new File(file.getOriginalFilename());
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }

    private String getFileExtension(String fileName) {
        if (fileName != null && fileName.contains(".")) {
            return fileName.substring(fileName.lastIndexOf('.'));
        }
        return "";
    }
}
