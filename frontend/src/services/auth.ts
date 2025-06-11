import ky from "ky";

const BASE_URL = `${process.env.NEXT_PUBLIC_API_URL}/project/v1`;

const auth = {
  logout: async () => {
    return await ky.delete(`${BASE_URL}/`, {
      credentials: "include",
    });
  },
};

export default auth;
