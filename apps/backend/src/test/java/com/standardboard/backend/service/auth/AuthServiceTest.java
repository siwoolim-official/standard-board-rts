package com.standardboard.backend.service.auth;

import com.standardboard.backend.domain.user.Role;
import com.standardboard.backend.domain.user.User;
import com.standardboard.backend.dto.auth.LoginRequest;
import com.standardboard.backend.dto.auth.SignUpRequest;
import com.standardboard.backend.repository.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * AuthService 단위 테스트 (Unit Test)
 */
@ExtendWith(MockitoExtension.class) // Mockito 사용을 위한 확장
class AuthServiceTest {

    // 테스트 대상 객체에 Mock 객체를 주입 (실제 인스턴스)
    @InjectMocks
    private AuthService authService;

    // 의존성 객체를 Mock으로 생성
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    // 테스트용 사용자 정보
    private SignUpRequest signUpRequest;
    private LoginRequest loginRequest;
    private User testUser;
    private final String encodedPassword = "encodedPassword123";

    @BeforeEach
    void setUp() {
        // 회원가입 요청 데이터
        signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("test@example.com");
        signUpRequest.setPassword("password123");
        signUpRequest.setNickname("tester");

        // 로그인 요청 데이터
        loginRequest = new LoginRequest();
        loginRequest.setEmail("test@example.com");
        loginRequest.setPassword("password123");

        // DB에 저장될 User 엔티티 (빌더 패턴 사용)
        testUser = User.builder()
                .email("test@example.com")
                .password(encodedPassword)
                .nickname("tester")
                .role(Role.USER)
                .build();
    }

    // --- 회원가입(SignUp) 테스트 ---

    @Test
    @DisplayName("성공: 유효한 정보로 회원가입에 성공한다")
    void signUp_success() {
        // Given (사전 조건 설정)
        // 1. 중복된 이메일/닉네임이 없다고 가정
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(false);
        // 2. 비밀번호는 항상 암호화됨
        when(passwordEncoder.encode(anyString())).thenReturn(encodedPassword);
        // 3. User 엔티티 저장 시 mock 객체를 반환
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        // When (테스트 실행)
        User savedUser = authService.signUp(signUpRequest);

        // Then (결과 검증)
        // 1. 저장 로직이 호출되었는지 확인
        verify(userRepository).save(any(User.class));
        // 2. 반환된 객체가 예상된 정보와 일치하는지 확인
        assertThat(savedUser.getEmail()).isEqualTo(signUpRequest.getEmail());
        assertThat(savedUser.getPassword()).isEqualTo(encodedPassword);
        assertThat(savedUser.getRole()).isEqualTo(Role.USER);
    }

    @Test
    @DisplayName("실패: 이메일이 중복되면 예외가 발생한다")
    void signUp_fail_duplicateEmail() {
        // Given: 이메일이 이미 존재한다고 가정
        when(userRepository.existsByEmail(anyString())).thenReturn(true);

        // When & Then: IllegalArgumentException이 발생하는지 확인
        assertThrows(IllegalArgumentException.class, () -> authService.signUp(signUpRequest));
        // userRepository.save()는 호출되지 않음을 묵시적으로 확인
    }

    @Test
    @DisplayName("실패: 닉네임이 중복되면 예외가 발생한다")
    void signUp_fail_duplicateNickname() {
        // Given: 닉네임이 이미 존재한다고 가정
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByNickname(anyString())).thenReturn(true);

        // When & Then: IllegalArgumentException이 발생하는지 확인
        assertThrows(IllegalArgumentException.class, () -> authService.signUp(signUpRequest));
    }

    // --- 로그인(Login) 테스트 ---

    @Test
    @DisplayName("성공: 유효한 이메일과 비밀번호로 로그인에 성공한다")
    void login_success() {
        // Given
        // 1. 이메일로 사용자를 찾았다고 가정
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        // 2. 입력된 비밀번호와 저장된 암호화된 비밀번호가 일치한다고 가정
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(true);

        // When
        User authenticatedUser = authService.login(loginRequest);

        // Then
        assertThat(authenticatedUser.getEmail()).isEqualTo(testUser.getEmail());
    }

    @Test
    @DisplayName("실패: 존재하지 않는 이메일로 로그인하면 예외가 발생한다")
    void login_fail_userNotFound() {
        // Given: 이메일로 사용자를 찾을 수 없다고 가정
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.empty());

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
    }

    @Test
    @DisplayName("실패: 비밀번호가 일치하지 않으면 예외가 발생한다")
    void login_fail_passwordMismatch() {
        // Given
        when(userRepository.findByEmail(anyString())).thenReturn(Optional.of(testUser));
        // 비밀번호가 일치하지 않는다고 가정
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        // When & Then
        assertThrows(IllegalArgumentException.class, () -> authService.login(loginRequest));
    }
}