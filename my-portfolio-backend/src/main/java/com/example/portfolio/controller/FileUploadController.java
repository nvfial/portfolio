package com.example.portfolio.controller;

import com.example.portfolio.config.UserPrincipal;
import com.example.portfolio.service.FileUploadService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@Tag(name = "File Upload", description = "文件上传API")
public class FileUploadController {

    @Autowired
    private FileUploadService fileUploadService;

    @PostMapping("/image")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'REVIEWER')")
    @Operation(summary = "上传图片")
    public ResponseEntity<Map<String, String>> uploadImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, String> result = fileUploadService.uploadImage(file, principal.getId());
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cover")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR', 'REVIEWER')")
    @Operation(summary = "上传封面图片")
    public ResponseEntity<Map<String, String>> uploadCover(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal UserPrincipal principal) {
        Map<String, String> result = fileUploadService.uploadCoverImage(file, principal.getId());
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/file")
    @PreAuthorize("hasAnyRole('ADMIN', 'AUTHOR')")
    @Operation(summary = "删除文件")
    public ResponseEntity<Void> deleteFile(@RequestParam("path") String path) {
        fileUploadService.deleteFile(path);
        return ResponseEntity.ok().build();
    }
}