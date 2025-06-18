import { clientReportApi } from "@/services/client/clinetReportApi";
import { useState } from "react";

export const useReadScenarioReport = () => {
  const [isLoading, setIsLoading] = useState(false);

  const readScenarioReport = async () => {
    if (isLoading) return false;

    setIsLoading(true);
    try {
      const response = await clientReportApi.readScenarioReport();
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
