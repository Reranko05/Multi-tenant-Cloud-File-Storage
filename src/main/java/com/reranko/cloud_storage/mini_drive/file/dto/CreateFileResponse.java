package com.reranko.cloud_storage.mini_drive.file.dto;

/*
 *DTO for the response returned when creating a file upload intent.
 * Contains the file ID and the pre-signed upload URL.
 */

public class CreateFileResponse {

    private Long fileId;
    private String uploadUrl;

    public CreateFileResponse(Long fileId, String uploadUrl) {
        this.fileId = fileId;
        this.uploadUrl = uploadUrl;
    }

    public Long getFileId() {
        return fileId;
    }

    public String getUploadUrl() {
        return uploadUrl;
    }
}