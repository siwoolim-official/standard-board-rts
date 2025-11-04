import "./App.css";

function App() {
  return (
    <div className="min-h-screen flex flex-col items-center justify-center bg-gray-50">
      {/* 1. Tailwind 클래스를 적용합니다. */}
      <h1 className="text-4xl font-extrabold text-indigo-600 mb-4">
        Tailwind CSS 테스트 성공! 🎉
      </h1>

      {/* 2. 작은 텍스트와 배경색 테스트 */}
      <p className="text-lg text-gray-700 p-2 border border-dashed border-gray-400">
        이 글씨가 파란색이고 굵게 보인다면 정상 작동입니다.
      </p>
    </div>
  );
}

export default App;
