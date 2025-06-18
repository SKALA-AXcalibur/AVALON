import { readProjectScenariosResponse } from "@/types/scenario";
import ky from "ky-universal";
import { cookies } from "next/headers";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/scenario/v1`;

export const serverScenarioApi = {
  readProjectScenarios: async (
    offset: number = 0,
    query: number = 10
  ): Promise<readProjectScenariosResponse> => {
    const cookieStore = await cookies();
    const avalon = cookieStore.get("avalon")?.value;

    const headers: Record<string, string> = {};
    if (avalon) {
      headers.Cookie = `avalon=${avalon}`;
    }

    const response = await ky.get(`${BASE_URL}/project`, {
      searchParams: {
        offset,
        query,
      },
      headers,
    });

    return response.json();
  },
};
