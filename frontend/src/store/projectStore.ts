import { create } from "zustand";

interface Project {
  id: string;
  scenarioIds: string[];
}

interface ProjectState {
  project: Project;
  setProject: (project: Project) => void;
}

export const useProjectStore = create<ProjectState>((set) => ({
  project: {
    id: "PJ-001",
    scenarioIds: [],
  },
  setProject: (project: Project) => set({ project }),
}));
