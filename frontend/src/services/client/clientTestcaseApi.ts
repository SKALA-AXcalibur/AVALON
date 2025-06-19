import {
  createTestcaseRequest,
  createTestcaseResponse,
  readScenarioTestcasesResponse,
  readTestcaseResponse,
  updateTestcaseRequest,
} from "@/types/testcase";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/tc/v1`;

export const clientTestcaseApi = {
  createTestcase: async (
    scenarioId: string,
    testcase: createTestcaseRequest
  ): Promise<createTestcaseResponse> => {
    const response = await ky.post(`${BASE_URL}/${scenarioId}`, {
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
  generate: async (): Promise<void> => {
    await ky.post(`${BASE_URL}/testcase`, {
      credentials: "include",
    });
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
