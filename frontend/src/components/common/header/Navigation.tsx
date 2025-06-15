"use client";
import ActionButton from "@/components/common/ActionButton";
import LinkButton from "@/components/common/LinkButton";
import useLogout from "@/hooks/auth/logout";
import { useRunApiTest } from "@/hooks/test-run/runApiTest";
import useGenerateTestcases from "@/hooks/testcase/generateTestcases";
import { useParams, usePathname, useRouter } from "next/navigation";

const Navigation = () => {
  const { "project-id": projectId, "scenario-id": scenarioId } = useParams();
  const router = useRouter();
  const pathname = usePathname();
  const { logout, isLoading: isLoggingOut } = useLogout();
  const { generateTestcases, isLoading: isGeneratingTestcases } =
    useGenerateTestcases();
  const { runApiTest, isLoading: isRunningApiTest } = useRunApiTest();

  const handleRunApiTest = async () => {
    const result = await runApiTest();
    if (result) {
      router.push(`/project/${projectId}/test-run/${scenarioId}`);
    }
  };

  return (
    <nav className="flex flex-wrap gap-2">
      {pathname.includes("/scenario/") ? (
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
          <ActionButton
            onClick={handleRunApiTest}
            color="bg-emerald-500 hover:bg-emerald-600"
            isLoading={isRunningApiTest}
          >
            테스트 실행
          </ActionButton>
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
