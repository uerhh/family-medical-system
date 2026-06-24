package com.familymedical.service;

import com.aliyun.oss.OSS;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Set;
import java.util.UUID;

@Service
public class OssService {

    private static final Set<String> ALLOWED_TYPES = Set.of("image/jpeg", "image/png", "image/webp");
    private static final long MAX_SIZE = 2 * 1024 * 1024; // 2MB
    private static final long IMAGE_MAX_SIZE = 5 * 1024 * 1024; // 5MB for consultation images

    private final OSS ossClient;

    @Value("${aliyun.oss.bucket-name}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    public OssService(OSS ossClient) {
        this.ossClient = ossClient;
    }

    public String uploadAvatar(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("仅支持 jpg、png、webp 格式图片");
        }
        if (file.getSize() > MAX_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过 2MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = "avatars/" + UUID.randomUUID().toString().replace("-", "") + extension;

        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }

        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }

    public String uploadImage(MultipartFile file) {
        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_TYPES.contains(contentType)) {
            throw new IllegalArgumentException("仅支持 jpg、png、webp 格式图片");
        }
        if (file.getSize() > IMAGE_MAX_SIZE) {
            throw new IllegalArgumentException("图片大小不能超过 5MB");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String objectName = "consultations/" + UUID.randomUUID().toString().replace("-", "") + extension;

        try (InputStream inputStream = file.getInputStream()) {
            ossClient.putObject(bucketName, objectName, inputStream);
        } catch (IOException e) {
            throw new RuntimeException("文件上传失败", e);
        }

        return "https://" + bucketName + "." + endpoint + "/" + objectName;
    }
}
