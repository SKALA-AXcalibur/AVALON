import { useState } from "react";
import testcaseApi from "@/services/testcase";
import { useProjectStore } from "@/store/projectStore";

const useDeleteTestcase = () => {
  const [isLoading, setIsLoading] = useState(false);
  const { project, setProject } = useProjectStore();

  const deleteTestcase = async (testcaseId: string) => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await testcaseApi.deleteTestcase(testcaseId);

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
    } finally {
      setIsLoading(false);
    }
  };

  return { deleteTestcase, isLoading };
};

export default useDeleteTestcase;
