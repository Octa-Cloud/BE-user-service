package com.project.user.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.user.domain.domain.entity.User;
import com.project.user.domain.domain.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureWebMvc
@ActiveProfiles("test-mysql")
@Transactional
class UserServiceIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void contextLoads() {
        // Spring 컨텍스트가 정상적으로 로드되는지 확인
    }

    @Test
    void healthCheck() throws Exception {
        mockMvc.perform(get("/v3/api-docs"))
                .andExpect(status().isOk());
    }

    @Test
    void swaggerUiAccessible() throws Exception {
        mockMvc.perform(get("/swagger-ui/index.html"))
                .andExpect(status().isOk());
    }

    @Test
    void signUpEndpointExists() throws Exception {
        // 회원가입 엔드포인트가 존재하는지 확인
        mockMvc.perform(post("/api/v1/users/signup")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // 400은 정상 (요청 데이터가 잘못됨)
    }

    @Test
    void loginEndpointExists() throws Exception {
        // 로그인 엔드포인트가 존재하는지 확인
        mockMvc.perform(post("/api/v1/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // 400은 정상 (요청 데이터가 잘못됨)
    }

    @Test
    void verifyEmailEndpointExists() throws Exception {
        // 이메일 인증 엔드포인트가 존재하는지 확인
        mockMvc.perform(post("/api/v1/users/verify-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // 400은 정상 (요청 데이터가 잘못됨)
    }

    @Test
    void sendVerificationEmailEndpointExists() throws Exception {
        // 인증 이메일 전송 엔드포인트가 존재하는지 확인
        mockMvc.perform(post("/api/v1/users/send-verification-email")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // 400은 정상 (요청 데이터가 잘못됨)
    }

    @Test
    void tokenReissueEndpointExists() throws Exception {
        // 토큰 재발급 엔드포인트가 존재하는지 확인
        mockMvc.perform(post("/api/v1/users/token-reissue")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // 400은 정상 (요청 데이터가 잘못됨)
    }

    @Test
    void logoutEndpointExists() throws Exception {
        // 로그아웃 엔드포인트가 존재하는지 확인
        mockMvc.perform(post("/api/v1/users/logout")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
                .andExpect(status().isBadRequest()); // 400은 정상 (요청 데이터가 잘못됨)
    }
}
