import { useState, useEffect, useCallback } from "react";
import { useProjectStore } from "@/store/projectStore";
import { clientTestcaseApi } from "@/services/client/clientTestcaseApi";
import { ERROR_MESSAGES, SUCCESS_MESSAGES } from "@/constants/messages";
import { TestcaseInfo, Param, api } from "@/interfaces/testcase";

export const useTestcase = (
  projectId: string,
  scenarioId: string,
  testcaseId: string
) => {
  const { addTestcase, updateTestcase, deleteTestcase } = useProjectStore();

  const [testcaseInfo, setTestcaseInfo] = useState<TestcaseInfo>({
    tcId: testcaseId,
    precondition: "",
    description: "",
    expectedResult: "",
    status: 0,
    testDataList: [],
  });

  const [apiList, setApiList] = useState<api[]>([]);
  const [selectedApiId, setSelectedApiId] = useState<string>("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

  const fetchApiList = useCallback(async () => {
    try {
      setIsLoading(true);
      const response = await clientTestcaseApi.readApiList(scenarioId);
      setApiList(response.apiList);
    } catch (error) {
      console.error(error);
      setError("API 목록을 가져오는데 실패했습니다.");
    } finally {
      setIsLoading(false);
    }
  }, [scenarioId]);

  const fetchTestcaseInfo = useCallback(async () => {
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
  }, [testcaseId]);

  useEffect(() => {
    if (testcaseId === "new") {
      fetchApiList();
    } else {
      fetchTestcaseInfo();
    }
  }, [testcaseId, fetchApiList, fetchTestcaseInfo]);

  useEffect(() => {
    if (error || success) {
      const timer = setTimeout(() => {
        setError(null);
        setSuccess(null);
      }, 2000);
      return () => clearTimeout(timer);
    }
  }, [error, success]);

  const handlePreconditionChange = (value: string) => {
    setTestcaseInfo((prev) => ({ ...prev, precondition: value }));
  };

  const handleDescriptionChange = (value: string) => {
    setTestcaseInfo((prev) => ({ ...prev, description: value }));
  };

  const handleExpectedResultChange = (value: string) => {
    setTestcaseInfo((prev) => ({ ...prev, expectedResult: value }));
  };

  const handleTestDataListChange = (testDataList: Param[]) => {
    setTestcaseInfo((prev) => ({ ...prev, testDataList }));
  };

  const handleApiChange = async (apiId: string) => {
    setSelectedApiId(apiId);

    if (apiId) {
      try {
        setIsLoading(true);
        const response = await clientTestcaseApi.readParams(scenarioId, apiId);
        setTestcaseInfo((prev) => ({
          ...prev,
          testDataList: response.testDataList.map((param) => ({
            ...param,
            value: null,
          })),
        }));
      } catch (error) {
        console.error(error);
        setError("파라미터를 가져오는데 실패했습니다.");
      } finally {
        setIsLoading(false);
      }
    } else {
      setTestcaseInfo((prev) => ({ ...prev, testDataList: [] }));
    }
  };

  const handleCreate = async (onSuccess?: (testcaseId: string) => void) => {
    try {
      setIsLoading(true);
      const response = await clientTestcaseApi.createTestcase(
        scenarioId,
        selectedApiId,
        {
          precondition: testcaseInfo.precondition,
          description: testcaseInfo.description,
          expectedResult: testcaseInfo.expectedResult,
          status: testcaseInfo.status,
          testDataList: testcaseInfo.testDataList,
        }
      );
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
        status: testcaseInfo.status,
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
    apiList,
    selectedApiId,
    isLoading,
    error,
    success,
    handlePreconditionChange,
    handleDescriptionChange,
    handleExpectedResultChange,
    handleTestDataListChange,
    handleApiChange,
    handleCreate,
    handleUpdate,
    handleDelete,
    setTestcaseInfo,
  };
};
