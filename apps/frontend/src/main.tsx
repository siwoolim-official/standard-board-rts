import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";
import { BrowserRouter } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";
import { ReactQueryDevtools } from "@tanstack/react-query-devtools";

// 2. QueryClient 인스턴스 생성 및 기본 옵션 설정
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      retry: 1, // 쿼리 실패 시 재시도 횟수
      staleTime: 5 * 60 * 1000, // 5분 동안 fresh 상태 유지 (5분 동안 캐시 데이터 재사용)
    },
  },
});

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    {/* 3. QueryClientProvider로 앱을 감싸서 QueryClient를 주입 */}
    <QueryClientProvider client={queryClient}>
      {/* 2. BrowserRouter로 App을 감싸서 라우팅 기능 활성화 */}
      <BrowserRouter>
        <App />
      </BrowserRouter>
      {/* 4. DevTools 추가: 개발 환경에서만 보이며, 처음에는 닫힌 상태로 설정 */}
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </StrictMode>
);
