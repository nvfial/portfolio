package com.example.portfolio.service.impl;

import com.example.portfolio.service.FileUploadService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {

    @Value("${app.upload.directory:D:/uploads}")
    private String uploadDirectory;

    @Value("${app.upload.base-url:http://localhost:8080/uploads}")
    private String baseUrl;

    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    private static final String[] ALLOWED_EXTENSIONS = {".jpg", ".jpeg", ".png", ".gif", ".webp"};

    @Override
    public Map<String, String> uploadImage(MultipartFile file, Long userId) {
        return uploadFile(file, "images", userId);
    }

    @Override
    public Map<String, String> uploadCoverImage(MultipartFile file, Long userId) {
        return uploadFile(file, "covers", userId);
    }

    @Override
    public void deleteFile(String filePath) {
        try {
            Path path = Paths.get(uploadDirectory, filePath);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            throw new RuntimeException("删除文件失败: " + e.getMessage());
        }
    }

    @Override
    public String getFileUrl(String filePath) {
        return baseUrl + "/" + filePath;
    }

    private Map<String, String> uploadFile(MultipartFile file, String subDirectory, Long userId) {
        validateFile(file);

        try {
            String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
            String directory = uploadDirectory + "/" + subDirectory + "/" + datePath;
            
            Path dirPath = Paths.get(directory);
            if (!Files.exists(dirPath)) {
                Files.createDirectories(dirPath);
            }

            String originalFilename = file.getOriginalFilename();
            String extension = getFileExtension(originalFilename);
            String newFilename = UUID.randomUUID().toString() + extension;

            Path filePath = dirPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath);

            String relativePath = subDirectory + "/" + datePath + "/" + newFilename;
            String fullUrl = getFileUrl(relativePath);

            Map<String, String> result = new HashMap<>();
            result.put("path", relativePath);
            result.put("url", fullUrl);
            result.put("filename", newFilename);
            result.put("originalName", originalFilename);
            result.put("size", String.valueOf(file.getSize()));

            return result;
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过10MB");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名无效");
        }

        String extension = getFileExtension(originalFilename).toLowerCase();
        boolean allowed = false;
        for (String ext : ALLOWED_EXTENSIONS) {
            if (ext.equals(extension)) {
                allowed = true;
                break;
            }
        }

        if (!allowed) {
            throw new RuntimeException("不支持的文件格式，仅支持: " + String.join(", ", ALLOWED_EXTENSIONS));
        }
    }

    private String getFileExtension(String filename) {
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1) {
            return "";
        }
        return filename.substring(lastDotIndex);
    }
}