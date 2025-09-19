package com.skillrat.usermanagement.azure.service;

import java.io.IOException;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.skillrat.usermanagement.model.MediaModel;

public interface BlobStorageService {

	MediaModel uploadFile(MultipartFile file) throws IOException;

	List<MediaModel> uploadFiles(List<MultipartFile> files) throws IOException;

	MediaModel uploadCustomerPictureFile(Long userId, MultipartFile file) throws IOException;

}