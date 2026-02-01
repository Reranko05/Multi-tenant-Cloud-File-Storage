package com.reranko.cloud_storage.mini_drive.file.service;

import java.net.URL;
import java.time.Duration;

import org.springframework.stereotype.Service;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3UploadService {

    private final S3Presigner presigner;
    private final S3Client s3Client; 

    private static final String BUCKET_NAME = "mini-drive-reranko";

    public S3UploadService(S3Presigner presigner, S3Client s3Client) {
        this.presigner = presigner;
        this.s3Client = s3Client;
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

    public boolean objectExists(String s3ObjectKey) {
        try {
            s3Client.headObject(
                HeadObjectRequest.builder()
                    .bucket(BUCKET_NAME)
                    .key(s3ObjectKey)
                    .build()
            );
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}