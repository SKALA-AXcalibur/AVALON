"use client";
import ActionButton from "@/components/common/ActionButton";
import LinkButton from "@/components/common/LinkButton";
import useLogout from "@/hooks/auth/logout";
import useGenerateTestcases from "@/hooks/testcase/generateTestcases";
import { usePathname } from "next/navigation";

const Navigation = ({ projectId }: { projectId: string }) => {
  const pathname = usePathname();
  const isScenarioPage = pathname.includes("/scenario/");
  const { logout, isLoading: isLoggingOut } = useLogout();
  const { generateTestcases, isLoading: isGeneratingTestcases } =
    useGenerateTestcases();

  return (
    <nav className="flex flex-wrap gap-2">
      {isScenarioPage ? (
        <>
          <ActionButton
            onClick={logout}
            color="bg-blue-500 hover:bg-blue-600"
            isLoading={isLoggingOut}
          >
            로그아웃
          </ActionButton>
          <LinkButton
            href={`/project/${projectId}/upload`}
            color="bg-violet-500 hover:bg-violet-600"
            ariaLabel="파일 첨부하기"
          >
            파일 첨부
          </LinkButton>
          <ActionButton
            onClick={generateTestcases}
            color="bg-pink-500 hover:bg-pink-600"
            isLoading={isGeneratingTestcases}
          >
            TC 일괄 생성
          </ActionButton>
          <LinkButton
            href={`/project/${projectId}/test-run/scenario-1`}
            color="bg-emerald-500 hover:bg-emerald-600"
            ariaLabel="테스트 실행하기"
          >
            테스트 실행
          </LinkButton>
        </>
      ) : (
        <LinkButton
          href={`/project/${projectId}/scenario`}
          color="bg-blue-500 hover:bg-blue-600"
          ariaLabel="돌아가기"
        >
          돌아가기
        </LinkButton>
      )}
    </nav>
  );
};

export default Navigation;
