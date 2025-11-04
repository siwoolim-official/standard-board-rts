import { create } from "zustand";

// 1. 상태 타입 정의
type Theme = "light" | "dark" | "system";

interface ThemeState {
  theme: Theme;
  setTheme: (theme: Theme) => void;
  toggleTheme: () => void;
}

// 2. Zustand 스토어 생성
// create() 함수를 사용하여 상태와 액션을 정의합니다.
export const useThemeStore = create<ThemeState>((set, get) => ({
  // 초기 상태
  theme: "system",

  // 액션: 테마를 변경하는 함수
  setTheme: (newTheme) => set({ theme: newTheme }),

  // 액션: light <-> dark를 토글하는 함수
  toggleTheme: () => {
    const currentTheme = get().theme;
    const newTheme = currentTheme === "dark" ? "light" : "dark";
    set({ theme: newTheme });
  },
}));
