package com.standardboard.backend.auth.jwt;

import com.standardboard.backend.domain.user.Role;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.security.Security;
import java.util.Date;

/**
 * JWT 토큰 생성, 유효성 검증을 담당하는 유틸리티 클래스 (Provider)
 * Spring Security의 주요 인증 필터에 의해 사용됩니다.
 */
@Log4j2
@Component
public class JwtTokenProvider {

    // 불변성을 위해 final로 선언합니다.
    private final SecretKey secretKey;
    private final long expirationTime; // 밀리초 단위

    /**
     * Best Practice 해설:
     * 1. 생성자 주입(@Value): 설정 파일(application.properties)에서 JWT 키와 만료 시간을 주입받아 사용
     * 2. 즉시 초기화: 주입받은 Base64 문자열을 생성자 내에서 즉시 SecretKey 객체로 변환하여 final 필드를 초기화
     */
    public JwtTokenProvider(@Value("${app.jwt.secret-key}") String secretKeyString,
                            @Value("${app.jwt.expiration-in-ms}") long expirationTime) {

        // Base64 문자열을 디코딩하여 HMAC-SHA 알고리즘에 사용할 SecretKey 객체로 변환
        byte[] keyBytes = Decoders.BASE64.decode(secretKeyString);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationTime = expirationTime;
    }

    /**
     * Access Token을 생성합니다.
     * @param email 토큰에 담을 이메일 (Subject)
     * @param role 토큰에 담을 사용자 권한
     * @return 생성된 JWT Access Token
     */
    public String generateToken(String email, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationTime);

        return Jwts.builder().subject(email)
                .claim("role", role.name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }
    /**
     * JWT에서 클레임(Claims)을 추출하며, 서명 검증 및 유효성 검사를 수행
     * @param token JWT 문자열
     * @return 토큰에 포함된 클레임 객체
     */
    public Claims getClaimsFromToken(String token) {
        return Jwts.parser()
                .decryptWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }


    /**
     * JWT의 유효성을 검증합니다.
     * @param token JWT 문자열
     * @return 유효하면 true, 아니면 false
     */
    public boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature"); // 서명 불일치 (위변조)
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token"); // JWT 형식 오류
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token"); // 토큰 만료
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token"); // 지원하지 않는 형식의 토큰
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty."); // 클레임 문자열이 비어있음
        }
        return false;
    }

}