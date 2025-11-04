import { Routes, Route, Link } from "react-router-dom";
import HomePage from "./pages/HomePage"; // 생성한 페이지 import
import AboutPage from "./pages/AboutPage"; // 생성한 페이지 import
import "./App.css";
import { Button } from "./components/ui/button"; // shadcn/ui Button 컴포넌트
import { useThemeStore } from "./store/useThemeStore"; // Zustand 스토어 임포트

function App() {
  const theme = useThemeStore((state) => state.theme);
  const toggleTheme = useThemeStore((state) => state.toggleTheme);

  // 현재 테마 상태에 따라 최상위 HTML 엘리먼트에 클래스를 적용
  const rootClass = theme === "dark" ? "dark" : "";

  return (
    // Tailwind CSS의 다크 모드를 활성화하기 위해 클래스를 적용합니다.
    <div className={rootClass}>
      <div className="min-h-screen bg-background text-foreground p-4 transition-colors duration-300">
        {/* 네비게이션 및 테마 버튼 */}
        <nav className="mb-8 p-4 bg-card shadow rounded-lg flex justify-between items-center">
          <div>
            <Link
              to="/"
              className="text-primary hover:text-primary-foreground mr-4 font-semibold"
            >
              홈
            </Link>
            <Link
              to="/about"
              className="text-primary hover:text-primary-foreground font-semibold"
            >
              소개
            </Link>
          </div>

          {/* Zustand 상태를 사용한 토글 버튼 */}
          <Button onClick={toggleTheme} variant="secondary">
            테마 전환: {theme.toUpperCase()}
          </Button>
        </nav>

        {/* 라우팅 영역 */}
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route
            path="*"
            element={
              <h2 className="text-red-500 text-center">404 Not Found</h2>
            }
          />
        </Routes>
      </div>
    </div>
  );
}

export default App;
