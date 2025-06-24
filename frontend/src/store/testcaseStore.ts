import { create } from "zustand";

interface Testcase {
  id: string;
  precondition: string;
  content: string;
  parameters: {
    [key: string]: string;
  };
  expected: string;
}

interface TestcaseState {
  testcase: Testcase;
  setTestcase: (testcase: Testcase) => void;
}

export const useTestcaseStore = create<TestcaseState>((set) => ({
  testcase: {
    id: "TC-001",
    precondition: "",
    content: "",
    parameters: {},
    expected: "",
  },
  setTestcase: (testcase: Testcase) => set({ testcase }),
}));
