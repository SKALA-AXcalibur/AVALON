import { clientAuthApi } from "@/services/client/clientAuthApi";
import { useState } from "react";
import { useRouter } from "next/navigation";

const useLogout = (redirectUrl: string = "/login") => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);

  const logout = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await clientAuthApi.logout();
      router.push(redirectUrl);
    } catch (error) {
      console.error(error);
    } finally {
      setIsLoading(false);
    }
  };

  return { logout, isLoading };
};

export default useLogout;
