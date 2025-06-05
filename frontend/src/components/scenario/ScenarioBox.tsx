"use client";
import { Scenario, useScenarioStore } from "@/store/scenarioStore";
import { useState, useEffect } from "react";

const mockScenario = [
  {
    id: "scenario-1",
    title: "시나리오 #1",
    description: "사용자 로그인 후 글쓰기 및 로그아웃을 실행한다.",
    verify: "로그인 성공 여부 확인",
    testcaseIds: ["TC-001", "TC-002"],
  },
  {
    id: "scenario-2",
    title: "시나리오 #2",
    description: "사용자 로그아웃을 실행한다.",
    verify: "로그아웃 성공 여부 확인",
    testcaseIds: ["TC-001", "TC-002", "TC-003"],
  },
];

const ScenarioBox = () => {
  const { scenario } = useScenarioStore();
  const [currentScenario, setCurrentScenario] = useState<Scenario>(scenario);

  useEffect(() => {
    const found = mockScenario.find((s) => s.id === scenario.id);
    if (found) {
      setCurrentScenario(found);
    }
  }, [scenario.id]);

  return (
    <>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-slate-800">
          {currentScenario.id} {currentScenario.title}
        </h2>
        <div className="flex gap-2">
          <button className="bg-sky-400 text-white rounded-lg px-4 py-2">
            TC 추가
          </button>
          <button className="bg-red-500 text-white rounded-lg px-4 py-2">
            삭제
          </button>
          <button className="bg-green-500 text-white rounded-lg px-4 py-2">
            저장
          </button>
        </div>
      </div>
      <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 mb-4">
        {/* mermaid 코드 기반 그래프 출력 */}
      </div>
      <div className="flex gap-8">
        <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 flex-1">
          <h3 className="font-medium text-slate-700 mb-2">상세설명</h3>
          <textarea
            value={currentScenario?.description}
            onChange={(e) =>
              setCurrentScenario({
                ...currentScenario,
                description: e.target.value,
              })
            }
            className="w-full h-32 p-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-sky-500"
            placeholder="시나리오에 대한 상세 설명을 입력하세요"
          />
        </div>
        <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 flex-1">
          <h3 className="font-medium text-slate-700 mb-2">검증포인트</h3>
          <textarea
            value={currentScenario?.verify}
            onChange={(e) =>
              setCurrentScenario({
                ...currentScenario,
                verify: e.target.value,
              })
            }
            className="w-full h-32 p-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-sky-500"
            placeholder="검증해야 할 포인트들을 입력하세요"
          />
        </div>
      </div>
    </>
  );
};

export default ScenarioBox;
