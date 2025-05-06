package com.domulink.user.service;

import org.springframework.web.multipart.MultipartFile;

public interface  StorageService {
    String uploadPropertyImage(MultipartFile file);
    String uploadPropertyDocumentImage(MultipartFile file);
    public void deleteImage(String publicId);
}
