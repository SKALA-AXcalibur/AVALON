import ky from "ky";
import { uploadSpecRequest } from "@/types/spec";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/spec/v1`;

const specApi = {
  upload: async (files: uploadSpecRequest): Promise<void> => {
    await ky.post(`${BASE_URL}/`, {
      credentials: "include",
      json: files,
    });
  },
  analyze: async (): Promise<void> => {
    await ky.post(`${BASE_URL}/analyze`, {
      credentials: "include",
    });
  },
};

export default specApi;
