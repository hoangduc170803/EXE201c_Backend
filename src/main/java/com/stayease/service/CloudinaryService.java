package com.stayease.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for handling file uploads to Cloudinary
 * Supports: Images, Videos, and 360 Videos
 * Only initialized when cloudinary.enabled=true
 */
@Service
@Slf4j
public class CloudinaryService {

    private Cloudinary cloudinary;

    @Value("${cloudinary.enabled:false}")
    private boolean cloudinaryEnabled;

    @Autowired(required = false)
    public void setCloudinary(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
        if (cloudinary != null) {
            log.info("✓ Cloudinary bean injected successfully");
        } else {
            log.warn("⚠ Cloudinary bean not available (cloudinary.enabled=false or not configured)");
        }
    }

    /**
     * Upload image to Cloudinary
     * @param file MultipartFile to upload
     * @return Map with url, publicId, format, size
     */
    public Map<String, Object> uploadImage(MultipartFile file) throws IOException {
        if (!cloudinaryEnabled || cloudinary == null) {
            throw new IllegalStateException("Cloudinary is not enabled or not configured. Set cloudinary.enabled=true and provide valid credentials in application.properties");
        }

        validateImageFile(file);

        try {
            Map<String, Object> uploadParams = ObjectUtils.asMap(
                "resource_type", "image",
                "folder", "stayease/properties/images"
            );

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
            return extractUploadInfo(uploadResult, "IMAGE");
        } catch (Exception e) {
            log.error("Cloudinary upload failed: {}", e.getMessage());
            throw new IOException("Cloudinary upload failed. Please check your credentials in application.properties: " + e.getMessage());
        }
    }

    /**
     * Upload video to Cloudinary
     * @param file MultipartFile to upload
     * @return Map with url, publicId, format, size, duration
     */
    public Map<String, Object> uploadVideo(MultipartFile file) throws IOException {
        if (!cloudinaryEnabled || cloudinary == null) {
            throw new IllegalStateException("Cloudinary is not enabled or not configured. Set cloudinary.enabled=true and provide valid credentials in application.properties");
        }

        validateVideoFile(file);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
            "resource_type", "video",
            "folder", "stayease/properties/videos"
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return extractUploadInfo(uploadResult, "VIDEO");
    }

    /**
     * Upload 360 video to Cloudinary
     * @param file MultipartFile to upload
     * @return Map with url, publicId, format, size, duration
     */
    public Map<String, Object> upload360Video(MultipartFile file) throws IOException {
        if (!cloudinaryEnabled || cloudinary == null) {
            throw new IllegalStateException("Cloudinary is not enabled or not configured. Set cloudinary.enabled=true and provide valid credentials in application.properties");
        }

        validateVideoFile(file);

        Map<String, Object> uploadParams = ObjectUtils.asMap(
            "resource_type", "video",
            "folder", "stayease/properties/videos-360"
        );

        Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadParams);
        return extractUploadInfo(uploadResult, "VIDEO_360");
    }

    /**
     * Delete media from Cloudinary
     * @param publicId Cloudinary public ID
     * @param resourceType image or video
     */
    public void deleteMedia(String publicId, String resourceType) throws IOException {
        if (!cloudinaryEnabled || cloudinary == null) {
            log.warn("Cloudinary is not enabled or not configured. Cannot delete: {}", publicId);
            return;
        }

        Map<String, Object> params = ObjectUtils.asMap(
            "resource_type", resourceType.toLowerCase()
        );

        cloudinary.uploader().destroy(publicId, params);
        log.info("Deleted media from Cloudinary: {}", publicId);
    }

    /**
     * Extract relevant information from Cloudinary upload result
     */
    private Map<String, Object> extractUploadInfo(Map<String, Object> uploadResult, String mediaType) {
        Map<String, Object> info = new HashMap<>();
        info.put("url", uploadResult.get("secure_url"));
        info.put("publicId", uploadResult.get("public_id"));
        info.put("format", uploadResult.get("format"));
        info.put("size", uploadResult.get("bytes"));
        info.put("mediaType", mediaType);

        // Add duration for videos
        if (uploadResult.containsKey("duration")) {
            info.put("duration", uploadResult.get("duration"));
        }

        // Add dimensions
        if (uploadResult.containsKey("width")) {
            info.put("width", uploadResult.get("width"));
            info.put("height", uploadResult.get("height"));
        }

        log.info("Uploaded {} to Cloudinary: {}", mediaType, info.get("url"));
        return info;
    }

    /**
     * Validate image file
     */
    private void validateImageFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("File must be an image (JPG, PNG, WebP, etc.)");
        }

        // Max 10MB for images
        if (file.getSize() > 10 * 1024 * 1024) {
            throw new IOException("Image size must not exceed 10MB");
        }
    }

    /**
     * Validate video file
     */
    private void validateVideoFile(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("File is empty");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("video/")) {
            throw new IOException("File must be a video (MP4, MOV, etc.)");
        }

        // Max 100MB for videos (Cloudinary free tier limit)
        if (file.getSize() > 100 * 1024 * 1024) {
            throw new IOException("Video size must not exceed 100MB");
        }
    }
}

