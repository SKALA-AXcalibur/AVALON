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
    cursor?: string,
    size?: number
  ): Promise<readApiTestResultResponse> => {
    const searchParams: Record<string, string | number> = {};

    if (cursor !== undefined) {
      searchParams.cursor = cursor;
    }
    if (size !== undefined) {
      searchParams.size = size;
    }

    const response = await ky.get(`${BASE_URL}/result`, {
      credentials: "include",
      searchParams,
    });
    return response.json();
  },
  readApiTestScenarioResult: async (
    scenarioId: string,
    cursor?: string,
    size?: number
  ): Promise<readApiTestScenarioResultResponse> => {
    const searchParams: Record<string, string | number> = {};

    if (cursor !== undefined) {
      searchParams.cursor = cursor;
    }
    if (size !== undefined) {
      searchParams.size = size;
    }

    const response = await ky.get(`${BASE_URL}/result/${scenarioId}`, {
      credentials: "include",
      searchParams,
    });
    return response.json();
  },
};
