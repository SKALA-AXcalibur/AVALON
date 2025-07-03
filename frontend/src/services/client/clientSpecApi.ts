import { uploadSpecRequest } from "@/types/spec";
import { SuccessResponse, ErrorResponse } from "@/types/api";
import { handleApiResponse } from "@/utils/apiUtils";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/spec/v1`;

export const clientSpecApi = {
  upload: async (files: uploadSpecRequest): Promise<void> => {
    const formData = new FormData();
    formData.append("requirementFile", files.requirementFile);
    formData.append("interfaceDef", files.interfaceDef);
    formData.append("interfaceDesign", files.interfaceDesign);
    formData.append("databaseDesign", files.databaseDesign);
    const res = await ky
      .post(`${BASE_URL}/`, {
        credentials: "include",
        body: formData,
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
  analyze: async (): Promise<void> => {
    const res = await ky
      .post(`${BASE_URL}/analyze`, {
        credentials: "include",
      })
      .json<SuccessResponse<null> | ErrorResponse>();

    handleApiResponse(res);
  },
};
