import { ScenarioResult } from "@/interfaces/apiTest";

export type runApiTestRequest = {
  scenarioList: string[];
};

export type readApiTestResultResponse = {
  scenarioList: Omit<ScenarioResult, "tcList">[];
};

export type readApiTestScenarioResultResponse = Omit<
  ScenarioResult,
  "isSuccess"
>;
