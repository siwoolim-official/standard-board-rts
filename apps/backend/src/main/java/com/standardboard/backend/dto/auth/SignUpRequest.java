package com.standardboard.backend.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 회원가입 요청 DTO (Request)
 *
 * 클라이언트로부터 회원가입에 필요한 데이터를 받습니다.
 * 데이터 유효성 검증(Validation)을 위한 Jakarta Validation 어노테이션을 적용합니다.
 */
@Getter
@Setter
@NoArgsConstructor
public class SignUpRequest {

    // 이메일 (로그인 ID)
    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효하지 않은 이메일 형식입니다.")
    @Size(max = 100, message = "이메일은 100자 이하여야 합니다.")
    private String email;

    // 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하여야 합니다.")
    private String password;

    // 닉네임
    @NotBlank(message = "닉네임은 필수 입력 항목입니다.")
    @Size(min = 2, max = 50, message = "닉네임은 2자 이상 50자 이하여야 합니다.")
    private String nickname;
}