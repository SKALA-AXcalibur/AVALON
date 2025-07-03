import {
  readApiTestResultResponse,
  readApiTestScenarioResultResponse,
  runApiTestRequest,
} from "@/types/apiTest";
import { SuccessResponse, ErrorResponse } from "@/types/api";
import { handleApiResponse } from "@/utils/apiUtils";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/test/v1`;

export const clientApiTestApi = {
  runApiTest: async (request: runApiTestRequest): Promise<void> => {
    const res = await ky
      .post(`${BASE_URL}/run`, {
        credentials: "include",
        json: request,
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
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

    const res = await ky
      .get(`${BASE_URL}/result`, {
        credentials: "include",
        searchParams,
      })
      .json<SuccessResponse<readApiTestResultResponse> | ErrorResponse>();

    return handleApiResponse(res);
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

    const res = await ky
      .get(`${BASE_URL}/result/${scenarioId}`, {
        credentials: "include",
        searchParams,
      })
      .json<
        SuccessResponse<readApiTestScenarioResultResponse> | ErrorResponse
      >();

    return handleApiResponse(res);
  },
};
