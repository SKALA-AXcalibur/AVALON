import ky from "ky";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/project/v1`;

export const auth = {
  login: async (projectId: string): Promise<void> => {
    await ky.post(`${BASE_URL}/`, {
      json: { projectId },
      credentials: "include",
    });
  },
  logout: async (): Promise<void> => {
    await ky.delete(`${BASE_URL}/`, {
      credentials: "include",
    });
  },
  delete: async (projectId: string): Promise<void> => {
    await ky.delete(`${BASE_URL}/${projectId}`, {
      credentials: "include",
    });
  },
};
