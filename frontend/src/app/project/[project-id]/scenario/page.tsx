"use client";
import { useEffect } from "react";
import { useParams, useRouter } from "next/navigation";
import ActionButton from "@/components/common/ActionButton";
import useReadProjectScenarios from "@/hooks/scenario/readProjectScenarios";
import useLogout from "@/hooks/auth/logout";
import { useProjectStore } from "@/store/projectStore";
import useReadScenarioTestcases from "@/hooks/testcase/readScenarioTestcases";

const ScenarioPage = () => {
  const params = useParams();
  const router = useRouter();
  const projectId = params["project-id"] as string;
  const { project } = useProjectStore();
  const { logout, isLoading: isLoggingOut } = useLogout();
  const { readProjectScenarios, isLoading: isGettingScenarios } =
    useReadProjectScenarios();
  const { readScenarioTestcases, isLoading: isGettingTestcases } =
    useReadScenarioTestcases();

  const initProject = async () => {
    let isSuccess = await readProjectScenarios(projectId);
    if (!isSuccess) return;
    if (project.scenarios.length === 0) {
      router.push("/project/upload");
      return;
    }
    isSuccess = await readScenarioTestcases(project.scenarios[0].id);
    if (!isSuccess) return;
    router.push(`/project/${projectId}/scenario/${project.scenarios[0].id}`);
  };

  useEffect(() => {
    initProject();
  }, []);

  return (
    <div className="min-h-screen bg-white flex flex-col items-center justify-center">
      {isGettingScenarios || isGettingTestcases ? (
        <div className="text-gray-600">프로젝트를 가져오는 중입니다...</div>
      ) : (
        <div className="flex flex-col gap-2">
          <ActionButton
            onClick={initProject}
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
