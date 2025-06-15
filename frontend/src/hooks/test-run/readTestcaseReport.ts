import { apiReportApi } from "@/services/report";
import { useState } from "react";

export const useReadTestcaseReport = () => {
  const [isLoading, setIsLoading] = useState(false);

  const readTestcaseReport = async (scenarioId: string) => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await apiReportApi.readTestcaseReport(scenarioId);
      return response;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { readTestcaseReport, isLoading };
};
