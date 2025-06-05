"use client";
import { useEffect } from "react";
import { useRouter } from "next/navigation";
import { useProjectStore } from "@/store/projectStore";
import { useScenarioStore } from "@/store/scenarioStore";
import { useSidebarStore } from "@/store/sidebarStore";

const mockScenarioIds = ["scenario-1", "scenario-2"];

const ScenarioPage = () => {
  const router = useRouter();
  const { project, setProject } = useProjectStore();
  const { setScenario } = useScenarioStore();
  const { setOpenIndex } = useSidebarStore();

  useEffect(() => {
    if (project.id === "") {
      router.push("/login");
      return;
    }

    setProject({ id: project.id, scenarioIds: mockScenarioIds });
    setScenario({
      id: mockScenarioIds[0],
      title: "",
      description: "",
      verify: "",
      testcaseIds: [],
    });
    setOpenIndex(0);

    if (project.scenarioIds.length > 0) {
      router.push(`/project/${project.id}/scenario/${project.scenarioIds[0]}`);
    } else {
      router.push(`/project/${project.id}/upload`);
    }
  }, [project.id, project.scenarioIds.length, router, setProject]);

  return (
    <div className="min-h-screen bg-white flex items-center justify-center">
      <div className="text-gray-600">시나리오를 가져오는 중입니다...</div>
    </div>
  );
};

export default ScenarioPage;
