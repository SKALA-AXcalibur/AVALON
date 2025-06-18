import { Project } from "@/types/project";
import { create } from "zustand";

interface ProjectState {
  project: Project;
  setProject: (project: Project) => void;
  resetProject: () => void;
}

export const useProjectStore = create<ProjectState>((set) => ({
  project: {
    id: "",
    scenarios: [],
  },
  setProject: (project: Project) => set({ project }),
  resetProject: () => set({ project: { id: "", scenarios: [] } }),
}));
