package com.example.portfolio.service;

import org.springframework.web.multipart.MultipartFile;

public interface StorageService {
    String upload(MultipartFile file);
    void delete(String path);
    String getUrl(String path);
    String getPreviewUrl(String path, String fileType);
}