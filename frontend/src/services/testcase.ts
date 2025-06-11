import { readScenarioTestcasesResponse } from "@/types/testcase";
import ky from "ky";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/tc/v1`;

const testcaseApi = {
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
    const response = await ky.get(`${BASE_URL}/${scenarioId}`, {
      credentials: "include",
      searchParams: {
        offset,
        query,
      },
    });
    return response.json();
  },
};

export default testcaseApi;
