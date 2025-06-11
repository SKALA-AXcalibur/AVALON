import { useState } from "react";
import specApi from "@/services/spec";
import apiListApi from "@/services/apiList";
import scenarioApi from "@/services/scenario";
import { useProjectStore } from "@/store/projectStore";
import { uploadSpecRequest } from "@/types/spec";

const useCreateScenarios = () => {
  const [step, setStep] = useState<number>(0);
  const [isLoading, setIsLoading] = useState(false);
  const { project, setProject } = useProjectStore();

  const createScenarios = async (files: uploadSpecRequest) => {
    if (isLoading) return false;

    try {
      setIsLoading(true);
      if (step === 0) {
        await specApi.upload(files);
        setStep(1);
      }

      if (step === 1) {
        await specApi.analyze();
        setStep(2);
      }

      if (step === 2) {
        await apiListApi.create();
        setStep(3);
      }

      if (step === 3) {
        const scenarioResponse = await scenarioApi.create();
        setStep(4);

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
