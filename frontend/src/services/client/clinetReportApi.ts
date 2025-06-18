import {
  readScenarioReportResponse,
  readTestcaseReportResponse,
} from "@/types/report";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/report/v1`;

export const clientReportApi = {
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
