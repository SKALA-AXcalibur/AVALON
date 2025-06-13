"use client";
import { useEffect, useState } from "react";
import useReadTestcase from "@/hooks/testcase/readTestcase";
import { TestcaseInfo, TestData } from "@/interfaces/testcase";
import TestcaseNavigation from "./TestcaseNavigation";
import TestcaseDataTable from "./TestcaseDataTable";
import TextInputBox from "../common/TextInputBox";

const TestcaseBox = ({ testcaseId }: { testcaseId: string }) => {
  const { readTestcase, isLoading } = useReadTestcase(testcaseId);
  const [testcaseInfo, setTestcaseInfo] = useState<TestcaseInfo>({
    tcId: testcaseId,
    precondition: "",
    description: "",
    expectedResult: "",
    testDataList: [],
  });

  useEffect(() => {
    const fetchTestcaseInfo = async () => {
      const testcaseInfo = await readTestcase();
      if (!testcaseInfo) return;
      setTestcaseInfo(testcaseInfo);
    };
    fetchTestcaseInfo();
  }, [testcaseId]);

  const handlePreconditionChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setTestcaseInfo({ ...testcaseInfo, precondition: e.target.value });
  };

  const handleDescriptionChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setTestcaseInfo({ ...testcaseInfo, description: e.target.value });
  };

  const handleTestDataListChange = (testDataList: TestData[]) => {
    setTestcaseInfo({ ...testcaseInfo, testDataList });
  };

  const handleExpectedResultChange = (
    e: React.ChangeEvent<HTMLTextAreaElement>
  ) => {
    setTestcaseInfo({ ...testcaseInfo, expectedResult: e.target.value });
  };

  return (
    <>
      {isLoading ? (
        <div className="flex items-center justify-center h-full">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-sky-500"></div>
        </div>
      ) : (
        <>
          <div className="flex items-center justify-between mb-4">
            <h2 className="text-xl font-bold text-slate-800">
              {testcaseInfo.tcId}
            </h2>
            <TestcaseNavigation testcaseInfo={testcaseInfo} />
          </div>
          <div className="flex gap-8 mb-8">
            <TextInputBox
              title="사전 조건"
              value={testcaseInfo.precondition}
              placeholder="테스트케이스의 사전 조건을 입력하세요"
              onChange={handlePreconditionChange}
            />
            <TextInputBox
              title="내용"
              value={testcaseInfo.description}
              placeholder="테스트케이스의 내용을 입력하세요"
              onChange={handleDescriptionChange}
            />
          </div>
          <TestcaseDataTable
            testDataList={testcaseInfo.testDataList}
            onTestDataListChange={handleTestDataListChange}
          />
          <TextInputBox
            title="예상 결과"
            value={testcaseInfo.expectedResult}
            placeholder="테스트케이스의 예상 결과를 입력하세요"
            onChange={handleExpectedResultChange}
          />
        </>
      )}
    </>
  );
};

export default TestcaseBox;
