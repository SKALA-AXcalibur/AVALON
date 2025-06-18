import { clientScenarioApi } from "@/services/client/clientScenarioApi";
import { useProjectStore } from "@/store/projectStore";
import { useState } from "react";

const useReadProjectScenarios = () => {
  const [isLoading, setIsLoading] = useState(false);
  const { setProject } = useProjectStore();

  const readProjectScenarios = async (projectId: string) => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await clientScenarioApi.readProjectScenarios();
      if (response.total === 0) return false;
      setProject({
        id: projectId,
        scenarios: response.scenarioList.map((s) => ({ ...s, testcases: [] })),
      });
      return true;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { readProjectScenarios, isLoading };
};

export default useReadProjectScenarios;
