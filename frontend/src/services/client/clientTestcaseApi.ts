import {
  createTestcaseRequest,
  createTestcaseResponse,
  readApiListResponse,
  readParamsResponse,
  readScenarioTestcasesResponse,
  readTestcaseResponse,
  updateTestcaseRequest,
} from "@/types/testcase";
import { SuccessResponse, ErrorResponse } from "@/types/api";
import { handleApiResponse } from "@/utils/apiUtils";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/tc/v1`;

export const clientTestcaseApi = {
  generate: async (): Promise<void> => {
    const res = await ky
      .post(`${BASE_URL}/`, {
        credentials: "include",
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  createTestcase: async (
    scenarioId: string,
    apiId: string,
    testcase: createTestcaseRequest
  ): Promise<createTestcaseResponse> => {
    const res = await ky
      .post(`${BASE_URL}/${scenarioId}/${apiId}`, {
        credentials: "include",
        json: testcase,
      })
      .json<SuccessResponse<createTestcaseResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
  readTestcase: async (testcaseId: string): Promise<readTestcaseResponse> => {
    const res = await ky
      .get(`${BASE_URL}/${testcaseId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<readTestcaseResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
  updateTestcase: async (
    testcaseId: string,
    testcase: updateTestcaseRequest
  ): Promise<void> => {
    const res = await ky
      .put(`${BASE_URL}/${testcaseId}`, {
        credentials: "include",
        json: testcase,
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  deleteTestcase: async (testcaseId: string): Promise<void> => {
    const res = await ky
      .delete(`${BASE_URL}/${testcaseId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  readApiList: async (scenarioId: string): Promise<readApiListResponse> => {
    const res = await ky
      .get(`${BASE_URL}/api/${scenarioId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<readApiListResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
  readParams: async (
    scenarioId: string,
    apiId: string
  ): Promise<readParamsResponse> => {
    const res = await ky
      .get(`${BASE_URL}/api/${scenarioId}/${apiId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<readParamsResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
  readScenarioTestcases: async (
    scenarioId: string
  ): Promise<readScenarioTestcasesResponse> => {
    const res = await ky
      .get(`${BASE_URL}/scenario/${scenarioId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<readScenarioTestcasesResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
};
