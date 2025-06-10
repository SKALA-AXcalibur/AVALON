"use client";
import { useEffect } from "react";
import { useParams } from "next/navigation";
import ActionButton from "@/components/common/ActionButton";
import useReadProjectScenarios from "@/hooks/scenario/readProjectScenarios";
import useLogout from "@/hooks/auth/logout";

const ScenarioPage = () => {
  const params = useParams();
  const projectId = params["project-id"] as string;
  const { readProjectScenarios, isLoading: isGettingScenarios } =
    useReadProjectScenarios(projectId);
  const { logout, isLoading: isLoggingOut } = useLogout();

  useEffect(() => {
    readProjectScenarios();
  }, []);

  return (
    <div className="min-h-screen bg-white flex flex-col items-center justify-center">
      {isGettingScenarios ? (
        <div className="text-gray-600">시나리오를 가져오는 중입니다...</div>
      ) : (
        <div className="flex flex-col gap-2">
          <ActionButton
            onClick={readProjectScenarios}
            color="bg-blue-500 hover:bg-blue-600"
          >
            다시 가져오기
          </ActionButton>
          <ActionButton
            onClick={logout}
            color="bg-red-500 hover:bg-red-600"
            isLoading={isLoggingOut}
          >
            로그아웃
          </ActionButton>
        </div>
      )}
    </div>
  );
};

export default ScenarioPage;
