package com.reranko.cloud_strorage.mini_drive.file.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    private String ownerUserId;

    // File size in bytes
    @Column(nullable = false)
    private Long fileSize;

    // MIME type of the file
    @Column(nullable = false)
    private String contentType;

    // Timestamp of when the file was uploaded
    @Column(nullable = false)
    private LocalDateTime createdAt;

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

    public String getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(String ownerUserId) {
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
}