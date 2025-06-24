"use client";
import { TestRunTable } from "./TestRunTable";
import { ActionButton } from "@/components/common/ActionButton";
import { useTestRun } from "@/hooks/useTestRun";

export const TestRunBox = ({ scenarioId }: { scenarioId: string }) => {
  const { scenario, loadingStates, readScenarioReport, readTestcaseReport } =
    useTestRun(scenarioId);

  return (
    <>
      {loadingStates.scenarioResult ? (
        <div className="flex items-center justify-center h-full">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-sky-500"></div>
        </div>
      ) : (
        <>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-slate-800">
              {scenario.scenarioName}
              <span className="text-sm text-slate-500 ml-2">
                {scenario.scenarioId}
              </span>
            </h2>
            <div className="flex gap-2">
              <ActionButton
                onClick={readScenarioReport}
                color="bg-transparent text-slate-700 hover:text-orange-500"
                disabled={loadingStates.scenarioReport}
              >
                시나리오 다운로드
              </ActionButton>
              <ActionButton
                onClick={readTestcaseReport}
                color="bg-transparent text-slate-700 hover:text-orange-500"
                disabled={loadingStates.testcaseReport}
              >
                테스트케이스 다운로드
              </ActionButton>
            </div>
          </div>
          <TestRunTable testcaseList={scenario.tcList} />
        </>
      )}
    </>
  );
};
