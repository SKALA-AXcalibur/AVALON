import ScenarioResponse from "@/types/scenario";
import ky from "ky";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

const scenario = {
  getProjectScenarios: async (
    offset: number = 0,
    query: number = 10
  ): Promise<ScenarioResponse> => {
    const response = await ky.get(`${BASE_URL}/api/scenario/v1/project`, {
      credentials: "include",
      searchParams: {
        offset,
        query,
      },
    });

    return response.json();
  },
};

export default scenario;
