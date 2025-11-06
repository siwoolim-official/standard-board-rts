package com.standardboard.backend.controller.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.standardboard.backend.auth.jwt.JwtTokenProvider;
import com.standardboard.backend.config.SecurityConfig;
import com.standardboard.backend.domain.user.Role;
import com.standardboard.backend.domain.user.User;
import com.standardboard.backend.dto.auth.LoginRequest;
import com.standardboard.backend.dto.auth.SignUpRequest;
import com.standardboard.backend.service.auth.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * AuthController 통합 테스트 (WebMvcTest)
 */
@WebMvcTest(AuthController.class)
// SecurityConfig 로드 (JWT 필터 정상 작동을 위해)
@Import({SecurityConfig.class})
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService; // MockitoBean은 자동으로 컨텍스트에 등록 및 주입됩니다.

    @MockitoBean
    private JwtTokenProvider jwtTokenProvider; // 마찬가지로 MockBean 대체

    private SignUpRequest validSignUpRequest;
    private LoginRequest validLoginRequest;
    private User savedUser;
    private final String testToken = "mocked.jwt.access.token";


    @BeforeEach
    void setUp() {
        // 유효한 회원가입 요청 DTO
        validSignUpRequest = new SignUpRequest();
        validSignUpRequest.setEmail("newuser@test.com");
        validSignUpRequest.setPassword("securepassword123");
        validSignUpRequest.setNickname("newbie");

        // 유효한 로그인 요청 DTO
        validLoginRequest = new LoginRequest();
        validLoginRequest.setEmail("login@test.com");
        validLoginRequest.setPassword("securepassword123");

        // Service에서 반환할 User 엔티티
        savedUser = User.builder()
                .email(validSignUpRequest.getEmail())
                .password("encoded_password")
                .nickname(validSignUpRequest.getNickname())
                .role(Role.USER)
                .build();
    }

    // --- 회원가입 API 테스트: POST /api/v1/auth/signup ---

    @Test
    @DisplayName("성공: 유효한 회원가입 요청 시 201 Created 반환")
    void signUp_success() throws Exception {
        // Given
        // 필드명 (authService)을 그대로 사용하여 Mockito 행동 정의
        when(authService.signUp(any(SignUpRequest.class))).thenReturn(savedUser);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validSignUpRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.email").value(validSignUpRequest.getEmail()));
    }

    @Test
    @DisplayName("성공: 유효한 로그인 요청 시 200 OK와 AccessToken 반환")
    void login_success() throws Exception {
        // Given
        when(authService.login(any(LoginRequest.class))).thenReturn(savedUser);
        // jwtTokenProvider 필드명을 그대로 사용하여 Mockito 행동 정의
        when(jwtTokenProvider.generateToken(any(), any())).thenReturn(testToken);

        // When & Then
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validLoginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.accessToken").value(testToken))
                .andExpect(jsonPath("$.data.email").value(savedUser.getEmail()));
    }

    @Test
    @DisplayName("실패: 비밀번호 불일치 시 400 Bad Request 반환")
    void login_fail_passwordMismatch() throws Exception {
        // Given: 서비스 계층에서 인증 실패 예외를 발생시킨다고 가정
        when(authService.login(any(LoginRequest.class)))
                .thenThrow(new IllegalArgumentException("이메일 또는 비밀번호가 일치하지 않습니다."));

        // When
        ResultActions result = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginRequest)));

        // Then
        result.andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false));
    }
}