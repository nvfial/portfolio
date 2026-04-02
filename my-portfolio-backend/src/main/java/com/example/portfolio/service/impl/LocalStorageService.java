package com.example.portfolio.service.impl;

import com.example.portfolio.service.StorageService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class LocalStorageService implements StorageService {
    
    @Value("${storage.local.path:./uploads/resources}")
    private String storagePath;
    
    @Value("${storage.local.base-url:http://localhost:8080/uploads/resources}")
    private String baseUrl;
    
    @PostConstruct
    public void init() {
        try {
            Path path = Paths.get(storagePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot create storage directory", e);
        }
    }
    
    @Override
    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }
        
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        
        String filename = UUID.randomUUID().toString() + extension;
        String datePath = java.time.LocalDate.now().toString();
        String fullPath = storagePath + "/" + datePath + "/" + filename;
        
        try {
            Path path = Paths.get(storagePath, datePath);
            if (!Files.exists(path)) {
                Files.createDirectories(path);
            }
            
            Files.write(Paths.get(fullPath), file.getBytes());
            return datePath + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to upload file", e);
        }
    }
    
    @Override
    public void delete(String path) {
        try {
            Files.deleteIfExists(Paths.get(storagePath, path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete file", e);
        }
    }
    
    @Override
    public String getUrl(String path) {
        return baseUrl + "/" + path;
    }
    
    @Override
    public String getPreviewUrl(String path, String fileType) {
        String url = getUrl(path);
        if (fileType == null) return url;
        
        String lowerType = fileType.toLowerCase();
        if (lowerType.matches("jpg|jpeg|png|gif|webp|bmp")) {
            return url;
        } else if (lowerType.matches("mp3|wav|flac|aac|ogg|m4a")) {
            return url;
        } else if (lowerType.matches("mp4|webm|mkv|avi")) {
            return url;
        }
        return null;
    }
}