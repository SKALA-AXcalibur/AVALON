import { Testcase } from "./testcase";

interface Scenario {
  id: string;
  name: string;
  testcases: Testcase[];
}

interface ScenarioInfo {
  id: string;
  name: string;
  graph: string;
  description: string;
  validation: string;
}

export type { Scenario, ScenarioInfo };
