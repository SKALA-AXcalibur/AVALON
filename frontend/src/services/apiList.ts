import ky from "ky";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/list/v1`;

const apiListApi = {
  create: async (): Promise<void> => {
    await ky.post(`${BASE_URL}/`, { credentials: "include" });
  },
};

export default apiListApi;
