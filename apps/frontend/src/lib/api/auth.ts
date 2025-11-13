import { useMutation } from "@tanstack/react-query";

// 백엔드 API 응답 구조 (ApiResponse<T>)
interface ApiResponse<T> {
  success: boolean;
  data: T;
  error: {
    code: string;
    message: string;
  } | null;
}

// 1. 요청 DTO 타입 (백엔드의 LoginRequest/SignUpRequest와 동일)
interface AuthRequest {
  email: string;
  password: string;
}

interface SignUpRequest extends AuthRequest {
  nickname: string;
}

// 2. 응답 DTO 타입 (백엔드의 LoginResponse/SignUpResponse와 동일)
interface LoginResponse {
  accessToken: string;
  userId: number;
  email: string;
  nickname: string;
  role: "USER" | "ADMIN";
}

interface SignUpResponse {
  id: number;
  email: string;
  nickname: string;
  role: "USER" | "ADMIN";
}

// 기본 API URL (백엔드 서버 주소)
const API_BASE_URL = "http://localhost:8080/api/v1/auth";

/**
 * API 호출 공통 함수 (POST)
 */
async function postApi<T>(endpoint: string, data: object): Promise<T> {
  const response = await fetch(`${API_BASE_URL}${endpoint}`, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
    },
    body: JSON.stringify(data),
    credentials: "include",
  });

  // 백엔드의 ApiResponse 구조를 파싱
  const result: ApiResponse<T> = await response.json();

  if (!result.success) {
    // 백엔드에서 비즈니스 로직 오류(400 Bad Request 등)를 응답했을 경우
    throw new Error(result.error?.message || "알 수 없는 오류가 발생했습니다.");
  }

  // HTTP 상태 코드가 400 이상이면 fetch가 에러를 던지지 않으므로 수동 처리
  if (!response.ok) {
    throw new Error(result.error?.message || `HTTP 오류: ${response.status}`);
  }

  return result.data;
}

/**
 * 3. 회원가입 뮤테이션 훅
 */
export const useSignUpMutation = () => {
  return useMutation<SignUpResponse, Error, SignUpRequest>({
    mutationFn: (data) => postApi<SignUpResponse>("/signup", data),
  });
};

/**
 * 4. 로그인 뮤테이션 훅
 */
export const useLoginMutation = () => {
  return useMutation<LoginResponse, Error, AuthRequest>({
    mutationFn: (data) => postApi<LoginResponse>("/login", data),
  });
};
