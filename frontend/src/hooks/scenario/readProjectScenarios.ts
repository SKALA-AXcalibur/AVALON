import scenarioApi from "@/services/scenario";
import { useProjectStore } from "@/store/projectStore";
import { useRouter } from "next/navigation";
import { useState } from "react";

const useReadProjectScenarios = (projectId: string) => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const { setProject } = useProjectStore();

  const readProjectScenarios = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      const response = await scenarioApi.readProjectScenarios();
      if (response.total === 0) {
        router.push("/project/upload");
        return;
      }
      setProject({
        id: projectId,
        scenarios: response.scenarioList,
      });
      router.push(
        `/project/${projectId}/scenario/${response.scenarioList[0].id}`
      );
    } catch (error) {
      console.log(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { readProjectScenarios, isLoading };
};

export default useReadProjectScenarios;
