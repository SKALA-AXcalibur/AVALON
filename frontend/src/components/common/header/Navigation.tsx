"use client";
import useLogout from "@/hooks/auth/logout";
import ActionButton from "@/components/common/ActionButton";
import LinkButton from "@/components/common/LinkButton";
import { usePathname } from "next/navigation";

const Navigation = ({ projectId }: { projectId: string }) => {
  const pathname = usePathname();
  const isScenarioPage = pathname.includes("/scenario/");
  const { logout, isLoading } = useLogout();

  return (
    <nav className="flex flex-wrap gap-2">
      {isScenarioPage ? (
        <>
          <ActionButton
            onClick={logout}
            color="bg-blue-500 hover:bg-blue-600"
            isLoading={isLoading}
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
          <LinkButton
            href={`/project/${projectId}/scenario`}
            color="bg-pink-500 hover:bg-pink-600"
            ariaLabel="TC 일괄 생성"
          >
            TC 일괄 생성
          </LinkButton>
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
