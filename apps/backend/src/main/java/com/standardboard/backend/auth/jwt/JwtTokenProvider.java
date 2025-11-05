package com.standardboard.backend.auth.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

/**
 * JWT 토큰 생성, 유효성 검증을 담당하는 유틸리티 클래스 (Provider)
 *
 * Spring Security의 주요 인증 필터에 의해 사용됩니다.
 */
@Component
public class JwtTokenProvider {

    private final Key signingKey;
    private final long expirationTime;

    // application.properties에서 secret-key와 expiration-time을 주입받아 사용합니다.
    public JwtTokenProvider(@Value("${jwt.secret-key}") String secretKey,
                            @Value("${jwt.expiration-time}") long expirationTime) {
        // Base64 디코딩을 통해 Secret Key를 Key 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
    }

    /**
     * Access Token을 생성합니다.
     * @param userId 토큰에 담을 사용자 ID
     * @param email 토큰에 담을 이메일 (Subject)
     * @param role 토큰에 담을 사용자 권한
     * @return 생성된 JWT Access Token
     */
    public String generateToken(Long userId, String email, String role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder()
                .setSubject(email) // 토큰 제목/주체 (여기서는 이메일 사용)
                .claim("userId", userId) // 사용자 고유 ID를 Custom Claim으로 저장
                .claim("role", role)     // 사용자 권한을 Custom Claim으로 저장
                .setIssuedAt(now)        // 토큰 발행 시간
                .setExpiration(expiryDate) // 토큰 만료 시간
                .signWith(signingKey, SignatureAlgorithm.HS256) // HS256 알고리즘으로 서명
                .compact();
    }
}