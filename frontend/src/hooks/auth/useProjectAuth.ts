import { useRef, useState } from "react";
import { validateId } from "@/utils/validateId";
import { clientAuthApi } from "@/services/client/clientAuthApi";
import { ERROR_MESSAGES, SUCCESS_MESSAGES } from "@/constants/messages";

export const useProjectAuth = () => {
  const [projectId, setProjectId] = useState("");
  const projectIdRef = useRef("");
  const [isLoading, setIsLoading] = useState(false);
  const [isConfirming, setIsConfirming] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState<string | null>(null);

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

    setIsConfirming(false);
  };

  const handleLogin = async (onSuccess?: () => void) => {
    try {
      setIsLoading(true);

      await clientAuthApi.login(projectId);

      onSuccess?.();
    } catch (error) {
      console.error("Login failed:", error);
      setError(ERROR_MESSAGES.PROJECT_AUTH.LOGIN_FAILED);
    } finally {
      setIsLoading(false);
      setErrorMessageTimeout();
    }
  };

  const handleDelete = async () => {
    if (!isConfirming) {
      setIsConfirming(true);
      return;
    }

    try {
      setIsLoading(true);

      await clientAuthApi.delete(projectId);

      setSuccess(SUCCESS_MESSAGES.PROJECT_AUTH.DELETE_SUCCESS);
    } catch (error) {
      console.error("Delete failed:", error);
      setError(ERROR_MESSAGES.PROJECT_AUTH.DELETE_FAILED);
    } finally {
      setIsLoading(false);
      setIsConfirming(false);
      setErrorMessageTimeout();
    }
  };

  return {
    projectId,
    isLoading,
    isConfirming,
    error,
    success,
    handleProjectIdChange,
    handleLogin,
    handleDelete,
  };
};
