import {
  createScenarioRequest,
  readScenarioResponse,
  updateScenarioRequest,
  readProjectScenariosResponse,
  createScenarioResponse,
} from "@/types/scenario";
import ky from "ky";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/scenario/v1`;

const scenarioApi = {
  createScenario: async (
    scenario: createScenarioRequest
  ): Promise<createScenarioResponse> => {
    const response = await ky.post(`${BASE_URL}/`, {
      credentials: "include",
      json: scenario,
    });

    return response.json();
  },
  readScenario: async (scenarioId: string): Promise<readScenarioResponse> => {
    const response = await ky.get(`${BASE_URL}/scenario/${scenarioId}`, {
      credentials: "include",
    });

    return response.json();
  },
  updateScenario: async (
    scenarioId: string,
    scenario: updateScenarioRequest
  ): Promise<void> => {
    await ky.put(`${BASE_URL}/${scenarioId}`, {
      credentials: "include",
      json: scenario,
    });
  },
  deleteScenario: async (scenarioId: string): Promise<void> => {
    await ky.delete(`${BASE_URL}/scenario/${scenarioId}`, {
      credentials: "include",
    });
  },
  readProjectScenarios: async (
    offset: number = 0,
    query: number = 10
  ): Promise<readProjectScenariosResponse> => {
    const response = await ky.get(`${BASE_URL}/project`, {
      credentials: "include",
      searchParams: {
        offset,
        query,
      },
    });

    return response.json();
  },
};

export default scenarioApi;
