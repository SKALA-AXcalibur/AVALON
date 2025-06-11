import scenarioApi from "@/services/scenario";
import { useProjectStore } from "@/store/projectStore";
import { Scenario } from "@/interfaces/scenario";
import { useState } from "react";

const useReadProjectScenarios = () => {
  const [isLoading, setIsLoading] = useState(false);
  const { setProject } = useProjectStore();

  const readProjectScenarios = async (projectId: string) => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await scenarioApi.readProjectScenarios();
      if (response.total === 0) return false;
      setProject({
        id: projectId,
        scenarios: response.scenarioList as Scenario[],
      });
      return true;
    } catch (error) {
      console.log(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { readProjectScenarios, isLoading };
};

export default useReadProjectScenarios;
