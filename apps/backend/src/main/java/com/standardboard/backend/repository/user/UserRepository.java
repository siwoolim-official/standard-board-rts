package com.standardboard.backend.repository.user;

import com.standardboard.backend.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository (사용자 데이터 접근 계층)
 *
 * Spring Data JPA를 사용하여 기본적인 CRUD 및 사용자 정의 쿼리를 제공합니다.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // 1. 이메일(로그인 ID)로 사용자 정보를 조회합니다. (로그인 및 중복 확인용)
    Optional<User> findByEmail(String email);

    // 2. 닉네임 중복을 확인합니다.
    boolean existsByNickname(String nickname);

    // 3. 이메일 중복을 확인합니다.
    boolean existsByEmail(String email);
}