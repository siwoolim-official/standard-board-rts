package com.standardboard.backend.health;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller
 *
 * 서버의 상태를 외부에 알리는 API를 제공합니다.
 * 백엔드 서버가 정상적으로 실행되었는지 확인하는 가장 첫 번째 단계입니다.
 */
@RestController
public class HealthController {

    @GetMapping("/api/v1/health")
    public Map<String, String> checkHealth() {
        Map<String, String> response = new HashMap<>();
        // 서버 상태는 'OK'로 응답하며, 프로젝트 이름과 버전을 함께 반환합니다.
        response.put("status", "OK");
        response.put("application", "standard-board-rts-backend");
        response.put("version", "1.0.0");
        return response;
    }
}