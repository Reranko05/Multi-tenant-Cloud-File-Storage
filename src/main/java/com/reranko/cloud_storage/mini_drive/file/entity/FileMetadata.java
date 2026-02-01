package com.reranko.cloud_storage.mini_drive.file.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entity representing metadata for a file stored in the cloud storage system.
 * Includes fields for original file name, S3 object key, owner user ID,
 * file size, content type, and creation timestamp.
 */

@Entity
@Table(name = "files")
public class FileMetadata {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Original name of the file
    @Column(nullable = false)
    private String originalFileName;

    // System generated, immutable S3 key
    @Column(nullable = false, unique = true)
    private String s3ObjectKey;

    // Owner of the file (from JWT)
    @Column(nullable = false)
    private Long ownerUserId;

    // File size in bytes
    @Column(nullable = false)
    private Long fileSize;

    // MIME type of the file
    @Column(nullable = false)
    private String contentType;

    // Timestamp of when the file was uploaded
    @Column(nullable = false)
    private LocalDateTime createdAt;

    // Status of the file upload
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private FileStatus status;

    // Getter only for id
    public Long getId() {
        return id;
    }

    // Getters and setters for other fields
    public String getOriginalFileName() {
        return originalFileName;
    }

    public void setOriginalFileName(String originalFileName) {
        this.originalFileName = originalFileName;
    }

    public String getS3ObjectKey() {
        return s3ObjectKey;
    }

    public void setS3ObjectKey(String s3ObjectKey) {
        this.s3ObjectKey = s3ObjectKey;
    }

    public Long getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(Long ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public FileStatus getStatus() {
        return status;
    }

    public void setStatus(FileStatus status) {
        this.status = status;
    } 
}