import ky from "ky";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

const auth = {
  logout: async () => {
    return await ky.delete(`${BASE_URL}/api/project/v1/`, {
      credentials: "include",
    });
  },
};

export default auth;
