import ky from "ky";
import { uploadSpecRequest } from "@/types/spec";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/spec/v1`;

const specApi = {
  upload: async (files: uploadSpecRequest): Promise<void> => {
    const formData = new FormData();
    formData.append("requirementFile", files.requirementFile);
    formData.append("interfaceDef", files.interfaceDef);
    formData.append("interfaceDesign", files.interfaceDesign);
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

export default specApi;
