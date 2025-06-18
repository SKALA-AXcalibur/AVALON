import { clientScenarioApi } from "@/services/client/clientScenarioApi";
import { useState } from "react";

const useReadScenario = (scenarioId: string) => {
  const [isLoading, setIsLoading] = useState(false);

  const readScenario = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      const response = await clientScenarioApi.readScenario(scenarioId);
      return response;
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { readScenario, isLoading };
};

export default useReadScenario;
