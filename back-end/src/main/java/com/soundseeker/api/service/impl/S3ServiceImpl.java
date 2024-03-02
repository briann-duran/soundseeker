package com.soundseeker.api.service.impl;

import com.soundseeker.api.service.IS3Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

@Service
public class S3ServiceImpl implements IS3Service {

    private final S3Client s3Client;

    @Autowired
    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    public String uploadFile(MultipartFile file) throws IOException {
        try {
            String originalFileName = Objects.requireNonNull(file.getOriginalFilename());
            String fileExtension = getFileExtension(originalFileName);

            if (!isValidImageType(fileExtension)) {
                throw new IllegalArgumentException("Tipo de archivo no admitido. Solo se permiten archivos jpg, jpeg, png, svg o webp.");
            }

            String uniqueFileName = generateUniqueFileName(originalFileName);

            String folderName = "img/";
            String key = folderName + uniqueFileName;

            PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                    .bucket("1023c09-grupo1-img")
                    .key(key)
                    .contentType(getContentType(fileExtension))
                    .build();

            s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));
            return "/img/" + uniqueFileName;
        } catch (IOException e) {
            throw new IOException(e.getMessage());
        }
    }

    private String getFileExtension(String fileName) {
        return fileName.substring(fileName.lastIndexOf(".") + 1).toLowerCase();
    }

    private boolean isValidImageType(String fileExtension) {
        return fileExtension.equals("jpg") || fileExtension.equals("jpeg") ||
                fileExtension.equals("png") || fileExtension.equals("svg") ||
                fileExtension.equals("webp");
    }

    private String getContentType(String fileExtension) {
        return switch (fileExtension) {
            case "jpg", "jpeg" -> "image/jpeg";
            case "png" -> "image/png";
            case "svg" -> "image/svg+xml";
            case "webp" -> "image/webp";
            default -> "application/octet-stream";
        };
    }

    private String generateUniqueFileName(String originalFileName) {
        String timestampHex = Long.toHexString(Instant.now().toEpochMilli());
        String shortUUID = UUID.randomUUID().toString().replace("-", "").substring(0, 8);
        return timestampHex + "_" + shortUUID + "_" + originalFileName;
    }
}