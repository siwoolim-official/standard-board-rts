import "./App.css";
import { Button } from "./components/ui/button"; // 2번 단계에서 생성된 경로

function App() {
  return (
    <div className="p-4">
      <h1>Standard Board Project</h1>
      <Button>Click me</Button> {/* shadcn/ui 버튼 테스트 */}
    </div>
  );
}

export default App;
