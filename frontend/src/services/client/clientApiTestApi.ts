import { readApiTestResultResponse, runApiTestRequest } from "@/types/apiTest";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/test/v1`;

export const clientApiTestApi = {
  runApiTest: async (request: runApiTestRequest) => {
    await ky.post(`${BASE_URL}/run`, {
      credentials: "include",
      json: request,
    });
  },
  readApiTestResult: async (): Promise<readApiTestResultResponse> => {
    const response = await ky.get(`${BASE_URL}/result`, {
      credentials: "include",
    });
    return response.json();
  },
};
