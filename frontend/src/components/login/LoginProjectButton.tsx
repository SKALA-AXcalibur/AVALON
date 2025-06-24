"use client";
import validateId from "@/utils/ValidateId";
import ky from "ky";
import { useRouter } from "next/navigation";
import { useState } from "react";

interface ProjectResponse {
  projectId: string;
}

const LoginProjectButton = () => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (formData: FormData) => {
    try {
      setIsLoading(true);
      setError(null);

      const projectId = formData.get("projectId") as string;
      const validation = validateId(projectId);

      if (!validation.isValid) {
        return;
      }

      const response = await ky.post(
        `${process.env.NEXT_PUBLIC_API_URL}/api/project/v1/`,
        {
          json: { projectId },
          credentials: "include",
        }
      );

      const data = (await response.json()) as ProjectResponse;
      router.push(`/project/${data.projectId}/scenario`);
    } catch (error) {
      console.error("Login failed:", error);
      setError(
        "로그인에 실패했습니다. 프로젝트 ID를 확인하거나 잠시 후 다시 시도해주세요."
      );
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col">
      <button
        formAction={handleLogin}
        disabled={isLoading}
        className={`px-6 py-3 text-lg rounded-lg transition-colors ${
          isLoading
            ? "bg-blue-300 text-white cursor-not-allowed"
            : "bg-blue-500 text-white hover:bg-blue-600"
        }`}
      >
        {isLoading ? "로그인 중..." : "로그인"}
      </button>
      <div className="h-4 mt-1">
        {error && (
          <p className="text-sm text-red-600" role="alert">
            {error}
          </p>
        )}
      </div>
    </div>
  );
};

export default LoginProjectButton;
