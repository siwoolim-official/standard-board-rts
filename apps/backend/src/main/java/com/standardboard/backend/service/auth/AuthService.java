package com.standardboard.backend.service.auth;

import com.standardboard.backend.domain.user.Role;
import com.standardboard.backend.domain.user.User;
import com.standardboard.backend.dto.auth.SignUpRequest;
import com.standardboard.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 인증(Authentication) 관련 비즈니스 로직을 처리하는 서비스
 *
 * 회원가입, 로그인, 토큰 발행 등의 핵심 로직이 포함됩니다.
 */
@Service
@RequiredArgsConstructor // Lombok을 사용하여 final 필드를 인자로 받는 생성자를 자동 생성 (의존성 주입)
@Transactional(readOnly = true) // 읽기 전용 트랜잭션 기본 설정
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 회원가입을 처리하는 비즈니스 메서드
     * @param request 회원가입 요청 DTO
     * @return 저장된 User 엔티티
     */
    @Transactional // 쓰기 작업이므로 트랜잭션 재정의
    public User signUp(SignUpRequest request) {
        // 1. 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        // 2. 닉네임 중복 확인
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }

        // 3. 비밀번호 암호화 (보안 필수)
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 4. User 엔티티 생성
        User newUser = User.builder()
                .email(request.getEmail())
                .password(encodedPassword) // 암호화된 비밀번호 저장
                .nickname(request.getNickname())
                .role(Role.USER) // 기본 권한은 USER
                .build();

        // 5. DB 저장 및 반환
        return userRepository.save(newUser);
    }

    /**
     * (추후 구현 예정): 로그인 및 JWT 토큰 발행 메서드
     */
}