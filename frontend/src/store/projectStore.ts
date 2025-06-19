import { Scenario } from "@/interfaces/scenario";
import { Project } from "@/types/project";
import { create } from "zustand";

interface ProjectState {
  project: Project;
  setProject: (project: Project) => void;
  addScenario: (scenario: Scenario) => void;
  updateScenario: (scenario: Scenario) => void;
  deleteScenario: (scenarioId: string) => void;
  resetProject: () => void;
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
          s.id === scenario.id ? scenario : s
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
  resetProject: () => set({ project: { id: "", scenarios: [] } }),
}));
