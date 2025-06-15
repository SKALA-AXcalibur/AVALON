import ky from "ky";
import {
  readScenarioReportResponse,
  readTestcaseReportResponse,
} from "@/types/report";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/report/v1`;

export const apiReportApi = {
  readScenarioReport: async (): Promise<readScenarioReportResponse> => {
    const response = await ky.get(`${BASE_URL}/scenario`, {
      credentials: "include",
    });
    return response.json();
  },
  readTestcaseReport: async (
    scenarioId: string
  ): Promise<readTestcaseReportResponse> => {
    const response = await ky.get(`${BASE_URL}/testcase/${scenarioId}`, {
      credentials: "include",
    });
    return response.json();
  },
};
