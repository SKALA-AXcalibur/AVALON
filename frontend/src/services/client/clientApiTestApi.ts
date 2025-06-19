import {
  readApiTestResultResponse,
  readApiTestScenarioResultResponse,
  runApiTestRequest,
} from "@/types/apiTest";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/test/v1`;

export const clientApiTestApi = {
  runApiTest: async (request: runApiTestRequest) => {
    await ky.post(`${BASE_URL}/run`, {
      credentials: "include",
      json: request,
    });
  },
  readApiTestResult: async (
    cursor: number = 0,
    size: number = 10
  ): Promise<readApiTestResultResponse> => {
    const response = await ky.get(`${BASE_URL}/result`, {
      credentials: "include",
      searchParams: { cursor, size },
    });
    return response.json();
  },
  readApiTestScenarioResult: async (
    scenarioId: string,
    cursor: number = 0,
    size: number = 10
  ): Promise<readApiTestScenarioResultResponse> => {
    const response = await ky.get(`${BASE_URL}/result/${scenarioId}`, {
      credentials: "include",
      searchParams: { cursor, size },
    });
    return response.json();
  },
};
