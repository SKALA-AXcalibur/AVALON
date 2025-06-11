import scenarioApi from "@/services/scenario";
import { useState } from "react";

const useReadScenario = (scenarioId: string) => {
  const [isLoading, setIsLoading] = useState(false);

  const readScenario = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      const response = await scenarioApi.readScenario(scenarioId);
      return response;
    } catch (error) {
      console.log(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { readScenario, isLoading };
};

export default useReadScenario;
