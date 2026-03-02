package com.stayease.controller;

import com.stayease.dto.response.ApiResponse;
import com.stayease.service.CloudinaryService;
import com.stayease.service.LocalFileStorageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller for file upload operations
 * Handles images, videos, and 360 videos upload to Cloudinary or Local Storage
 */
@RestController
@RequestMapping("/api/files")
@Slf4j
@Tag(name = "File Management", description = "APIs for uploading and managing media files")
@CrossOrigin(origins = "*")
public class FileController {
    @Autowired(required = false)
    private  CloudinaryService cloudinaryService;
    @Autowired
    private  LocalFileStorageService localFileStorageService;

    @Value("${cloudinary.enabled:false}")
    private boolean cloudinaryEnabled;

    /**
     * Upload a single image
     * POST /api/files/upload-image
     */
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload single image", description = "Upload an image file to Cloudinary or Local Storage (max 10MB)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadImage(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            log.info("Uploading image: {} ({})", file.getOriginalFilename(), formatFileSize(file.getSize()));

            Map<String, Object> result;

            if (cloudinaryEnabled && cloudinaryService != null) {
                // Use Cloudinary
                result = cloudinaryService.uploadImage(file);
            } else {
                // Fallback to local storage
                log.info("Using local storage for file upload");
                result = localFileStorageService.saveFile(file, "images");
            }

            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Image uploaded successfully")
                    .data(result)
                    .build());

        } catch (IOException e) {
            log.error("Failed to upload image: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to upload image: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Upload multiple images
     * POST /api/files/upload-images
     */
    @PostMapping(value = "/upload-images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload multiple images", description = "Upload multiple image files to Cloudinary (max 10 files)")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> uploadImages(
            @RequestParam("files") MultipartFile[] files
    ) {
        if (files.length > 10) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<List<Map<String, Object>>>builder()
                            .success(false)
                            .message("Cannot upload more than 10 images at once")
                            .build());
        }

        List<Map<String, Object>> results = new ArrayList<>();
        List<String> errors = new ArrayList<>();

        for (MultipartFile file : files) {
            try {
                log.info("Uploading image: {} ({})", file.getOriginalFilename(), formatFileSize(file.getSize()));
                Map<String, Object> result;

                if (cloudinaryEnabled && cloudinaryService != null) {
                    result = cloudinaryService.uploadImage(file);
                } else {
                    result = localFileStorageService.saveFile(file, "images");
                }

                results.add(result);
            } catch (IOException e) {
                log.error("Failed to upload {}: {}", file.getOriginalFilename(), e.getMessage());
                errors.add(file.getOriginalFilename() + ": " + e.getMessage());
            }
        }

        if (!errors.isEmpty()) {
            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .body(ApiResponse.<List<Map<String, Object>>>builder()
                            .success(false)
                            .message("Some files failed to upload: " + String.join(", ", errors))
                            .data(results)
                            .build());
        }

        return ResponseEntity.ok(ApiResponse.<List<Map<String, Object>>>builder()
                .success(true)
                .message("All images uploaded successfully")
                .data(results)
                .build());
    }

    /**
     * Upload a single video
     * POST /api/files/upload-video
     */
    @PostMapping(value = "/upload-video", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload single video", description = "Upload a video file to Cloudinary or Local Storage (max 100MB, 1-2 minutes)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> uploadVideo(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            log.info("Uploading video: {} ({})", file.getOriginalFilename(), formatFileSize(file.getSize()));

            Map<String, Object> result;

            if (cloudinaryEnabled && cloudinaryService != null) {
                result = cloudinaryService.uploadVideo(file);
            } else {
                log.info("Using local storage for video upload");
                result = localFileStorageService.saveFile(file, "videos");
            }

            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("Video uploaded successfully")
                    .data(result)
                    .build());

        } catch (IOException e) {
            log.error("Failed to upload video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to upload video: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Upload a 360 degree video
     * POST /api/files/upload-video-360
     */
    @PostMapping(value = "/upload-video-360", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload 360 video", description = "Upload a 360 degree video to Cloudinary or Local Storage (max 100MB)")
    public ResponseEntity<ApiResponse<Map<String, Object>>> upload360Video(
            @RequestParam("file") MultipartFile file
    ) {
        try {
            log.info("Uploading 360 video: {} ({})", file.getOriginalFilename(), formatFileSize(file.getSize()));

            Map<String, Object> result;

            if (cloudinaryEnabled && cloudinaryService != null) {
                result = cloudinaryService.upload360Video(file);
            } else {
                log.info("Using local storage for 360 video (Note: 360 playback requires frontend implementation)");
                result = localFileStorageService.saveFile(file, "videos-360");
                result.put("mediaType", "VIDEO_360");
            }

            return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                    .success(true)
                    .message("360 video uploaded successfully")
                    .data(result)
                    .build());

        } catch (IOException e) {
            log.error("Failed to upload 360 video: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.<Map<String, Object>>builder()
                            .success(false)
                            .message("Failed to upload 360 video: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Delete media from Cloudinary
     * DELETE /api/files/{publicId}
     */
    @DeleteMapping("/{publicId}")
    @Operation(summary = "Delete media", description = "Delete a file from Cloudinary by public ID")
    public ResponseEntity<ApiResponse<Void>> deleteMedia(
            @PathVariable String publicId,
            @RequestParam(defaultValue = "image") String resourceType
    ) {
        try {
            if (cloudinaryEnabled && cloudinaryService != null) {
                cloudinaryService.deleteMedia(publicId, resourceType);
            } else {
                log.warn("Cannot delete from Cloudinary - service not available or disabled");
            }
            return ResponseEntity.ok(ApiResponse.<Void>builder()
                    .success(true)
                    .message("Media deleted successfully")
                    .build());
        } catch (IOException e) {
            log.error("Failed to delete media: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.<Void>builder()
                            .success(false)
                            .message("Failed to delete media: " + e.getMessage())
                            .build());
        }
    }

    /**
     * Get upload configuration info
     * GET /api/files/config
     */
    @GetMapping("/config")
    @Operation(summary = "Get upload config", description = "Get file upload limits and supported formats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUploadConfig() {
        Map<String, Object> config = new HashMap<>();
        config.put("cloudinaryEnabled", cloudinaryEnabled);
        config.put("storageType", cloudinaryEnabled ? "Cloudinary Cloud" : "Local Storage");
        config.put("maxImageSize", "10MB");
        config.put("maxVideoSize", "100MB");
        config.put("supportedImageFormats", List.of("JPG", "PNG", "WebP", "GIF"));
        config.put("supportedVideoFormats", List.of("MP4", "MOV", "AVI", "WebM"));
        config.put("maxImagesPerProperty", 10);
        config.put("maxVideosPerProperty", 3);
        config.put("video360Supported", cloudinaryEnabled);

        return ResponseEntity.ok(ApiResponse.<Map<String, Object>>builder()
                .success(true)
                .message("Upload configuration")
                .data(config)
                .build());
    }

    /**
     * Format file size for logging
     */
    private String formatFileSize(long size) {
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return String.format("%.2f KB", size / 1024.0);
        return String.format("%.2f MB", size / (1024.0 * 1024.0));
    }
}

