"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { useProjectStore } from "@/store/projectStore";

const Header = () => {
  const pathname = usePathname();
  const { project } = useProjectStore();

  const isScenarioPage = pathname.includes("/scenario/");

  return (
    <header className="w-full border-b border-slate-200 py-4 px-8 bg-white flex items-center justify-between">
      <h1 className="text-2xl font-bold text-slate-800">{project.id}</h1>
      <div className="flex gap-2">
        {isScenarioPage ? (
          <>
            <Link href="/login">
              <button className="bg-blue-500 text-white rounded-lg px-4 py-2 flex items-center gap-1">
                로그아웃
              </button>
            </Link>
            <Link href={`/project/${project.id}/upload`}>
              <button className="bg-violet-500 text-white rounded-lg px-4 py-2 flex items-center gap-1">
                파일 첨부
              </button>
            </Link>
            <Link href={`/project/${project.id}/scenario`}>
              <button className="bg-pink-500 text-white rounded-lg px-4 py-2 flex items-center gap-1">
                TC 일괄 생성
              </button>
            </Link>
            <Link href={`/project/${project.id}/test-run/scenario-1`}>
              <button className="bg-emerald-500 text-white rounded-lg px-4 py-2 flex items-center gap-1">
                테스트 실행
              </button>
            </Link>
          </>
        ) : (
          <Link href={`/project/${project.id}/scenario`}>
            <button className="bg-blue-500 text-white rounded-lg px-4 py-2 flex items-center gap-1">
              돌아가기
            </button>
          </Link>
        )}
      </div>
    </header>
  );
};

export default Header;
