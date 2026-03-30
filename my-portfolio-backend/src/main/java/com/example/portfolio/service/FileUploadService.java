package com.example.portfolio.service;

import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface FileUploadService {
    
    Map<String, String> uploadImage(MultipartFile file, Long userId);
    
    Map<String, String> uploadCoverImage(MultipartFile file, Long userId);
    
    void deleteFile(String filePath);
    
    String getFileUrl(String filePath);
}