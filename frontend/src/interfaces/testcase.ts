export interface Testcase {
  tcId: string;
}

export interface api {
  apiId: string;
  apiName: string;
}

export interface Param {
  paramId: number;
  category: string;
  koName: string;
  name: string;
  context: string;
  type: string;
  length: number | null;
  format: string | null;
  defaultValue: string | null;
  required: boolean;
  parent: string | null;
  desc: string | null;
  value: string | null;
}

export interface TestcaseInfo {
  tcId: string;
  precondition: string | null;
  description: string;
  expectedResult: string;
  status: number;
  testDataList: Param[] | null;
}
