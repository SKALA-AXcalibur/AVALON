import scenarioApi from "@/services/scenario";
import { useState } from "react";

const useCreateScenario = (
  name: string,
  description: string,
  validation: string
) => {
  const [isLoading, setIsLoading] = useState(false);

  const createScenario = async () => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await scenarioApi.createScenario({
        name,
        description,
        validation,
      });
      return response.id;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { createScenario, isLoading };
};

export default useCreateScenario;
