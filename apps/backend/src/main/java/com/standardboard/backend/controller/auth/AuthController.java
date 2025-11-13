package com.standardboard.backend.controller.auth;

import com.standardboard.backend.auth.jwt.JwtTokenProvider;
import com.standardboard.backend.domain.user.User;
import com.standardboard.backend.dto.auth.LoginResponse;
import com.standardboard.backend.dto.auth.LoginRequest;
import com.standardboard.backend.dto.auth.SignUpRequest;
import com.standardboard.backend.dto.auth.SignUpResponse;
import com.standardboard.backend.dto.common.ApiResponse;
import com.standardboard.backend.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

/**
 * 인증(Authentication) API Controller
 *
 * 회원가입, 로그인 등의 API 엔드포인트를 제공합니다.
 * 경로는 SecurityConfig에서 설정한 '인증 없이 접근 가능한' /api/v1/auth/** 에 해당합니다.
 */
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * 회원가입 API
     * POST /api/v1/auth/signup
     * @param request 회원가입 요청 DTO (JSON Body)
     * @return 성공 시 201 Created와 가입된 사용자 정보(DTO) 반환
     */
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignUpResponse>> signUp(@Valid @RequestBody SignUpRequest request) {
        // 1. 서비스 로직 호출
        User newUser = authService.signUp(request);

        // 2. 응답 DTO 변환 및 반환
        SignUpResponse response = SignUpResponse.from(newUser);

        // 3. ApiResponse로 데이터를 감싸고, HTTP 상태 코드 201 Created와 함께 반환
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success(response)); // ApiResponse.success() 정
    }

    /**
     * 로그인 API
     * POST /api/v1/auth/login
     * @param request 로그인 요청 DTO (JSON Body)
     * @return 성공 시 200 OK와 JWT 토큰 및 사용자 정보를 담은 ApiResponse 반환
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request, HttpServletResponse response) {
        // 서비스 로직 호출: 인증만 수행, User 엔티티 반환
        User authenticatedUser = authService.login(request);

        // JWT Access Token 생성 (TokenProvider 사용)
        String accessToken = jwtTokenProvider.generateToken(
                authenticatedUser.getEmail(),
                authenticatedUser.getRole()
        );

        // JWT를 응답 쿠키에 설정 (핵심 변경 사항)
        Cookie cookie = new Cookie("accessToken", accessToken); // 쿠키 이름
        cookie.setHttpOnly(true); // JavaScript 접근 차단 (XSS 방어)
//        cookie.setSecure(false);   // true HTTPS 통신에서만 전송, 운영 환경에서는 true 설정
        cookie.setPath("/");      // 모든 경로에서 쿠키 전송
        cookie.setMaxAge(3600);   // 쿠키 만료 시간

        response.addCookie(cookie); // 응답에 쿠키 추가

        // 응답 DTO 구성 (토큰 없이 사용자 정보만 담아 전송)
        LoginResponse responseBody = LoginResponse.builder()
                .userId(authenticatedUser.getId())
                .email(authenticatedUser.getEmail())
                .nickname(authenticatedUser.getNickname())
                .role(authenticatedUser.getRole())
                .build();

        // HTTP 상태 코드 200 OK와 함께 응답
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(ApiResponse.success(responseBody));
    }
}