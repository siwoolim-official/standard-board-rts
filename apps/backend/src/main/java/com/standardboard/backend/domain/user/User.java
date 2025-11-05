package com.standardboard.backend.domain.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * User Entity (사용자 도메인 모델)
 *
 * 게시판 서비스를 이용하는 사용자의 정보를 담는 엔티티입니다.
 * 보안, 확장성, JPA Best Practice를 고려하여 설계합니다.
 */
@Entity
@Table(name = "users") // SQL 예약어와 충돌 방지를 위해 'users' 테이블명 사용
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 사용 시 기본 생성자 필수, PROTECTED로 외부 접근 제한
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Primary Key

    // 1. 로그인 ID (Email)
    @Column(nullable = false, unique = true, length = 100)
    private String email;

    // 2. 비밀번호 (암호화된 상태로 저장)
    @Column(nullable = false, length = 255)
    private String password;

    // 3. 닉네임 (게시판에 표시되는 이름)
    @Column(nullable = false, unique = true, length = 50)
    private String nickname;

    // 4. 권한 (일반 사용자, 관리자 등)
    @Enumerated(EnumType.STRING) // Enum 이름을 DB에 저장
    @Column(nullable = false, length = 20)
    private Role role;

    // 5. 계정 생성일
    @CreationTimestamp // 엔티티가 생성될 때 현재 시간을 자동 삽입
    @Column(updatable = false)
    private LocalDateTime createdAt;

    // 빌더 패턴을 이용한 생성자 (생성 시점에 필수 값 주입)
    @Builder
    public User(String email, String password, String nickname, Role role) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        // Role이 명시되지 않으면 기본값으로 USER를 설정 (방어적 프로그래밍)
        this.role = (role != null) ? role : Role.USER;
    }

    /**
     * DTO에서 엔티티로 변환할 때 비밀번호를 암호화하는 별도 메서드입니다.
     * 엔티티 내에서 비즈니스 로직(암호화)을 수행하지 않고, 서비스 레이어에서 처리합니다.
     * 이 엔티티는 JPA의 변경 감지(Dirty Checking)를 위한 메서드만 가집니다. (추후 구현)
     */
}