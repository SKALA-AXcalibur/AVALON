import { useRef, useState } from "react";
import { validateId } from "@/utils/validateId";
import { clientAuthApi } from "@/services/client/clientAuthApi";
import { ERROR_MESSAGES, SUCCESS_MESSAGES } from "@/constants/messages";
import { clientScenarioApi } from "@/services/client/clientScenarioApi";
import { useProjectStore } from "@/store/projectStore";

export const useProjectAuth = () => {
  const [projectId, setProjectId] = useState("");
  const projectIdRef = useRef("");
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);
  const { setProject } = useProjectStore();

  const setErrorMessageTimeout = () => {
    setTimeout(() => {
      setSuccess(null);
      if (projectIdRef.current === projectId) {
        setError(null);
      }
    }, 2000);
  };

  const handleProjectIdChange = (value: string) => {
    setProjectId(value);
    projectIdRef.current = value;

    const validation = validateId(value);
    setError(validation.isValid ? null : validation.errorMessage || null);
  };

  const handleLogin = async (
    onSuccess?: (scenarioId: string | null, total: number) => void
  ) => {
    try {
      setIsLoading(true);
      await clientAuthApi.login(projectId);
      const response = await clientScenarioApi.readProjectScenarios();
      setProject({
        id: projectId,
        scenarios: response.scenarioList.map((scenario) => ({
          id: scenario.id,
          name: scenario.name,
          testcases: [],
        })),
      });
      onSuccess?.(
        response.total > 0 ? response.scenarioList[0].id : null,
        response.total
      );
    } catch (error) {
      console.error("Login failed:", error);
      setError(ERROR_MESSAGES.PROJECT_AUTH.LOGIN_FAILED);
    } finally {
      setIsLoading(false);
      setErrorMessageTimeout();
    }
  };

  const handleDelete = async () => {
    try {
      setIsLoading(true);
      await clientAuthApi.delete(projectId);
      setSuccess(SUCCESS_MESSAGES.PROJECT_AUTH.DELETE_SUCCESS);
    } catch (error) {
      console.error("Delete failed:", error);
      setError(ERROR_MESSAGES.PROJECT_AUTH.DELETE_FAILED);
    } finally {
      setIsLoading(false);
      setErrorMessageTimeout();
    }
  };

  return {
    projectId,
    isLoading,
    error,
    success,
    handleProjectIdChange,
    handleLogin,
    handleDelete,
  };
};
