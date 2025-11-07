import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware"; // 토큰 지속성을 위한 미들웨어

// 1. 사용자 정보 타입 정의
interface UserInfo {
  userId: number;
  email: string;
  nickname: string;
  role: "USER" | "ADMIN";
}

// 2. 인증 상태 타입 정의
interface AuthState {
  accessToken: string | null;
  isAuthenticated: boolean;
  user: UserInfo | null;

  // 액션
  login: (token: string, user: UserInfo) => void;
  logout: () => void;
}

// 3. Zustand 스토어 생성 및 로컬 스토리지에 지속(Persist) 설정
export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      // 초기 상태
      accessToken: null,
      isAuthenticated: false,
      user: null,

      // 액션: 로그인 (토큰과 사용자 정보를 저장)
      login: (token, user) =>
        set({
          accessToken: token,
          isAuthenticated: true,
          user: user,
        }),

      // 액션: 로그아웃 (모든 정보를 초기화)
      logout: () =>
        set({
          accessToken: null,
          isAuthenticated: false,
          user: null,
        }),
    }),
    {
      name: "auth-storage", // 로컬 스토리지 키 이름
      storage: createJSONStorage(() => localStorage), // 로컬 스토리지 사용
    }
  )
);
