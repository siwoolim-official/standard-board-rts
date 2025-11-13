import React, { useState } from "react";
import { useNavigate } from "react-router-dom";
import { Button } from "../components/ui/button";
// import { Input } from "../components/ui/input"; // Input 컴포넌트가 아직 없지만, 구현된다고 가정
import { useLoginMutation, useSignUpMutation } from "../lib/api/auth";
import { useAuthStore } from "../store/useAuthStore";

/**
 * 입력 컴포넌트 (임시)
 * 이 컴포넌트는 shadcn/ui의 Input 컴포넌트를 대체한다고 가정합니다.
 */
interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label: string;
  id: string;
}
const CustomInput: React.FC<InputProps> = ({ label, id, ...props }) => (
  <div className="flex flex-col space-y-1.5">
    <label htmlFor={id} className="text-sm font-medium">
      {label}
    </label>
    <input
      id={id}
      className="flex h-10 w-full rounded-md border border-input bg-background px-3 py-2 text-sm ring-offset-background"
      {...props}
    />
  </div>
);

const AuthPage = () => {
  const [isLogin, setIsLogin] = useState(true);
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [nickname, setNickname] = useState("");

  const navigate = useNavigate();
  const loginAction = useAuthStore((state) => state.login);

  // TanStack Query 뮤테이션 훅
  const signUpMutation = useSignUpMutation();
  const loginMutation = useLoginMutation();

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();

    if (isLogin) {
      // 로그인 처리
      loginMutation.mutate(
        { email, password },
        {
          onSuccess: (data) => {
            // 1. Zustand에 토큰 및 사용자 정보 저장
            loginAction({
              userId: data.userId,
              email: data.email,
              nickname: data.nickname,
              role: data.role,
            });
            alert("로그인 성공!");
            navigate("/"); // 홈으로 이동
          },
          onError: (error) => {
            alert(`로그인 실패: ${error.message}`);
          },
        }
      );
    } else {
      // 회원가입 처리
      signUpMutation.mutate(
        { email, password, nickname },
        {
          onSuccess: () => {
            alert("회원가입 성공! 로그인해 주세요.");
            setIsLogin(true); // 로그인 화면으로 전환
          },
          onError: (error) => {
            alert(`회원가입 실패: ${error.message}`);
          },
        }
      );
    }
  };

  const isLoading = signUpMutation.isPending || loginMutation.isPending;

  return (
    <div className="flex justify-center items-center min-h-[80vh]">
      <div className="w-full max-w-md p-8 space-y-6 bg-card shadow-xl rounded-xl">
        <h1 className="text-2xl font-bold text-center text-foreground">
          {isLogin ? "로그인" : "회원가입"}
        </h1>

        <form onSubmit={handleSubmit} className="space-y-4">
          <CustomInput
            label="이메일"
            id="email"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            required
            disabled={isLoading}
          />

          {!isLogin && (
            <CustomInput
              label="닉네임"
              id="nickname"
              type="text"
              value={nickname}
              onChange={(e) => setNickname(e.target.value)}
              required
              disabled={isLoading}
            />
          )}

          <CustomInput
            label="비밀번호"
            id="password"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            required
            disabled={isLoading}
          />

          <Button type="submit" className="w-full" disabled={isLoading}>
            {isLoading ? "처리 중..." : isLogin ? "로그인" : "회원가입"}
          </Button>
        </form>

        <p className="text-center text-sm text-muted-foreground">
          {isLogin ? "계정이 없으신가요?" : "이미 계정이 있으신가요?"}{" "}
          <button
            type="button"
            className="text-primary hover:underline font-medium"
            onClick={() => setIsLogin(!isLogin)}
            disabled={isLoading}
          >
            {isLogin ? "회원가입" : "로그인"}
          </button>
        </p>
      </div>
    </div>
  );
};

export default AuthPage;
