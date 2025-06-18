import { useState } from "react";
import { clientTestcaseApi } from "@/services/client/clientTestcaseApi";
import { useProjectStore } from "@/store/projectStore";

const useDeleteTestcase = () => {
  const [isLoading, setIsLoading] = useState(false);
  const { project, setProject } = useProjectStore();

  const deleteTestcase = async (testcaseId: string) => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await clientTestcaseApi.deleteTestcase(testcaseId);

      const updatedScenarios = project.scenarios.map((scenario) => ({
        ...scenario,
        testcases: scenario.testcases.filter(
          (testcase) => testcase.tcId !== testcaseId
        ),
      }));

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

  return { deleteTestcase, isLoading };
};

export default useDeleteTestcase;
