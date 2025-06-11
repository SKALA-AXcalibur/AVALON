import { Testcase } from "@/interfaces/testcase";

type readScenarioTestcasesResponse = {
  tcList: Testcase[];
  tcTotal: number;
};

export type { readScenarioTestcasesResponse };
