package com.reranko.cloud_storage.mini_drive.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

/* Configuration class for setting up AWS S3 Presigner and S3 Client.
 * This class defines a bean that creates an S3Presigner instance configured for the specified AWS region.
 * What is a Presigner?
 * A Presigner is used to generate pre-signed URLs for S3 operations, allowing temporary access to S3 resources without requiring AWS credentials.
 * 
 * What is S3 Client?
 * S3 Client is used to interact with the S3 service, allowing operations such as checking
 */

@Configuration
public class S3Config {

    @Bean
    public S3Presigner s3Presigner() {
        return S3Presigner.builder()
            .region(Region.AP_SOUTH_1) // Code for AWS Mumbai region
            .build();
    }

    /* Spring creates and manages objects for you using dependency injection(DI). 
     * When your S3UploadService asked for an S3Client in its constructor, 
     * Spring tried to create the service but couldn’t because it didn’t know how to create an S3Client on its own. 
     * AWS SDK clients are not built into Spring, so Spring won’t guess their configuration. 
     * By defining an S3Client as a @Bean, you explicitly told Spring how to create it.
     *  Once Spring knew how to create that object, it could inject it into S3UploadService, 
     * and the application started successfully.
     */

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.AP_SOUTH_1)
                .build();
    }
}