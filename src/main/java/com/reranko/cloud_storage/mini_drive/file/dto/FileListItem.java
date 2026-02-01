package com.reranko.cloud_storage.mini_drive.file.dto;

import java.time.LocalDateTime;

import com.reranko.cloud_storage.mini_drive.file.entity.FileStatus;

public class FileListItem {

    private Long fileId;
    private String fileName;
    private Long fileSize;
    private String contentType;
    private FileStatus status;
    private LocalDateTime createdAt;

    public FileListItem(
        Long fileId,
        String fileName,
        Long fileSize,
        String contentType,
        FileStatus status,
        LocalDateTime createdAt
    ) {
        this.fileId = fileId;
        this.fileName = fileName;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getFileName() {
        return fileName;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public String getContentType() {
        return contentType;
    }

    public FileStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}



