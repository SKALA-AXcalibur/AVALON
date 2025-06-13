export interface Testcase {
  tcId: string;
}

export interface TestData {
  key: string;
  type: "string" | "number" | "boolean";
  value: string;
}

export interface TestcaseInfo {
  tcId: string;
  precondition: string;
  description: string;
  expectedResult: string;
  testDataList: TestData[];
}
