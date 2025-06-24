import ky from "ky-universal";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/list/v1`;

export const clientApiListApi = {
  create: async (): Promise<void> => {
    await ky.post(`${BASE_URL}/generate/`, { credentials: "include" });
  },
};
