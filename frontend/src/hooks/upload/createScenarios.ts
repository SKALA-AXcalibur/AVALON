import { useState } from "react";
import specApi from "@/services/spec";
import apiListApi from "@/services/apiList";
import scenarioApi from "@/services/scenario";
import { useProjectStore } from "@/store/projectStore";
import { uploadSpecRequest } from "@/types/spec";
import { UPLOAD_STEPS } from "@/constants/upload";

const useCreateScenarios = () => {
  const [step, setStep] = useState<number>(UPLOAD_STEPS.UPLOAD);
  const [isLoading, setIsLoading] = useState(false);
  const { project, setProject } = useProjectStore();

  const createScenarios = async (files: uploadSpecRequest) => {
    if (isLoading) return false;

    try {
      setIsLoading(true);

      switch (step) {
        case UPLOAD_STEPS.UPLOAD:
          await specApi.upload(files);
          setStep(UPLOAD_STEPS.ANALYZE);
          break;

        case UPLOAD_STEPS.ANALYZE:
          await specApi.analyze();
          setStep(UPLOAD_STEPS.CREATE_API_LIST);
          break;

        case UPLOAD_STEPS.CREATE_API_LIST:
          await apiListApi.create();
          setStep(UPLOAD_STEPS.CREATE_SCENARIOS);
          break;

        case UPLOAD_STEPS.CREATE_SCENARIOS:
          const scenarioResponse = await scenarioApi.create();
          setStep(UPLOAD_STEPS.COMPLETE);

          setProject({
            ...project,
            scenarios: scenarioResponse.scenarioList.map((s) => ({
              ...s,
              testcases: [],
            })),
          });
          return true;
      }

      return false;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return {
    createScenarios,
    step,
    setStep,
    isLoading,
  };
};

export default useCreateScenarios;
