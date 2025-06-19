export interface TestcaseResult {
  tcId: string;
  description: string;
  expectedResult: string;
  isSuccess: string;
  executedTime: string;
}

export interface ScenarioResult {
  scenarioId: string;
  scenarioName: string;
  isSuccess: string;
  tcList: TestcaseResult[];
}

export interface ApiTestResult {
  scenarioList: ScenarioResult[];
}
