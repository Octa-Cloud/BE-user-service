package com.project.user.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test")
class UserControllerTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @Test
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testSignUpWithValidData() throws Exception {
        String signUpRequest = """
                {
                    "email": "test@example.com",
                    "password": "password123",
                    "name": "Test User"
                }
                """;

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testSignUpWithInvalidEmail() throws Exception {
        String signUpRequest = """
                {
                    "email": "invalid-email",
                    "password": "password123",
                    "name": "Test User"
                }
                """;

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignUpWithShortPassword() throws Exception {
        String signUpRequest = """
                {
                    "email": "test@example.com",
                    "password": "123",
                    "name": "Test User"
                }
                """;

        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content(signUpRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testLoginWithValidCredentials() throws Exception {
        String loginRequest = """
                {
                    "email": "test@example.com",
                    "password": "password123"
                }
                """;

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists())
                .andExpect(jsonPath("$.data.refreshToken").exists());
    }

    @Test
    void testLoginWithInvalidCredentials() throws Exception {
        String loginRequest = """
                {
                    "email": "test@example.com",
                    "password": "wrongpassword"
                }
                """;

        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginRequest))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void testSendVerificationEmail() throws Exception {
        String emailRequest = """
                {
                    "email": "test@example.com"
                }
                """;

        mockMvc.perform(post("/api/v1/users/send-verification-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(emailRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testVerifyEmailWithValidCode() throws Exception {
        String verifyRequest = """
                {
                    "email": "test@example.com",
                    "verificationCode": "123456"
                }
                """;

        mockMvc.perform(post("/api/v1/users/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content(verifyRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }

    @Test
    void testTokenReissue() throws Exception {
        String reissueRequest = """
                {
                    "refreshToken": "valid-refresh-token"
                }
                """;

        mockMvc.perform(post("/api/v1/users/token-reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reissueRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").exists());
    }

    @Test
    void testLogout() throws Exception {
        String logoutRequest = """
                {
                    "refreshToken": "valid-refresh-token"
                }
                """;

        mockMvc.perform(post("/api/v1/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content(logoutRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true));
    }
}
