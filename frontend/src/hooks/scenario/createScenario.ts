import scenarioApi from "@/services/scenario";
import { useParams, useRouter } from "next/navigation";
import { useState } from "react";

const useCreateScenario = (
  name: string,
  description: string,
  validation: string
) => {
  const params = useParams();
  const projectId = params["project-id"];
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const createScenario = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      const response = await scenarioApi.createScenario({
        name,
        description,
        validation,
      });
      router.push(`/project/${projectId}/scenario/${response.id}`);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { createScenario, isLoading };
};

export default useCreateScenario;
