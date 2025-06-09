import scenario from "@/services/scenario";
import { useProjectStore } from "@/store/projectStore";
import { useRouter } from "next/navigation";
import { useState } from "react";

const useGetScenarios = (projectId: string) => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const { setProject } = useProjectStore();

  const getScenarios = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      const response = await scenario.getProjectScenarios();
      if (response.total === 0) {
        router.push("/project/upload");
        return;
      }
      setProject({
        id: projectId,
        scenarios: response.sceneList,
      });
      router.push(`/project/${projectId}/scenario/${response.sceneList[0].id}`);
    } catch (error) {
      console.log(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { getScenarios, isLoading };
};

export default useGetScenarios;
