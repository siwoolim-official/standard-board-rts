import { StrictMode } from "react";
import { createRoot } from "react-dom/client";
import "./index.css";
import App from "./App.tsx";

import { BrowserRouter } from "react-router-dom";

createRoot(document.getElementById("root")!).render(
  <StrictMode>
    {/* 2. BrowserRouter로 App을 감싸서 라우팅 기능 활성화 */}
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </StrictMode>
);
