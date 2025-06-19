"use client";
import TestcaseDataTable from "./TestcaseDataTable";
import TextInputBox from "../common/TextInputBox";
import LinkButton from "../common/LinkButton";
import ActionButton from "../common/ActionButton";
import { useTestcase } from "@/hooks/testcase/useTestcase";
import { useRouter } from "next/navigation";

export const TestcaseBox = ({
  projectId,
  scenarioId,
  testcaseId,
}: {
  projectId: string;
  scenarioId: string;
  testcaseId: string;
}) => {
  const router = useRouter();
  const {
    testcaseInfo,
    isLoading,
    error,
    success,
    handlePreconditionChange,
    handleDescriptionChange,
    handleExpectedResultChange,
    handleTestDataListChange,
    handleCreate,
    handleUpdate,
    handleDelete,
  } = useTestcase(projectId, scenarioId, testcaseId);

  const onCreateSuccess = (tcId: string) => {
    router.push(
      `/project/${projectId}/scenario/${scenarioId}/testcase/${tcId}`
    );
  };

  const onDeleteSuccess = () => {
    router.push(`/project/${projectId}/scenario/${scenarioId}`);
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
            <div className="flex gap-2">
              <LinkButton
                href={`/project/${projectId}/scenario/${scenarioId}/testcase/new`}
                color="bg-sky-400 hover:bg-sky-500"
                ariaLabel="TC 추가"
              >
                TC 추가
              </LinkButton>
              <ActionButton
                onClick={() => handleDelete(onDeleteSuccess)}
                color="bg-red-500 hover:bg-red-600"
                disabled={isLoading}
              >
                삭제
              </ActionButton>
              {testcaseId === "new" ? (
                <ActionButton
                  onClick={() => handleCreate(onCreateSuccess)}
                  color="bg-green-500 hover:bg-green-600"
                  disabled={isLoading}
                >
                  생성
                </ActionButton>
              ) : (
                <ActionButton
                  onClick={handleUpdate}
                  color="bg-green-500 hover:bg-green-600"
                  disabled={isLoading}
                >
                  저장
                </ActionButton>
              )}
            </div>
          </div>
          <div className="h-[200px] flex gap-8 mb-8">
            <TextInputBox
              title="사전 조건"
              value={testcaseInfo.precondition}
              placeholder="테스트케이스의 사전 조건을 입력하세요"
              onChange={(e) => handlePreconditionChange(e.target.value)}
            />
            <TextInputBox
              title="내용"
              value={testcaseInfo.description}
              placeholder="테스트케이스의 내용을 입력하세요"
              onChange={(e) => handleDescriptionChange(e.target.value)}
            />
          </div>
          <TestcaseDataTable
            testDataList={testcaseInfo.testDataList}
            onTestDataListChange={handleTestDataListChange}
          />
          <div className="h-[250px] flex gap-8">
            <TextInputBox
              title="예상 결과"
              value={testcaseInfo.expectedResult}
              placeholder="테스트케이스의 예상 결과를 입력하세요"
              onChange={(e) => handleExpectedResultChange(e.target.value)}
            />
          </div>
          {error ? (
            <div className="h-6 mt-1 text-sm text-red-600">{error}</div>
          ) : (
            <div className="h-6 mt-1 text-sm text-green-600">{success}</div>
          )}
        </>
      )}
    </>
  );
};
