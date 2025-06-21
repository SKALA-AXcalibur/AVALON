import { Testcase } from "./testcase";

export interface Scenario {
  id: string;
  name: string;
  testcases: Testcase[];
}

export interface ScenarioInfo {
  id: string;
  name: string;
  graph: string;
  description: string;
  validation: string;
}
