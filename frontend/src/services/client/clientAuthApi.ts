import { SuccessResponse, ErrorResponse } from "@/types/api";
import { handleApiResponse } from "@/utils/apiUtils";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/project/v1`;

export const clientAuthApi = {
  login: async (projectId: string): Promise<void> => {
    const res = await ky
      .post(`${BASE_URL}/`, {
        json: { projectId },
        credentials: "include",
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  logout: async (): Promise<void> => {
    const res = await ky
      .delete(`${BASE_URL}/`, {
        credentials: "include",
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  delete: async (projectId: string): Promise<void> => {
    const res = await ky
      .delete(`${BASE_URL}/${projectId}`, {
        credentials: "include",
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
};
