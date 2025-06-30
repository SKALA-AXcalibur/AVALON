import {
  createTestcaseRequest,
  createTestcaseResponse,
  readApiListResponse,
  readParamsResponse,
  readScenarioTestcasesResponse,
  readTestcaseResponse,
  updateTestcaseRequest,
} from "@/types/testcase";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/tc/v1`;

export const clientTestcaseApi = {
  generate: async (): Promise<void> => {
    await ky.post(`${BASE_URL}/`, {
      credentials: "include",
    });
  },
  createTestcase: async (
    scenarioId: string,
    apiId: string,
    testcase: createTestcaseRequest
  ): Promise<createTestcaseResponse> => {
    const response = await ky.post(`${BASE_URL}/${scenarioId}/${apiId}`, {
      credentials: "include",
      json: testcase,
    });
    return response.json();
  },
  readTestcase: async (testcaseId: string): Promise<readTestcaseResponse> => {
    const response = await ky.get(`${BASE_URL}/${testcaseId}`, {
      credentials: "include",
    });
    return response.json();
  },
  updateTestcase: async (
    testcaseId: string,
    testcase: updateTestcaseRequest
  ): Promise<void> => {
    await ky.put(`${BASE_URL}/${testcaseId}`, {
      credentials: "include",
      json: testcase,
    });
  },
  deleteTestcase: async (testcaseId: string): Promise<void> => {
    await ky.delete(`${BASE_URL}/${testcaseId}`, {
      credentials: "include",
    });
  },
  readApiList: async (scenarioId: string): Promise<readApiListResponse> => {
    const response = await ky.get(`${BASE_URL}/api/${scenarioId}`, {
      credentials: "include",
    });
    return response.json();
  },
  readParams: async (
    scenarioId: string,
    apiId: string
  ): Promise<readParamsResponse> => {
    const response = await ky.get(`${BASE_URL}/api/${scenarioId}/${apiId}`, {
      credentials: "include",
    });
    return response.json();
  },
  readScenarioTestcases: async (
    scenarioId: string,
    offset: number = 0,
    query: number = 10
  ): Promise<readScenarioTestcasesResponse> => {
    const response = await ky.get(`${BASE_URL}/scenario/${scenarioId}`, {
      credentials: "include",
      searchParams: {
        offset,
        query,
      },
    });
    return response.json();
  },
};
