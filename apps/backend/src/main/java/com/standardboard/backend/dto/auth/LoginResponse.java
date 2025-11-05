package com.standardboard.backend.dto.auth;

import com.standardboard.backend.domain.user.Role;
import lombok.Builder;
import lombok.Getter;

/**
 * 로그인 응답 DTO (Response)
 *
 * 인증 성공 후 클라이언트에게 JWT 토큰을 반환합니다.
 */
@Getter
@Builder
public class LoginResponse {
    private String accessToken;
    private Long userId;
    private String email;
    private String nickname;
    private Role role;
}