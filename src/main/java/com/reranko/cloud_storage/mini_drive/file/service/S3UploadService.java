package com.reranko.cloud_storage.mini_drive.file.service;

import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

import java.time.Duration;
import java.net.URL;

@Service
public class S3UploadService {

    private final S3Presigner presigner;

    private static final String BUCKET_NAME = "mini-drive-reranko";

    public S3UploadService(S3Presigner presigner) {
        this.presigner = presigner;
    }

    public URL generateUploadUrl(
        String s3ObjectKey,
        String contentType
    ) {
        
        PutObjectRequest putObjectRequest =
            PutObjectRequest.builder()
                .bucket(BUCKET_NAME)
                .key(s3ObjectKey)
                .contentType(contentType)
                .build();
        
        PutObjectPresignRequest presignRequest =
            PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(10))
                .putObjectRequest(putObjectRequest)
                .build();
        
        return presigner.presignPutObject(presignRequest).url();
    }
}