import { useState } from "react";
import testcaseApi from "@/services/testcase";
import { TestcaseInfo } from "@/interfaces/testcase";

const useUpdateTestcase = () => {
  const [isLoading, setIsLoading] = useState(false);

  const updateTestcase = async (testcaseInfo: TestcaseInfo) => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await testcaseApi.updateTestcase(testcaseInfo.tcId, {
        precondition: testcaseInfo.precondition,
        description: testcaseInfo.description,
        expectedResult: testcaseInfo.expectedResult,
        testDataList: testcaseInfo.testDataList,
      });
      return true;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { updateTestcase, isLoading };
};

export default useUpdateTestcase;
