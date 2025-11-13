import { create } from "zustand";
// import { persist, createJSONStorage } from "zustand/middleware"; // 토큰 지속성을 위한 미들웨어

// 1. 사용자 정보 타입 정의
interface UserInfo {
  userId: number;
  email: string;
  nickname: string;
  role: "USER" | "ADMIN";
}

// 2. 인증 상태 타입 정의
interface AuthState {
  isAuthenticated: boolean;
  user: UserInfo | null;

  // 액션
  login: (user: UserInfo) => void;
  logout: () => void;
}

// 3. Zustand 스토어 생성 및 로컬 스토리지에 지속(Persist) 설정
export const useAuthStore = create<AuthState>()((set) => ({
  isAuthenticated: false,
  user: null,

  // 액션: 로그인 (토큰은 쿠키가 관리하므로, 사용자 정보만 저장)
  login: (user) =>
    set({
      isAuthenticated: true,
      user: user,
    }),

  // 액션: 로그아웃
  logout: () =>
    set({
      isAuthenticated: false,
      user: null,
    }),
}));
