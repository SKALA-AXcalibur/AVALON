import { apiReportApi } from "@/services/report";
import { useState } from "react";

export const useReadScenarioReport = () => {
  const [isLoading, setIsLoading] = useState(false);

  const readScenarioReport = async () => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await apiReportApi.readScenarioReport();
      return response;
    } catch (error) {
      console.error(error);
      return false;
    } finally {
      setIsLoading(false);
    }
  };

  return { readScenarioReport, isLoading };
};
