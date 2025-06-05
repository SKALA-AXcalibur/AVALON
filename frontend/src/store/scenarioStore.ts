import { create } from "zustand";

export interface Scenario {
  id: string;
  title: string;
  description: string;
  verify: string;
  testcaseIds: string[];
}

interface ScenarioState {
  scenario: Scenario;
  setScenario: (scenario: Scenario) => void;
}

export const useScenarioStore = create<ScenarioState>((set) => ({
  scenario: {
    id: "scenario-1",
    title: "",
    description: "",
    verify: "",
    testcaseIds: [],
  },
  setScenario: (scenario: Scenario) => set({ scenario }),
}));
