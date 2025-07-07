import { create } from "zustand";

interface SidebarState {
  openScenarios: Set<string>;
  addOpenScenario: (scenarioId: string) => void;
  toggleOpenScenario: (scenarioId: string) => void;
  isOpen: (scenarioId: string) => boolean;
  resetSidebar: () => void;
}

export const useSidebarStore = create<SidebarState>((set, get) => ({
  openScenarios: new Set(),
  addOpenScenario: (scenarioId: string) =>
    set((state) => {
      const newSet = new Set(state.openScenarios);
      newSet.add(scenarioId);
      return { openScenarios: newSet };
    }),
  toggleOpenScenario: (scenarioId: string) =>
    set((state) => {
      const newSet = new Set(state.openScenarios);
      if (newSet.has(scenarioId)) {
        newSet.delete(scenarioId);
      } else {
        newSet.add(scenarioId);
      }
      return { openScenarios: newSet };
    }),
  isOpen: (scenarioId: string) => get().openScenarios.has(scenarioId),
  resetSidebar: () => set({ openScenarios: new Set() }),
}));
