import testcaseApi from "@/services/testcase";
import { useProjectStore } from "@/store/projectStore";
import { useState } from "react";

const useReadScenarioTestcases = () => {
  const [isLoading, setIsLoading] = useState(false);
  const { project, setProject } = useProjectStore();

  const readScenarioTestcases = async (scenarioId: string) => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await testcaseApi.readScenarioTestcases(scenarioId);
      if (response.tcTotal === 0) return false;
      setProject({
        ...project,
        scenarios: project.scenarios.map((scenario) =>
          scenario.id === scenarioId
            ? { ...scenario, testcases: response.tcList }
            : scenario
        ),
      });
      return true;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    readScenarioTestcases,
    isLoading,
  };
};

export default useReadScenarioTestcases;
