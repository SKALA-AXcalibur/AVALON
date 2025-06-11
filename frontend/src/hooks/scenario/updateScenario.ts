import scenarioApi from "@/services/scenario";
import { useState } from "react";

const useUpdateScenario = (
  scenarioId: string,
  name: string,
  description: string,
  validation: string
) => {
  const [isLoading, setIsLoading] = useState(false);

  const updateScenario = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await scenarioApi.updateScenario(scenarioId, {
        name,
        description,
        validation,
      });
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { updateScenario, isLoading };
};

export default useUpdateScenario;
