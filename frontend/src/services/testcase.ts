import ky from "ky";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

const testcase = {
  generate: async (): Promise<void> => {
    await ky.post(`${BASE_URL}/api/tc/v1/testcase`, {
      credentials: "include",
    });
  },
};

export default testcase;
