import { clientAuthApi } from "@/services/client/clientAuthApi";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { useProjectStore } from "@/store/projectStore";
import { useSidebarStore } from "@/store/sidebarStore";

const useLogout = (redirectUrl: string = "/login") => {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const { resetProject } = useProjectStore();
  const { resetSidebar } = useSidebarStore();

  const logout = async () => {
    if (isLoading) return;

    setIsLoading(true);
    try {
      await clientAuthApi.logout();
      resetProject();
      resetSidebar();
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
