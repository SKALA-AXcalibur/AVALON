import { Scenario } from "@/interfaces/scenario";
import { Testcase } from "@/interfaces/testcase";
import { Project } from "@/types/project";
import { create } from "zustand";

interface ProjectState {
  project: Project;
  setProject: (project: Project) => void;
  addScenario: (scenario: Scenario) => void;
  updateScenario: (scenario: Scenario) => void;
  deleteScenario: (scenarioId: string) => void;
  addTestcase: (scenarioId: string, testcase: Testcase) => void;
  updateTestcase: (scenarioId: string, testcase: Testcase) => void;
  deleteTestcase: (scenarioId: string, testcaseId: string) => void;
  resetProject: () => void;
  resetAllScenarios: () => void;
}

export const useProjectStore = create<ProjectState>((set) => ({
  project: {
    id: "",
    scenarios: [],
  },
  setProject: (project: Project) => set({ project }),
  addScenario: (scenario: Scenario) =>
    set((state) => ({
      project: {
        ...state.project,
        scenarios: [...state.project.scenarios, scenario],
      },
    })),
  updateScenario: (scenario: Scenario) =>
    set((state) => ({
      project: {
        ...state.project,
        scenarios: state.project.scenarios.map((s) =>
          s.id === scenario.id ? scenario : s,
        ),
      },
    })),
  deleteScenario: (scenarioId: string) =>
    set((state) => ({
      project: {
        ...state.project,
        scenarios: state.project.scenarios.filter((s) => s.id !== scenarioId),
      },
    })),
  addTestcase: (scenarioId: string, testcase: Testcase) =>
    set((state) => ({
      project: {
        ...state.project,
        scenarios: state.project.scenarios.map((s) =>
          s.id === scenarioId
            ? { ...s, testcases: [...s.testcases, testcase] }
            : s,
        ),
      },
    })),
  updateTestcase: (scenarioId: string, testcase: Testcase) =>
    set((state) => ({
      project: {
        ...state.project,
        scenarios: state.project.scenarios.map((s) =>
          s.id === scenarioId
            ? {
                ...s,
                testcases: s.testcases.map((t) =>
                  t.tcId === testcase.tcId ? testcase : t,
                ),
              }
            : s,
        ),
      },
    })),
  deleteTestcase: (scenarioId: string, testcaseId: string) =>
    set((state) => ({
      project: {
        ...state.project,
        scenarios: state.project.scenarios.map((s) =>
          s.id === scenarioId
            ? {
                ...s,
                testcases: s.testcases.filter((t) => t.tcId !== testcaseId),
              }
            : s,
        ),
      },
    })),
  resetProject: () => set({ project: { id: "", scenarios: [] } }),
  resetAllScenarios: () =>
    set((state) => ({
      project: {
        ...state.project,
        scenarios: state.project.scenarios.map((s) => ({
          ...s,
          testcases: [],
        })),
      },
    })),
}));
