import { Routes, Route, Link } from "react-router-dom";
import HomePage from "./pages/HomePage";
import AboutPage from "./pages/AboutPage";
import AuthPage from "./pages/AuthPage";
import "./App.css";
import { Button } from "./components/ui/button";
import { useThemeStore } from "./store/useThemeStore";
import { useAuthStore } from "./store/useAuthStore";

function App() {
  const theme = useThemeStore((state) => state.theme);
  const toggleTheme = useThemeStore((state) => state.toggleTheme);

  // 인증 상태와 액션 가져오기
  const isAuthenticated = useAuthStore((state) => state.isAuthenticated);
  const user = useAuthStore((state) => state.user);
  const logout = useAuthStore((state) => state.logout);

  const rootClass = theme === "dark" ? "dark" : "";

  return (
    <div className={rootClass}>
      <div className="min-h-screen bg-background text-foreground p-4 transition-colors duration-300">
        {/* 네비게이션 및 테마 버튼 */}
        <nav className="mb-8 p-4 bg-card shadow rounded-lg flex justify-between items-center">
          <div className="flex items-center space-x-4">
            <Link
              to="/"
              className="text-lg font-bold text-primary hover:text-primary-foreground mr-4"
            >
              게시판 홈
            </Link>
            {/* <Link
              to="/about"
              className="text-primary hover:text-primary-foreground font-semibold"
            >
              소개
            </Link> */}

            {/* 로그인 상태에 따라 환영 메시지 표시 */}
            {isAuthenticated && (
              <span className="text-sm text-muted-foreground ml-4">
                환영합니다, {user?.nickname}님!
              </span>
            )}
          </div>

          <div className="flex items-center space-x-2">
            {/* ⬇️ 인증 상태에 따른 버튼 */}
            {isAuthenticated ? (
              <Button onClick={logout} variant="destructive">
                로그아웃
              </Button>
            ) : (
              <Link to="/auth">
                <Button variant="default">로그인/회원가입</Button>
              </Link>
            )}

            {/* 테마 버튼 */}
            <Button onClick={toggleTheme} variant="secondary">
              테마 전환: {theme.toUpperCase()}
            </Button>
          </div>
        </nav>

        {/* 라우팅 영역 (AuthPage 추가) */}
        <Routes>
          <Route path="/" element={<HomePage />} />
          <Route path="/about" element={<AboutPage />} />
          <Route path="/auth" element={<AuthPage />} />
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
