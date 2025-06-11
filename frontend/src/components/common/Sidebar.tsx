"use client";
import { useProjectStore } from "@/store/projectStore";
import { useRouter } from "next/navigation";
import LinkButton from "./LinkButton";
import { useState, useEffect } from "react";
import useReadScenarioTestcases from "@/hooks/testcase/readScenarioTestcases";

const Sidebar = ({
  projectId,
  scenarioId,
  testcaseId,
}: {
  projectId: string;
  scenarioId: string;
  testcaseId?: string;
}) => {
  const router = useRouter();
  const { project } = useProjectStore();
  const [openScenarios, setOpenScenarios] = useState<Set<string>>(
    new Set(scenarioId ? [scenarioId] : [])
  );
  const { readScenarioTestcases } = useReadScenarioTestcases();

  useEffect(() => {
    const fetchInitialTestcases = async () => {
      if (scenarioId) {
        await readScenarioTestcases(scenarioId);
      }
    };
    fetchInitialTestcases();
  }, []);

  const handleToggleClick = async (scenarioId: string) => {
    const isCurrentlyOpen = openScenarios.has(scenarioId);

    setOpenScenarios((prev) => {
      const newSet = new Set(prev);
      if (isCurrentlyOpen) {
        newSet.delete(scenarioId);
      } else {
        newSet.add(scenarioId);
      }
      return newSet;
    });

    if (!isCurrentlyOpen) {
      await readScenarioTestcases(scenarioId);
    }
  };

  const handleScenarioClick = (scenarioId: string) => {
    router.push(`/project/${projectId}/scenario/${scenarioId}`);
  };

  const handleTestcaseClick = (scenarioId: string, testcaseId: string) => {
    router.push(
      `/project/${projectId}/scenario/${scenarioId}/testcase/${testcaseId}`
    );
  };

  return (
    <aside className="w-72 bg-slate-50 border-r border-slate-200 flex flex-col">
      <div className="flex-1 p-6 overflow-y-auto">
        {project.scenarios.map((scenario) => (
          <div key={scenario.id} className="mb-4">
            <div className="flex items-center w-full font-bold text-slate-800">
              <button
                className="mr-2 focus:outline-none"
                onClick={() => handleToggleClick(scenario.id)}
              >
                {scenario.testcases.length > 0
                  ? openScenarios.has(scenario.id)
                    ? "⏷"
                    : "⏵"
                  : "⏵"}
              </button>
              <span
                className={`cursor-pointer hover:text-sky-600 ${
                  scenario.id === scenarioId ? "text-sky-600" : ""
                }`}
                onClick={() => handleScenarioClick(scenario.id)}
              >
                {scenario.name}
              </span>
            </div>
            {scenario.testcases.length > 0 &&
              openScenarios.has(scenario.id) && (
                <div className="mt-2 ml-6">
                  {scenario.testcases.map((testcase) => (
                    <div
                      key={testcase.tcId}
                      onClick={() =>
                        handleTestcaseClick(scenario.id, testcase.tcId)
                      }
                      className={`block rounded-lg px-4 py-2 text-slate-600 hover:bg-sky-50 cursor-pointer ${
                        testcase.tcId === testcaseId
                          ? "bg-sky-50 text-sky-600"
                          : ""
                      }`}
                    >
                      {testcase.tcId}
                    </div>
                  ))}
                </div>
              )}
          </div>
        ))}
      </div>
      <div className="p-6 border-t border-slate-200">
        <LinkButton
          href={`/project/${projectId}/scenario/new`}
          color="bg-emerald-500 hover:bg-emerald-600"
          ariaLabel="시나리오 추가"
        >
          시나리오 추가
        </LinkButton>
      </div>
    </aside>
  );
};

export default Sidebar;
