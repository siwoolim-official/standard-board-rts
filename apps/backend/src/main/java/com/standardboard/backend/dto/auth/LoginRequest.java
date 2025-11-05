package com.standardboard.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 로그인 요청 DTO (Request)
 *
 * 클라이언트로부터 이메일과 비밀번호를 받습니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class LoginRequest {

    // 이메일 (로그인 ID)
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    private String email;

    // 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;
}
