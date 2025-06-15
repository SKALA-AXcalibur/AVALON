"use client";
import Link from "next/link";
import { ApiTestResult } from "@/interfaces/apiTest";
import ActionButton from "@/components/common/ActionButton";
import { useReadScenarioReport } from "@/hooks/test-run/readScenarioReport";

const TestRunSidebar = ({
  apiTestResult,
}: {
  apiTestResult: ApiTestResult;
}) => {
  const { readScenarioReport, isLoading: isReadingScenarioReport } =
    useReadScenarioReport();

  return (
    <aside className="w-72 bg-slate-50 border-r border-slate-200 flex flex-col">
      <div className="flex-1 p-6 overflow-y-auto">
        {apiTestResult.scenarioList.map((s) => (
          <Link
            key={s.scenarioId}
            href={`/project/${apiTestResult.projectId}/test-run/${s.scenarioId}`}
          >
            <div
              key={s.scenarioId}
              className="mb-8 flex items-center justify-between"
            >
              <div className="font-bold text-slate-800">{s.scenarioName}</div>
              <span
                className={`material-icons ${
                  s.scenarioExecution ? "text-green-500" : "text-red-500"
                }`}
              >
                {s.scenarioExecution ? "pass" : "fail"}
              </span>
            </div>
          </Link>
        ))}
      </div>
      <div className="p-6 border-t border-slate-200">
        <ActionButton
          onClick={() => {
            readScenarioReport();
          }}
          color="w-full bg-emerald-500 hover:bg-emerald-600 justify-center"
          isLoading={isReadingScenarioReport}
        >
          시나리오 다운로드
        </ActionButton>
      </div>
    </aside>
  );
};

export default TestRunSidebar;
