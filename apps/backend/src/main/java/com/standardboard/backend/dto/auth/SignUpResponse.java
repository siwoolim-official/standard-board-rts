package com.standardboard.backend.dto.auth;

import com.standardboard.backend.domain.user.Role;
import com.standardboard.backend.domain.user.User;
import lombok.Builder;
import lombok.Getter;

/**
 * 회원가입 응답 DTO (Response)
 *
 * 회원가입 성공 후 클라이언트에게 반환할 데이터를 담습니다.
 * 비밀번호 같은 민감한 정보는 절대 포함하지 않습니다.
 */
@Getter
@Builder
public class SignUpResponse {
    private Long id;
    private String email;
    private String nickname;
    private Role role;

    // User 엔티티를 받아 DTO로 변환하는 정적 팩토리 메서드 (Best Practice)
    public static SignUpResponse from(User user) {
        return SignUpResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .role(user.getRole())
                .build();
    }
}