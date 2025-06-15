import { useProjectStore } from "@/store/projectStore";
import { apiTestApi } from "@/services/apiTest";
import { useState } from "react";

export const useRunApiTest = () => {
  const [isLoading, setIsLoading] = useState(false);
  const { project } = useProjectStore();

  const runApiTest = async () => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      await apiTestApi.runApiTest({
        scenarioList: project.scenarios.map((scenario) => ({
          scenarioId: scenario.id,
        })),
      });
      return true;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { runApiTest, isLoading };
};
