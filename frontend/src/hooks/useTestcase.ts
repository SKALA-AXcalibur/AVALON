import { useState, useEffect } from "react";
import { useProjectStore } from "@/store/projectStore";
import { clientTestcaseApi } from "@/services/client/clientTestcaseApi";
import { ERROR_MESSAGES, SUCCESS_MESSAGES } from "@/constants/messages";
import { TestcaseInfo, TestData } from "@/interfaces/testcase";

export const useTestcase = (
  projectId: string,
  scenarioId: string,
  testcaseId: string,
) => {
  const { addTestcase, updateTestcase, deleteTestcase } = useProjectStore();

  const [testcaseInfo, setTestcaseInfo] = useState<TestcaseInfo>({
    tcId: testcaseId,
    precondition: "",
    description: "",
    expectedResult: "",
    testDataList: [],
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  useEffect(() => {
    if (error || success) {
      const timer = setTimeout(() => {
        setError(null);
        setSuccess(null);
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [error, success]);

  useEffect(() => {
    if (testcaseId !== "new") {
      const fetchTestcaseInfo = async () => {
        try {
          setIsLoading(true);
          const testcase = await clientTestcaseApi.readTestcase(testcaseId);
          setTestcaseInfo(testcase);
        } catch (error) {
          console.error(error);
          setError(ERROR_MESSAGES.TESTCASE.READ_FAILED);
        } finally {
          setIsLoading(false);
        }
      };
      fetchTestcaseInfo();
    }
  }, [testcaseId]);

  const handlePreconditionChange = (value: string) => {
    setTestcaseInfo((prev) => ({ ...prev, precondition: value }));
  };

  const handleDescriptionChange = (value: string) => {
    setTestcaseInfo((prev) => ({ ...prev, description: value }));
  };

  const handleExpectedResultChange = (value: string) => {
    setTestcaseInfo((prev) => ({ ...prev, expectedResult: value }));
  };

  const handleTestDataListChange = (testDataList: TestData[]) => {
    setTestcaseInfo((prev) => ({ ...prev, testDataList }));
  };

  const handleCreate = async (onSuccess?: (testcaseId: string) => void) => {
    try {
      setIsLoading(true);
      const response = await clientTestcaseApi.createTestcase(scenarioId, {
        precondition: testcaseInfo.precondition,
        description: testcaseInfo.description,
        expectedResult: testcaseInfo.expectedResult,
        testDataList: testcaseInfo.testDataList,
      });
      addTestcase(scenarioId, {
        tcId: response.tcId,
      });
      setSuccess(SUCCESS_MESSAGES.TESTCASE.CREATE_SUCCESS);
      onSuccess?.(response.tcId);
    } catch (error) {
      console.error(error);
      setError(ERROR_MESSAGES.TESTCASE.CREATE_FAILED);
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdate = async () => {
    try {
      setIsLoading(true);
      await clientTestcaseApi.updateTestcase(testcaseInfo.tcId, {
        precondition: testcaseInfo.precondition,
        description: testcaseInfo.description,
        expectedResult: testcaseInfo.expectedResult,
        testDataList: testcaseInfo.testDataList,
      });
      updateTestcase(scenarioId, {
        tcId: testcaseInfo.tcId,
      });
      setSuccess(SUCCESS_MESSAGES.TESTCASE.UPDATE_SUCCESS);
    } catch (error) {
      console.error(error);
      setError(ERROR_MESSAGES.TESTCASE.UPDATE_FAILED);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (onSuccess?: () => void) => {
    try {
      setIsLoading(true);
      await clientTestcaseApi.deleteTestcase(testcaseInfo.tcId);
      deleteTestcase(scenarioId, testcaseInfo.tcId);
      setSuccess(SUCCESS_MESSAGES.TESTCASE.DELETE_SUCCESS);
      onSuccess?.();
    } catch (error) {
      console.error(error);
      setError(ERROR_MESSAGES.TESTCASE.DELETE_FAILED);
    } finally {
      setIsLoading(false);
    }
  };

  return {
    testcaseInfo,
    isLoading,
    error,
    success,
    handlePreconditionChange,
    handleDescriptionChange,
    handleExpectedResultChange,
    handleTestDataListChange,
    handleCreate,
    handleUpdate,
    handleDelete,
    setTestcaseInfo,
  };
};
