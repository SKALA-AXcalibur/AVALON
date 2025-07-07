import { ApiTestResult } from "@/interfaces/apiTest";
import { create } from "zustand";

interface TestResultState {
  testResult: ApiTestResult;
  setTestResult: (testResult: ApiTestResult) => void;
}

export const useTestResultStore = create<TestResultState>((set) => ({
  testResult: {
    scenarioList: [],
  },
  setTestResult: (testResult: ApiTestResult) => set({ testResult }),
}));
