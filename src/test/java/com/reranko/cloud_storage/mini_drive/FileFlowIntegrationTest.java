package com.reranko.cloud_storage.mini_drive;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.reranko.cloud_storage.mini_drive.file.service.S3UploadService;

/* Integration test for the full file upload flow:
 * 1. Register a new user
 * 2. Login to get an authentication token
 * 3. Create a file upload intent
 */

@SpringBootTest
@AutoConfigureMockMvc
class FileFlowIntegrationTest {

  @TestConfiguration
  static class TestConfig {
      // Additional test-specific beans can be defined here if needed

      @Bean
      @Primary
      S3UploadService s3UploadService() {
        return Mockito.mock(S3UploadService.class);
      }
  }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private S3UploadService s3UploadService;

    private void registerUser() throws Exception {
        String body = """
        {
          "email": "testflow@example.com",
          "password": "password123"
        }
        """;

        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk());
    }

    private String loginAndGetToken() throws Exception {
        String body = """
        {
          "email": "testflow@example.com",
          "password": "password123"
        }
        """;

        return mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andReturn()
                .getResponse()
                .getContentAsString();
    }

    @Test
    void fullFileUploadFlow() throws Exception {

        System.out.println("STEP 1: Register User");
        registerUser();
        System.out.println("User registered successfully.");

        System.out.println("STEP 2: Login User");
        String token = loginAndGetToken();
        System.out.println("User logged in successfully. Token: " + token);
        
        System.out.println("STEP 3: Create File Upload Intent");
        String uploadIntentBody = """
        {
          "fileName": "test.pdf",
          "fileSize": 123456,
          "contentType": "application/pdf"
        }
        """;

        Mockito.when(
            s3UploadService.generateUploadUrl(
                Mockito.anyString(),
                Mockito.anyString()
            )
        ).thenReturn(new java.net.URL("http://mock-s3-url"));

        String uploadResponse =
        mockMvc.perform(post("/files/upload-intent")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(uploadIntentBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

        Long fileId = objectMapper
            .readTree(uploadResponse)
            .get("fileId")
            .asLong();

        System.out.println("File upload intent created successfully. File ID: " + fileId);

        System.out.println("STEP 4: Complete File Upload");

        Mockito.when(
             s3UploadService.objectExists(Mockito.anyString()))
            .thenReturn(true);

        mockMvc.perform(post("/files/{fileId}/complete",fileId)
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        
        System.out.println("File upload completed successfully.");

        System.out.println("STEP 5: List Files");

        mockMvc.perform(get("/files")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].status").value("UPLOADED"));

        System.out.println("File listing verified successfully. Status = UPLOADED.");


    }
}
