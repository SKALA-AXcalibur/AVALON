"use client";
import { useProjectStore } from "@/store/projectStore";
import { useScenarioStore } from "@/store/scenarioStore";
import { useTestcaseStore } from "@/store/testcaseStore";
import { useSidebarStore } from "@/store/sidebarStore";
import { useRouter } from "next/navigation";

const mockScenarioList = [
  {
    id: "scenario-1",
    title: "시나리오 #1",
    testcaseIds: ["TC-001", "TC-002"],
  },
  {
    id: "scenario-2",
    title: "시나리오 #2",
    testcaseIds: ["TC-001", "TC-002", "TC-003"],
  },
];

const Sidebar = () => {
  const router = useRouter();
  const { project } = useProjectStore();
  const { setScenario } = useScenarioStore();
  const { setTestcase } = useTestcaseStore();
  const { openIndex, setOpenIndex } = useSidebarStore();

  const handleScenarioClick = (scenarioId: string, idx: number) => {
    setOpenIndex(idx);
    setScenario({
      id: scenarioId,
      title: "",
      description: "",
      verify: "",
      testcaseIds: [],
    });
    router.push(`/project/${project.id}/scenario/${scenarioId}`);
  };

  const handleTestcaseClick = (scenarioId: string, testcaseId: string) => {
    setTestcase({
      id: testcaseId,
      precondition: "",
      content: "",
      parameters: {},
      expected: "",
    });
    router.push(
      `/project/${project.id}/scenario/${scenarioId}/testcase/${testcaseId}`
    );
  };

  const handleAddScenario = () => {
    setScenario({
      id: "",
      title: "",
      description: "",
      verify: "",
      testcaseIds: [],
    });
    router.push(`/project/${project.id}/scenario/`);
  };

  return (
    <aside className="w-72 bg-slate-50 border-r border-slate-200 flex flex-col">
      <div className="flex-1 p-6 overflow-y-auto">
        {mockScenarioList.map((scenario, idx) => (
          <div key={scenario.id} className="mb-4">
            <div className="flex items-center w-full font-bold text-slate-800">
              <button
                className="mr-2 focus:outline-none"
                onClick={() => setOpenIndex(openIndex === idx ? null : idx)}
              >
                {scenario.testcaseIds.length > 0
                  ? openIndex === idx
                    ? "⏷"
                    : "⏵"
                  : "⏵"}
              </button>
              <span
                className="cursor-pointer hover:text-sky-600"
                onClick={() => handleScenarioClick(scenario.id, idx)}
              >
                {scenario.title}
              </span>
            </div>
            {scenario.testcaseIds.length > 0 && openIndex === idx && (
              <div className="mt-2 ml-6">
                {scenario.testcaseIds.map((testcaseId) => (
                  <div
                    key={testcaseId}
                    onClick={() => handleTestcaseClick(scenario.id, testcaseId)}
                    className="block rounded-lg px-4 py-2 text-slate-600 hover:bg-sky-50 cursor-pointer"
                  >
                    {testcaseId}
                  </div>
                ))}
              </div>
            )}
          </div>
        ))}
      </div>
      <div className="p-6 border-t border-slate-200">
        <button
          onClick={handleAddScenario}
          className="w-full bg-emerald-500 text-white rounded-lg py-3 flex items-center justify-center gap-2 hover:bg-emerald-600"
        >
          시나리오 추가
        </button>
      </div>
    </aside>
  );
};

export default Sidebar;
