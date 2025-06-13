import { useState } from "react";
import testcaseApi from "@/services/testcase";

const useReadTestcase = (testcaseId: string) => {
  const [isLoading, setIsLoading] = useState(false);

  const readTestcase = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      const response = await testcaseApi.readTestcase(testcaseId);
      return response;
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { readTestcase, isLoading };
};

export default useReadTestcase;
