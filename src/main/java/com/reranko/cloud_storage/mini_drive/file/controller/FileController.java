package com.reranko.cloud_storage.mini_drive.file.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reranko.cloud_storage.mini_drive.file.dto.CreateFileRequest;
import com.reranko.cloud_storage.mini_drive.file.dto.CreateFileResponse;
import com.reranko.cloud_storage.mini_drive.file.entity.FileMetadata;
import com.reranko.cloud_storage.mini_drive.file.entity.FileStatus;
import com.reranko.cloud_storage.mini_drive.file.repository.FileMetadataRepository;
import com.reranko.cloud_storage.mini_drive.file.service.S3UploadService;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileMetadataRepository fileRepository;
    private final S3UploadService s3UploadService;

    public FileController(FileMetadataRepository fileRepository, S3UploadService s3UploadService) {
        this.fileRepository = fileRepository;
        this.s3UploadService = s3UploadService;
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
        file.setStatus(FileStatus.PENDING);

        file.setS3ObjectKey(
            "user-" + userId + "/" + UUID.randomUUID() // UUID => Universally Unique Identifier 
        );                                             // generates a unique string for the file name in S3

                FileMetadata saved = fileRepository.save(file); // Save metadata to DB

        String uploadUrl = s3UploadService.generateUploadUrl(
            saved.getS3ObjectKey(),
            saved.getContentType()
        ).toString();

        return ResponseEntity.ok(
            new CreateFileResponse(
                saved.getId(),
                uploadUrl
            )
        );
    }

    @PostMapping("/{fileId}/complete")
    public ResponseEntity<Void> completeUpload(
        @PathVariable Long fileId
    ) {

        Long userId = (Long) SecurityContextHolder
            .getContext()
            .getAuthentication()
            .getPrincipal();
        
        FileMetadata file = fileRepository
            .findByIdAndOwnerUserId(fileId, userId)
            .orElseThrow(() -> new RuntimeException("File not found"));
        
        // Prevent double completion
        if (file.getStatus() != FileStatus.PENDING) {
            throw new RuntimeException("File already completed");
        }

        file.setStatus(FileStatus.UPLOADED);
        fileRepository.save(file);

        return ResponseEntity.ok().build();
    }
}