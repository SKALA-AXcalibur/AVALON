"use client";
import { ApiTestResult } from "@/interfaces/apiTest";
import TestRunSidebar from "./TestRunSidebar";
import TestRunTable from "./TestRunTable";
import { useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import ActionButton from "@/components/common/ActionButton";
import { useReadTestcaseReport } from "@/hooks/test-run/readTestcaseReport";

export const TestRunBox = ({
  projectId,
  scenarioId,
  apiTestResult,
}: {
  projectId: string;
  scenarioId: string;
  apiTestResult: ApiTestResult;
}) => {
  const { readTestcaseReport, isLoading: isReadingTestcaseReport } =
    useReadTestcaseReport();
  const [idx, setIdx] = useState(0);
  const router = useRouter();

  useEffect(() => {
    if (apiTestResult.scenarioList.length === 0) {
      alert("시나리오가 목록이 비어있습니다. 시나리오 페이지로 돌아갑니다.");
      router.push(`/project/${projectId}/scenario/${scenarioId}`);
      return;
    }

    const newIndex = apiTestResult.scenarioList.findIndex(
      (s) => s.scenarioId === scenarioId
    );
    console.log(apiTestResult.scenarioList, scenarioId, newIndex);
    if (newIndex === -1) {
      const firstScenarioId = apiTestResult.scenarioList[0].scenarioId;
      router.replace(`/project/${projectId}/test-run/${firstScenarioId}`);
      return;
    }

    setIdx(newIndex);
  }, [scenarioId, apiTestResult.scenarioList, projectId, router]);

  return (
    <div className="flex flex-1">
      <TestRunSidebar apiTestResult={apiTestResult} />
      <main className="flex-1 p-12">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-bold text-slate-800">
            {apiTestResult.scenarioList[idx]?.scenarioName}
            <span className="text-sm text-slate-500">
              {apiTestResult.scenarioList[idx]?.scenarioId}
            </span>
          </h2>
          <ActionButton
            onClick={() => {
              readTestcaseReport(apiTestResult.scenarioList[idx]?.scenarioId);
            }}
            color="bg-emerald-500 hover:bg-emerald-600"
            isLoading={isReadingTestcaseReport}
          >
            테스트케이스 다운로드
          </ActionButton>
        </div>
        <TestRunTable testcaseList={apiTestResult.scenarioList[idx]?.tcList} />
      </main>
    </div>
  );
};
