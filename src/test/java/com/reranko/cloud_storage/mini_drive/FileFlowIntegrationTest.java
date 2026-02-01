package com.reranko.cloud_storage.mini_drive;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

/* Integration test for the full file upload flow:
 * 1. Register a new user
 * 2. Login to get an authentication token
 * 3. Create a file upload intent
 */

@SpringBootTest
@AutoConfigureMockMvc
class FileFlowIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

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

        mockMvc.perform(post("/files/upload-intent")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(uploadIntentBody))
                .andExpect(status().isOk());
        System.out.println("File upload intent created successfully.");
    }
}
