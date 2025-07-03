import {
  createScenarioRequest,
  readScenarioResponse,
  updateScenarioRequest,
  readProjectScenariosResponse,
  createScenarioResponse,
} from "@/types/scenario";
import { SuccessResponse, ErrorResponse } from "@/types/api";
import { handleApiResponse } from "@/utils/apiUtils";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/scenario/v1`;

export const clientScenarioApi = {
  create: async (): Promise<readProjectScenariosResponse> => {
    const res = await ky
      .post(`${BASE_URL}/create`, {
        credentials: "include",
      })
      .json<SuccessResponse<readProjectScenariosResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
  createScenario: async (
    scenario: createScenarioRequest
  ): Promise<createScenarioResponse> => {
    const res = await ky
      .post(`${BASE_URL}/`, {
        credentials: "include",
        json: scenario,
      })
      .json<SuccessResponse<createScenarioResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
  readScenario: async (scenarioId: string): Promise<readScenarioResponse> => {
    const res = await ky
      .get(`${BASE_URL}/scenario/${scenarioId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<readScenarioResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
  updateScenario: async (
    scenarioId: string,
    scenario: updateScenarioRequest
  ): Promise<void> => {
    const res = await ky
      .put(`${BASE_URL}/${scenarioId}`, {
        credentials: "include",
        json: scenario,
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  deleteScenario: async (scenarioId: string): Promise<void> => {
    const res = await ky
      .delete(`${BASE_URL}/scenario/${scenarioId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  readProjectScenarios: async (
    offset: number = 0,
    query: number = 10
  ): Promise<readProjectScenariosResponse> => {
    const res = await ky
      .get(`${BASE_URL}/project`, {
        credentials: "include",
        searchParams: {
          offset,
          query,
        },
      })
      .json<SuccessResponse<readProjectScenariosResponse> | ErrorResponse>();

    return handleApiResponse(res);
  },
};
