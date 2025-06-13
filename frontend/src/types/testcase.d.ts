import { Testcase, TestcaseInfo } from "@/interfaces/testcase";

export type createTestcaseRequest = Omit<TestcaseInfo, "tcId">;

type createTestcaseResponse = Pick<TestcaseInfo, "tcId">;

export type readTestcaseResponse = TestcaseInfo;

export type updateTestcaseRequest = Omit<TestcaseInfo, "tcId">;

export type readScenarioTestcasesResponse = {
  tcList: Testcase[];
  tcTotal: number;
};
