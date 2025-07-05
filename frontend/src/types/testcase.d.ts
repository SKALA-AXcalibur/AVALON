import { TestcaseInfo, api, Param } from "@/interfaces/testcase";

export type createTestcaseRequest = {
  precondition: string | null;
  description: string;
  expectedResult: string;
  status: number | null;
  testDataList: { paramId: number; value: string | null }[] | null;
};

type createTestcaseResponse = string;

export type readTestcaseResponse = TestcaseInfo;

export type updateTestcaseRequest = {
  precondition: string | null;
  description: string;
  expectedResult: string;
  status: number;
  testDataList: { paramId: number; value: string | null }[] | null;
};

export type readApiListResponse = {
  apiList: api[];
};

export type readParamsResponse = {
  testDataList: Omit<Param, "value">[];
};

export type readScenarioTestcasesResponse = {
  tcList: string[];
  tcTotal: number;
};
