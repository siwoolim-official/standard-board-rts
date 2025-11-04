import { clsx, type ClassValue } from "clsx";
import { twMerge } from "tailwind-merge";

/**
 * Tailwind 클래스를 조건부로 병합하고 정리하는 유틸리티 함수입니다.
 * @param inputs - 결합할 클래스 값 (문자열, 배열, 객체)
 * @returns 병합되고 최적화된 클래스 문자열
 *
 * shadcn/ui에서 클래스 관리를 위해 사용하는 표준 방식입니다.
 */
export function cn(...inputs: ClassValue[]) {
  return twMerge(clsx(inputs));
}
