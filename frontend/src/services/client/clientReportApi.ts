import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/report/v1`;

export const clientReportApi = {
  readScenarioReport: async (): Promise<Blob> => {
    const res = await ky
      .get(`${BASE_URL}/scenario`, {
        credentials: "include",
      })
      .blob();

    return res;
  },
  readTestcaseReport: async (scenarioId: string): Promise<Blob> => {
    const res = await ky
      .get(`${BASE_URL}/testcase/${scenarioId}`, {
        credentials: "include",
      })
      .blob();

    return res;
  },
};
