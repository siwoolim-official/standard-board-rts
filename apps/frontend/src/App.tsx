import { Routes, Route, Link } from "react-router-dom";
import HomePage from "./pages/HomePage"; // 생성한 페이지 import
import AboutPage from "./pages/AboutPage"; // 생성한 페이지 import
import "./App.css";

function App() {
  return (
    <div className="min-h-screen bg-gray-100 p-4">
      {/* 라우팅을 위한 간단한 네비게이션 메뉴 */}
      <nav className="mb-8 p-4 bg-white shadow rounded-lg">
        <Link
          to="/"
          className="text-blue-500 hover:text-blue-700 mr-4 font-semibold"
        >
          홈
        </Link>
        <Link
          to="/about"
          className="text-blue-500 hover:text-blue-700 font-semibold"
        >
          소개
        </Link>
      </nav>

      {/* URL 경로에 따라 렌더링할 컴포넌트를 정의 */}
      <Routes>
        <Route path="/" element={<HomePage />} />
        <Route path="/about" element={<AboutPage />} />
        {/* 모든 매칭되지 않는 경로를 위한 404 페이지도 추가 가능합니다. */}
        <Route
          path="*"
          element={<h2 className="text-red-500 text-center">404 Not Found</h2>}
        />
      </Routes>
    </div>
  );
}

export default App;
