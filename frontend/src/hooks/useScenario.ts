import { useState, useEffect } from "react";
import { useProjectStore } from "@/store/projectStore";
import { clientScenarioApi } from "@/services/client/clientScenarioApi";
import { ERROR_MESSAGES, SUCCESS_MESSAGES } from "@/constants/messages";
import { ScenarioInfo } from "@/interfaces/scenario";
import { validateId } from "@/utils/validateId";

export const useScenario = (projectId: string, scenarioId: string) => {
  const { updateScenario, deleteScenario: removeScenario } = useProjectStore();

  const [scenarioInfo, setScenarioInfo] = useState<ScenarioInfo>({
    id: scenarioId,
    name: "",
    graph: "",
    description: "",
    validation: "",
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
    if (scenarioId !== "new") {
      const fetchScenarioInfo = async () => {
        try {
          setIsLoading(true);
          const scenario = await clientScenarioApi.readScenario(scenarioId);
          setScenarioInfo(scenario);
        } catch (error) {
          console.error(error);
          setError(ERROR_MESSAGES.SCENARIO.READ_FAILED);
        } finally {
          setIsLoading(false);
        }
      };
      fetchScenarioInfo();
    }
  }, [scenarioId]);

  const handleNameChange = (value: string) => {
    const validation = validateId(value);
    setError(validation.isValid ? null : validation.errorMessage || null);
    setScenarioInfo((prev) => ({ ...prev, name: value }));
  };
  const handleDescriptionChange = (value: string) => {
    setScenarioInfo((prev) => ({ ...prev, description: value }));
  };
  const handleValidationChange = (value: string) => {
    setScenarioInfo((prev) => ({ ...prev, validation: value }));
  };

  const handleCreate = async (onSuccess?: () => void) => {
    try {
      setIsLoading(true);
      await clientScenarioApi.createScenario({
        name: scenarioInfo.name,
        description: scenarioInfo.description,
        validation: scenarioInfo.validation,
      });
      setSuccess(SUCCESS_MESSAGES.SCENARIO.CREATE_SUCCESS);
      resetScenario();
      onSuccess?.();
    } catch (error) {
      console.error(error);
      setError(ERROR_MESSAGES.SCENARIO.CREATE_FAILED);
    } finally {
      setIsLoading(false);
    }
  };

  const handleUpdate = async (onSuccess?: () => void) => {
    try {
      setIsLoading(true);
      await clientScenarioApi.updateScenario(scenarioInfo.id, {
        name: scenarioInfo.name,
        description: scenarioInfo.description,
        validation: scenarioInfo.validation,
      });
      updateScenario({
        id: scenarioInfo.id,
        name: scenarioInfo.name,
        testcases: [],
      });
      setSuccess(SUCCESS_MESSAGES.SCENARIO.UPDATE_SUCCESS);
      onSuccess?.();
    } catch (error) {
      console.error(error);
      setError(ERROR_MESSAGES.SCENARIO.UPDATE_FAILED);
    } finally {
      setIsLoading(false);
    }
  };

  const handleDelete = async (
    onSuccess?: (scenarioId: string | null, total: number) => void
  ) => {
    try {
      setIsLoading(true);
      await clientScenarioApi.deleteScenario(scenarioInfo.id);
      removeScenario(scenarioInfo.id);
      setSuccess(SUCCESS_MESSAGES.SCENARIO.DELETE_SUCCESS);
      const scenariosAfterDeletion =
        useProjectStore.getState().project.scenarios;
      const total = scenariosAfterDeletion.length;
      onSuccess?.(total > 0 ? scenariosAfterDeletion[0].id : null, total);
    } catch (error) {
      console.error(error);
      setError(ERROR_MESSAGES.SCENARIO.DELETE_FAILED);
    } finally {
      setIsLoading(false);
    }
  };

  const resetScenario = () => {
    setScenarioInfo({
      id: scenarioId,
      name: "",
      graph: "",
      description: "",
      validation: "",
    });
  };

  return {
    scenarioInfo,
    isLoading,
    error,
    success,
    handleNameChange,
    handleDescriptionChange,
    handleValidationChange,
    handleCreate,
    handleUpdate,
    handleDelete,
    setScenarioInfo,
  };
};
