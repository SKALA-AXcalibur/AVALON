import scenarioApi from "@/services/scenario";
import { useState } from "react";

const useDeleteScenario = (scenarioId: string) => {
  const [isLoading, setIsLoading] = useState(false);

  const deleteScenario = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await scenarioApi.deleteScenario(scenarioId);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { deleteScenario, isLoading };
};

export default useDeleteScenario;
