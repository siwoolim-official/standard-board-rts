package com.standardboard.backend.dto.common;

import lombok.Getter;
import lombok.ToString;

/**
 * 공통 API 응답 구조 (Response Wrapper)
 *
 * @param <T> 응답 본문에 포함될 실제 데이터의 타입
 */
@Getter
@ToString
public class ApiResponse<T> {

    // HTTP 상태 코드가 아닌, 비즈니스 로직 성공/실패 여부를 나타내는 상태 (true: 성공, false: 실패)
    private final boolean success;

    // 실제 응답 데이터 (성공 시)
    private final T data;

    // 오류 정보 (실패 시)
    private final ErrorResponse error;

    // --- 정적 팩토리 메서드: 성공 응답 생성 ---

    /**
     * 데이터를 포함하는 성공 응답을 생성합니다.
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(true, data, null);
    }

    /**
     * 데이터가 없는(Void) 성공 응답을 생성합니다. (예: 삭제, 상태 변경)
     */
    public static ApiResponse<Void> success() {
        return new ApiResponse<>(true, null, null);
    }

    // --- 정적 팩토리 메서드: 실패 응답 생성 ---

    /**
     * 오류 정보를 포함하는 실패 응답을 생성합니다.
     * (이 메서드는 주로 GlobalExceptionHandler에서 호출될 예정입니다.)
     */
    public static ApiResponse<Void> failure(String code, String message) {
        return new ApiResponse<>(false, null, new ErrorResponse(code, message));
    }

    // --- Private 생성자 ---
    private ApiResponse(boolean success, T data, ErrorResponse error) {
        this.success = success;
        this.data = data;
        this.error = error;
    }

    // --- 내부 클래스: 오류 응답 구조 ---

    /**
     * 오류 상세 정보를 담는 내부 클래스
     */
    @Getter
    private static class ErrorResponse {
        // 오류 코드 (예: E400_001)
        private final String code;
        // 오류 메시지
        private final String message;

        public ErrorResponse(String code, String message) {
            this.code = code;
            this.message = message;
        }
    }
}