export interface TestcaseResult {
  tcId: string;
  testDescription: string;
  inputData: string;
  expectedResult: string;
  testResult: boolean;
}

export interface ScenarioResult {
  scenarioId: string;
  scenarioName: string;
  scenarioDescription: string;
  scenarioExecution: boolean;
  tcList: TestcaseResult[];
}

export interface ApiTestResult {
  projectId: string;
  scenarioList: ScenarioResult[];
}
