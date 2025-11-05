package com.standardboard.backend.domain.user;

/**
 * 사용자 권한(Role) 정의
 *
 * 권한 관리는 '일반 사용자'와 '관리자(Admin)' 역할 분리가 핵심입니다.
 */
public enum Role {
    USER, // 일반 사용자
    ADMIN // 관리자
}