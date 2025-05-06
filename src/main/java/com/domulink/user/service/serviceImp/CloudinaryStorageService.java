package com.domulink.user.service.serviceImp;

import com.cloudinary.Cloudinary;
import com.domulink.exception.CloudinaryException;
import com.domulink.exception.InvalidFileException;
import com.domulink.user.service.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class CloudinaryStorageService implements StorageService {

    private final Cloudinary cloudinary;

    public String uploadPropertyImage(MultipartFile file) {
        validateFile(file);

        try {
            Map<String, Object> uploadOptions = Map.of(
                    "folder", "property-images",
                    "resource_type", "auto",
                    "quality", "auto"
            );

            log.debug("Uploading document to Cloudinary. Options: {}", uploadOptions);

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            log.debug("Upload result: {}", uploadResult);

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Error uploading document to Cloudinary: {}", e.getMessage());
            throw new CloudinaryException("Error uploading document to Cloudinary", e);
        }
    }


    private void validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            throw new InvalidFileException("File is empty");
        }

        String contentType = file.getContentType();

        // Common image MIME types including those from mobile devices
        Set<String> validImageTypes = Set.of(
                "image/jpeg", "image/jpg", "image/png",
                "image/gif", "image/bmp", "image/webp",
                "image/heic", "image/heif",  // Common iPhone formats
                "application/octet-stream"    // Some devices use this for images
        );

        if (!validImageTypes.contains(contentType.toLowerCase())) {
            throw new InvalidFileException("Invalid image format. Supported formats: JPG, PNG, GIF, BMP, WEBP, HEIC");
        }

        long fileSize = file.getSize();
        if (fileSize > 5_000_000) {
            throw new InvalidFileException("File size must be less than 5MB");
        }
    }

    public String uploadPropertyDocumentImage(MultipartFile file) {
        validateFile(file);

        try {
            Map<String, Object> uploadOptions = Map.of(
                    "folder", "property-document-images",
                    "resource_type", "auto",
                    "quality", "auto"
            );

            log.debug("Uploading document to Cloudinary. Options: {}", uploadOptions);

            Map<String, Object> uploadResult = cloudinary.uploader().upload(file.getBytes(), uploadOptions);
            log.debug("Upload result: {}", uploadResult);

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            log.error("Error uploading document to Cloudinary: {}", e.getMessage());
            throw new CloudinaryException("Error uploading document to Cloudinary", e);
        }
    }

    public void deleteImage(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            throw new IllegalArgumentException("Public ID must not be null or empty.");
        }

        try {
            log.debug("Deleting image from Cloudinary. Public ID: {}", publicId);

            Map<String, Object> deleteResult = cloudinary.uploader().destroy(publicId, Map.of("resource_type", "image"));
            log.debug("Delete result: {}", deleteResult);

            // Check if the image was successfully deleted (you may customize this check as needed)
            if ("ok".equals(deleteResult.get("result"))) {
                log.info("Image with public ID {} deleted successfully.", publicId);
            } else {
                log.error("Failed to delete image with public ID {}", publicId);

            }
        } catch (Exception e) {
            log.error("Error deleting image from Cloudinary: {}", e.getMessage());
            throw new CloudinaryException("Error deleting image from Cloudinary", e);
        }
}
}

