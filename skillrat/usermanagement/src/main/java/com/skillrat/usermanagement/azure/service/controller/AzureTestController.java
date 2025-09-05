
package com.skillrat.usermanagement.azure.service.controller;

import com.skillrat.usermanagement.azure.service.AzureBlobService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/v1/test-azure")
@RequiredArgsConstructor
public class AzureTestController {

    private final AzureBlobService azureBlobService;

    @PostMapping("/upload")
    public ResponseEntity<String> uploadFile(@RequestParam MultipartFile file) throws Exception {
        var media = azureBlobService.uploadFile(file);
        return ResponseEntity.ok("Uploaded! File URL: " + media.getUrl());
    }

    @GetMapping("/ping")
    public String ping() {
        return "Azure Blob Service is working!";
    }
}
