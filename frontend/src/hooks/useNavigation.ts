import { useParams, usePathname } from "next/navigation";
import { useState } from "react";
import { clientAuthApi } from "@/services/client/clientAuthApi";
import { clientTestcaseApi } from "@/services/client/clientTestcaseApi";
import { clientApiTestApi } from "@/services/client/clientApiTestApi";
import { useProjectStore } from "@/store/projectStore";
import { useSidebarStore } from "@/store/sidebarStore";
import { useTestResultStore } from "@/store/testResult";

type ActionButton = {
  type: "action";
  onClick: () => void | Promise<void>;
  color: string;
  text: string;
  loading: boolean;
};

type LinkButton = {
  type: "link";
  href: string;
  color: string;
  text: string;
};

type NavigationButton = ActionButton | LinkButton;

type NavigationCallbacks = {
  onLogoutSuccess?: () => void;
  onGenerateTestcasesSuccess?: () => void;
  onRunApiTestSuccess?: (scenarioId: string) => void;
};

export const useNavigation = (callbacks?: NavigationCallbacks) => {
  const { "project-id": projectId, "scenario-id": scenarioId } = useParams();
  const pathname = usePathname();
  const { project, resetProject, resetAllScenarios } = useProjectStore();
  const { resetSidebar } = useSidebarStore();
  const { setTestResult } = useTestResultStore();
  const [loadingStates, setLoadingStates] = useState({
    logout: false,
    generateTestcases: false,
    runApiTest: false,
  });

  const logout = async () => {
    setLoadingStates((prev) => ({ ...prev, logout: true }));
    try {
      await clientAuthApi.logout();
      resetProject();
      resetSidebar();
      callbacks?.onLogoutSuccess?.();
    } catch (error) {
      console.error(error);
    } finally {
      setLoadingStates((prev) => ({ ...prev, logout: false }));
    }
  };

  const generateTestcases = async () => {
    setLoadingStates((prev) => ({ ...prev, generateTestcases: true }));
    try {
      await clientTestcaseApi.generate();
      resetAllScenarios();
      resetSidebar();
      callbacks?.onGenerateTestcasesSuccess?.();
    } catch (error) {
      console.error(error);
    } finally {
      setLoadingStates((prev) => ({ ...prev, generateTestcases: false }));
    }
  };

  const handleRunApiTest = async () => {
    setLoadingStates((prev) => ({ ...prev, runApiTest: true }));
    try {
      await clientApiTestApi.runApiTest({
        scenarioList: project.scenarios.map((scenario) => scenario.id),
      });
      const testResult = await clientApiTestApi.readApiTestResult();
      setTestResult({
        scenarioList: testResult.scenarioList.map((scenario) => ({
          ...scenario,
          tcList: [],
        })),
      });
      callbacks?.onRunApiTestSuccess?.(testResult.scenarioList[0].scenarioId);
    } catch (error) {
      console.error(error);
    } finally {
      setLoadingStates((prev) => ({ ...prev, runApiTest: false }));
    }
  };

  const getButtons = (): NavigationButton[] => {
    const commonButtons = {
      logout: {
        type: "action" as const,
        onClick: logout,
        color: "bg-blue-500 hover:bg-blue-600",
        text: "로그아웃",
        loading: loadingStates.logout,
      },
      backToScenario: {
        type: "link" as const,
        href: `/project/${projectId}/scenario/${
          project.scenarios[0]?.id || ""
        }`,
        color: "bg-gray-500 hover:bg-gray-600",
        text: "돌아가기",
      },
    };

    if (pathname.includes("/scenario/")) {
      return [
        commonButtons.logout,
        {
          type: "link" as const,
          href: `/project/${projectId}/upload`,
          color: "bg-violet-500 hover:bg-violet-600",
          text: "파일 첨부",
        },
        {
          type: "action" as const,
          onClick: generateTestcases,
          color: "bg-pink-500 hover:bg-pink-600",
          text: "TC 일괄 생성",
          loading: loadingStates.generateTestcases,
        },
        {
          type: "action" as const,
          onClick: handleRunApiTest,
          color: "bg-emerald-500 hover:bg-emerald-600",
          text: "테스트 실행",
          loading: loadingStates.runApiTest,
        },
      ];
    }

    if (pathname.includes("/upload")) {
      if (project.scenarios.length > 0) {
        return [commonButtons.logout, commonButtons.backToScenario];
      }
      return [commonButtons.logout];
    }

    if (pathname.includes("/test-run/")) {
      return [commonButtons.logout, commonButtons.backToScenario];
    }

    return [commonButtons.logout];
  };

  return {
    projectId: projectId as string,
    scenarioId: scenarioId as string,
    getButtons,
  };
};
