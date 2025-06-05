"use client";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { useProjectStore } from "@/store/projectStore";

const Login = () => {
  const router = useRouter();
  const [projectId, setProjectId] = useState("");
  const setProject = useProjectStore((state) => state.setProject);

  const handleLogin = () => {
    if (projectId === "") {
      alert("[알림] 프로젝트 ID를 입력해주세요.");
      return;
    }

    const isValid = /^[a-zA-Z0-9]+$/.test(projectId);

    if (!isValid) {
      alert("[알림] 프로젝트 ID는 영문자와 숫자만 입력할 수 있습니다.");
      return;
    }

    setProject({ id: projectId, scenarioIds: [] });
    router.push(`/project/${projectId}/scenario`);
  };

  return (
    <div className="bg-white shadow-lg rounded-xl p-8 w-[512px] flex flex-col gap-4">
      <div className="flex gap-2">
        <input
          className="flex-1 border border-gray-300 rounded-lg px-4 py-3 text-lg"
          placeholder="project id"
          value={projectId}
          onChange={(e) => setProjectId(e.target.value)}
          onKeyDown={(e) => e.key === "Enter" && handleLogin()}
        />
        <button
          onClick={handleLogin}
          className="bg-sky-600 text-white rounded-lg px-6 py-3 flex items-center gap-2 font-medium"
        >
          입력
        </button>
      </div>
      <button className="bg-red-500 text-white rounded-lg py-3 font-medium flex items-center justify-center gap-2">
        삭제
      </button>
    </div>
  );
};

export default Login;
