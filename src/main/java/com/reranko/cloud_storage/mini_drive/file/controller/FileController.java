package com.reranko.cloud_storage.mini_drive.file.controller;

import com.reranko.cloud_storage.mini_drive.file.dto.CreateFileRequest;
import com.reranko.cloud_storage.mini_drive.file.dto.CreateFileResponse;
import com.reranko.cloud_storage.mini_drive.file.entity.FileMetadata;
import com.reranko.cloud_storage.mini_drive.file.repository.FileMetadataRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;

import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/*
 * REST controller for managing file upload intents.
 * Provides an endpoint to create a new file upload intent,
 * generating metadata and returning the file ID.
 * 
 * What does this file do?
 * This controller handles HTTP requests related to file uploads. It allows clients to create upload intents,
 * which involves generating file metadata, saving it to the database, and returning a pre-signed URL for uploading the file to S3.
 * 
 * How does it work?
 * When a client sends a POST request to the /files/upload-intent endpoint with file details,
 * the controller extracts the authenticated user's ID from the security context,
 * creates a new FileMetadata entity, saves it to the database,
 * and generates a pre-signed S3 URL for uploading the file.
 * The response includes the file ID and the upload URL.
 */

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileMetadataRepository fileRepository;
    private final S3Presigner s3Presigner;

    public FileController(FileMetadataRepository fileRepository, S3Presigner s3Presigner) {
        this.fileRepository = fileRepository;
        this.s3Presigner = s3Presigner;
    }

    @PostMapping("/upload-intent")
    public ResponseEntity<CreateFileResponse> createUploadIntent(
        @RequestBody CreateFileRequest request
    ) {

        Long userId = (Long) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
        
        FileMetadata file = new FileMetadata();
        file.setOriginalFileName(request.getFileName());
        file.setFileSize(request.getFileSize());
        file.setContentType(request.getContentType());
        file.setOwnerUserId(userId);
        file.setCreatedAt(LocalDateTime.now());

        file.setS3ObjectKey(
            "user-" + userId + "/" + UUID.randomUUID() // UUID => Universally Unique Identifier 
        );                                             // generates a unique string for the file name in S3

        FileMetadata saved = fileRepository.save(file); // Save metadata to DB

        /*
         * PutObjectRequest is a constructor which defines the parameters for the S3 upload.
         * Here, we specify the S3 bucket name, the object key (file path in S3),
         * the content type of the file, and other metadata needed for the upload.
         */

        PutObjectRequest putObjectRequest = PutObjectRequest.builder() 
                .bucket("your-s3-bucket-name")                          
                .key(saved.getS3ObjectKey())                            
                .contentType(request.getContentType())                                   
                .build();
        
        /* 
         * PutObjectPresignRequest is used to create a pre-signed URL for the S3 upload.
         * It includes the PutObjectRequest and specifies how long the pre-signed URL should be valid.
         * In this case, we set it to expire in 5 minutes.
         */
        
        PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(5))
                .putObjectRequest(putObjectRequest)
                .build();

        /* 
         * The presignPutObject method of the S3Presigner generates the pre-signed URL
         * based on the provided PutObjectPresignRequest.
         * This URL can then be used by clients to upload the file directly to S3
         * without needing further authentication.
         */
        
        PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);

        return ResponseEntity.ok(
            new CreateFileResponse(
                saved.getId(),
                presignedRequest.url().toString() // Convert URL to string
            )
        );
    }
}