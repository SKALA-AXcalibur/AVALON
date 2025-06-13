import { TestcaseInfo } from "@/interfaces/testcase";
import testcaseApi from "@/services/testcase";
import { useState } from "react";

const useCreateTestcase = () => {
  const [isLoading, setIsLoading] = useState(false);

  const createTestcase = async (
    scenarioId: string,
    testcaseInfo: TestcaseInfo
  ) => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await testcaseApi.createTestcase(scenarioId, {
        precondition: testcaseInfo.precondition,
        description: testcaseInfo.description,
        expectedResult: testcaseInfo.expectedResult,
        testDataList: testcaseInfo.testDataList,
      });
      return response.tcId;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { createTestcase, isLoading };
};

export default useCreateTestcase;
