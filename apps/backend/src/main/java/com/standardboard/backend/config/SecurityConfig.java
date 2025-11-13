package com.standardboard.backend.config;

import com.standardboard.backend.auth.jwt.JwtTokenProvider;
import com.standardboard.backend.service.auth.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.standardboard.backend.auth.jwt.JwtAuthenticationFilter;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.ContentTypeOptionsConfig;
import java.util.Arrays;
import java.util.List;

/**
 * Spring Security Configuration
 *
 * 이 애플리케이션은 REST API 기반이므로, JWT를 염두에 두고
 * 세션 없이(Stateless) 동작하도록 설계합니다.
 * 또한, CORS 문제를 해결하고, 공개 API(/health)에 대한 접근을 허용합니다.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // JWT 컴포넌트들을 의존성 주입받도록 필드 추가
    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    // Lombok의 @RequiredArgsConstructor 대신 수동 생성자 주입
    public SecurityConfig(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    /**
     * 비밀번호 암호화(해싱)를 위한 Encoder Bean을 등록합니다.
     * 표준 게시판 프로젝트에서는 강력한 BCrypt 해싱 알고리즘을 사용합니다.
     * @return PasswordEncoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * CORS(Cross-Origin Resource Sharing) 설정을 위한 Bean을 등록합니다.
     * WebConfig.java의 설정을 Spring Security 레벨에서 적용합니다.
     * @return CorsConfigurationSource
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // WebConfig.java의 설정과 동일하게 프론트엔드 개발 서버 주소를 지정합니다.
        configuration.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        return source;
    }

    /**
     * Spring Security의 FilterChain을 구성합니다.
     * @param http HttpSecurity 객체
     * @return SecurityFilterChain
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        // 1. CSRF 비활성화: REST API는 세션을 사용하지 않고 토큰으로 인증하므로 비활성화합니다.
        http.csrf(AbstractHttpConfigurer::disable);

        // 2. CORS 설정 적용: 위에서 정의한 corsConfigurationSource Bean을 적용합니다.
        http.cors(cors -> cors.configurationSource(corsConfigurationSource()));

        // 3. 세션 관리: JWT 기반 인증을 위해 세션을 사용하지 않도록(Stateless) 설정합니다.
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        // 4. 인증/인가 설정: URI 패턴에 따라 접근 권한을 설정합니다.
        http.authorizeHttpRequests(authz -> authz
                // Health Check API는 인증 없이 접근을 허용합니다.
                .requestMatchers("/api/v1/health").permitAll()
                // H2 Console 경로도 인증 없이 접근을 허용합니다.
                .requestMatchers("/h2-console/**").permitAll()
                // 회원가입, 로그인 등 인증 관련 API도 인증 없이 접근을 허용합니다. (추후 구현 예정)
                .requestMatchers("/api/v1/auth/**").permitAll()
                // 그 외 모든 /api/** 요청은 인증(로그인)이 필요합니다.
                .requestMatchers("/api/**").authenticated()
                // 나머지 모든 요청(정적 리소스 등)은 허용합니다.
                .anyRequest().permitAll()
        );

        http.headers(headers -> headers
                // H2 Console을 위한 클릭재킹 방어 비활성화
                .frameOptions(FrameOptionsConfig::disable)
                // Content-Type 옵션 활성화 (보안 강화)
                .contentTypeOptions(ContentTypeOptionsConfig::disable)
        );

        // JWT 인증 필터 등록
        http.addFilterBefore(
                new JwtAuthenticationFilter(jwtTokenProvider, userDetailsService), // 생성한 JWT 필터 객체 생성
                UsernamePasswordAuthenticationFilter.class // 스프링 기본 인증 필터 이전에 실행
        );

        // 기본 인증 비활성화: 폼 로그인, HTTP Basic 인증은 사용하지 않으므로 비활성화합니다.
        http.formLogin(AbstractHttpConfigurer::disable);
        http.httpBasic(AbstractHttpConfigurer::disable);

        return http.build();
    }
}