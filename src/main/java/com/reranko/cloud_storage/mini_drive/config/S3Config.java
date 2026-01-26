package com.reranko.cloud_storage.mini_drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/* Configuration class for setting up AWS S3 Presigner.
 * This class defines a bean that creates an S3Presigner instance configured for the specified AWS region.
 * What is a Presigner?
 * A Presigner is used to generate pre-signed URLs for S3 operations, allowing temporary access to S3 resources without requiring AWS credentials.
 */

@Configuration
public class S3Config {

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.AP_SOUTH_1) // Code for AWS Mumbai region
            .build();
    }
}