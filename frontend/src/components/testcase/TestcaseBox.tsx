"use client";
import { useState, useEffect } from "react";
import { useTestcaseStore } from "@/store/testcaseStore";
import TestcaseTable from "./TestcaseTable";

const mockTestcases = [
  {
    id: "TC-001",
    precondition: "데이터 베이스 내 테스트 고객 존재",
    content: "존재하는 고객 확인",
    parameters: {
      ID: "Torsvalds",
      Password: "ILoveLinux",
    },
    expected: "고객이 존재합니다.",
  },
  {
    id: "TC-002",
    precondition: "데이터 베이스 내 테스트 고객 존재",
    content: "존재하지 않는 고객 확인",
    parameters: {
      ID: "test_user",
      Password: "test1234",
    },
    expected: "고객이 존재하지 않습니다.",
  },
  {
    id: "TC-003",
    precondition: "데이터 베이스 내 테스트 고객 존재",
    content: "잘못된 비밀번호로 로그인 시도",
    parameters: {
      ID: "admin",
      Password: "admin1234",
    },
    expected: "비밀번호가 일치하지 않습니다.",
  },
];

const TestcaseBox = () => {
  const { testcase, setTestcase } = useTestcaseStore();
  const [currentTestcase, setCurrentTestcase] = useState(testcase);

  useEffect(() => {
    if (testcase.id) {
      const found = mockTestcases.find((tc) => tc.id === testcase.id);
      if (found) {
        setCurrentTestcase(found);
        setTestcase(found);
      }
    }
  }, [testcase.id, setTestcase]);

  const handleSave = () => {
    setTestcase(currentTestcase);
  };

  const handleParametersChange = (parameters: { [key: string]: string }) => {
    setCurrentTestcase({
      ...currentTestcase,
      parameters,
    });
  };

  return (
    <>
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-bold text-slate-800">
          {currentTestcase.id}
        </h2>
        <div className="flex gap-2">
          <button className="bg-sky-400 text-white rounded-lg px-4 py-2">
            TC 추가
          </button>
          <button className="bg-red-500 text-white rounded-lg px-4 py-2">
            삭제
          </button>
          <button
            onClick={handleSave}
            className="bg-green-500 text-white rounded-lg px-4 py-2"
          >
            저장
          </button>
        </div>
      </div>
      <div className="flex gap-8 mb-8">
        <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 flex-1">
          <h3 className="font-medium text-slate-700 mb-2">사전 조건</h3>
          <textarea
            value={currentTestcase.precondition}
            onChange={(e) =>
              setCurrentTestcase({
                ...currentTestcase,
                precondition: e.target.value,
              })
            }
            className="w-full h-32 p-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-sky-500"
            placeholder="테스트케이스의 사전 조건을 입력하세요"
          />
        </div>
        <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 flex-1">
          <h3 className="font-medium text-slate-700 mb-2">내용</h3>
          <textarea
            value={currentTestcase.content}
            onChange={(e) =>
              setCurrentTestcase({
                ...currentTestcase,
                content: e.target.value,
              })
            }
            className="w-full h-32 p-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-sky-500"
            placeholder="테스트케이스의 내용을 입력하세요"
          />
        </div>
      </div>
      <TestcaseTable
        parameters={currentTestcase.parameters}
        onParametersChange={handleParametersChange}
      />
      <div className="bg-slate-50 border border-slate-200 rounded-lg p-6">
        <h3 className="font-medium text-slate-700 mb-2">예상 결과</h3>
        <textarea
          value={currentTestcase.expected}
          onChange={(e) =>
            setCurrentTestcase({ ...currentTestcase, expected: e.target.value })
          }
          className="w-full h-32 p-2 border border-slate-300 rounded-lg focus:outline-none focus:ring-2 focus:ring-sky-500"
          placeholder="테스트케이스의 예상 결과를 입력하세요"
        />
      </div>
    </>
  );
};

export default TestcaseBox;
