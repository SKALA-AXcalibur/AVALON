import scenarioApi from "@/services/scenario";
import { useState } from "react";
import { useProjectStore } from "@/store/projectStore";

const useDeleteScenario = (scenarioId: string) => {
  const [isLoading, setIsLoading] = useState(false);
  const { project, setProject } = useProjectStore();

  const deleteScenario = async () => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      await scenarioApi.deleteScenario(scenarioId);

      const updatedScenarios = project.scenarios.filter(
        (scenario) => scenario.id !== scenarioId
      );
      setProject({
        ...project,
        scenarios: updatedScenarios,
      });

      return true;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { deleteScenario, isLoading };
};

export default useDeleteScenario;
