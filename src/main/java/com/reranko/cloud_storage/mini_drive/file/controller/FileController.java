package com.reranko.cloud_storage.mini_drive.file.controller;

import com.reranko.cloud_storage.mini_drive.file.dto.CreateFileRequest;
import com.reranko.cloud_storage.mini_drive.file.entity.FileMetadata;
import com.reranko.cloud_storage.mini_drive.file.repository.FileMetadataRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileMetadataRepository fileRepository;

    public FileController(FileMetadataRepository fileRepository) {
        this.fileRepository = fileRepository;
    }

    @PostMapping
    public ResponseEntity<Long> createUploadIntent(
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
            "user-" + userId + "/" + UUID.randomUUID()
        );

        FileMetadata saved = fileRepository.save(file);

        return ResponseEntity.ok(saved.getId());
    }
}