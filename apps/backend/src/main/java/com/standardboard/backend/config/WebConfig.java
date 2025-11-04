package com.standardboard.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web Configuration
 *
 * 클라이언트(프론트엔드)와의 교차 출처 리소스 공유(CORS) 문제를 해결하고,
 * Web MVC 관련 설정을 담당합니다.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**") // 1. CORS를 적용할 API 패턴 (모든 /api/v1/** 요청)
                .allowedOrigins("http://localhost:3000", "http://127.0.0.1:3000") // 2. 허용할 출처(Origin): React 개발 서버 주소
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS") // 3. 허용할 HTTP 메서드
                .allowedHeaders("*") // 4. 모든 헤더 허용
                .allowCredentials(true) // 5. 자격 증명(쿠키, 인증 헤더) 허용
                .maxAge(3600); // 6. 캐시 유지 시간 (초)
    }
}