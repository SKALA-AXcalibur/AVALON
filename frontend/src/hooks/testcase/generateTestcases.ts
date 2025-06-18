import { clientTestcaseApi } from "@/services/client/clientTestcaseApi";
import { useState } from "react";
import { useRouter } from "next/navigation";

const useGenerateTestcases = (redirectUrl: string = "/scenario") => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const generateTestcases = async () => {
    setIsLoading(true);
    try {
      await clientTestcaseApi.generate();
      router.push(redirectUrl);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { generateTestcases, isLoading };
};

export default useGenerateTestcases;
