import { ApiTestResult } from "@/interfaces/apiTest";

export type runApiTestRequest = {
  scenarioList: { scenarioId: string }[];
};

export type readApiTestResultResponse = ApiTestResult;
