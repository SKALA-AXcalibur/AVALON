import ky from "ky";

const BASE_URL = process.env.NEXT_PUBLIC_API_URL;

const testcase = {
  generate: async () => {
    return await ky.post(`${BASE_URL}/api/tc/v1/`);
  },
};

export default testcase;
