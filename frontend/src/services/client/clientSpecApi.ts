import { uploadSpecRequest } from "@/types/spec";
import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/spec/v1`;

export const clientSpecApi = {
  upload: async (files: uploadSpecRequest): Promise<void> => {
    const formData = new FormData();
    formData.append("requirementFile", files.requirementFile);
    formData.append("interfaceDef", files.interfaceDef);
    formData.append("interfaceDesign", files.interfaceDesign);
    formData.append("databaseDesign", files.databaseDesign);
    await ky.post(`${BASE_URL}/`, {
      credentials: "include",
      body: formData,
    });
  },
  analyze: async (): Promise<void> => {
    await ky.post(`${BASE_URL}/analyze`, {
      credentials: "include",
    });
  },
};
