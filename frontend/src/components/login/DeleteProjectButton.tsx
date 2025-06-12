"use client";
import validateId from "@/utils/validateId";
import ky from "ky";
import { useState } from "react";

const DeleteProjectButton = () => {
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const [isConfirming, setIsConfirming] = useState(false);

  const handleDelete = async (formData: FormData) => {
    try {
      if (!isConfirming) {
        setIsConfirming(true);
        return;
      }

      setIsLoading(true);
      setError(null);
      setSuccess(null);

      const projectId = formData.get("projectId") as string;
      const validation = validateId(projectId);

      if (!validation.isValid) {
        return;
      }

      await ky.delete(
        `${process.env.NEXT_PUBLIC_API_URL}/api/project/v1/${projectId}`,
        {
          credentials: "include",
        }
      );

      setSuccess("프로젝트가 성공적으로 삭제되었습니다.");
      setIsConfirming(false);

      // 성공 메시지를 2초 후에 자동으로 숨김
      setTimeout(() => {
        setSuccess(null);
      }, 2000);
    } catch (error) {
      console.error("Delete failed:", error);
      setError("프로젝트 삭제에 실패했습니다.");
      setIsConfirming(false);
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <div className="flex flex-col">
      <button
        formAction={handleDelete}
        disabled={isLoading}
        className={`w-full py-3 text-lg rounded-lg transition-colors ${
          isLoading
            ? "bg-red-300 text-white cursor-not-allowed"
            : isConfirming
            ? "bg-red-600 text-white hover:bg-red-700"
            : "bg-red-500 text-white hover:bg-red-600"
        }`}
      >
        {isLoading
          ? "삭제 중..."
          : isConfirming
          ? "정말 삭제하시겠습니까?"
          : "프로젝트 삭제"}
      </button>
      <div className="h-4 mt-1">
        {error && (
          <p className="text-sm text-red-600" role="alert">
            {error}
          </p>
        )}
        {success && (
          <p className="text-sm text-green-600" role="status">
            {success}
          </p>
        )}
      </div>
    </div>
  );
};

export default DeleteProjectButton;
