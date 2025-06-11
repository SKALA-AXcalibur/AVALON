"use client";
import { useState, useEffect } from "react";
import TestcaseTable from "./TestcaseTable";

const testcase = {
  id: "TC-001",
  precondition: "데이터 베이스 내 테스트 고객 존재",
  content: "존재하는 고객 확인",
  parameters: {
    ID: "Torsvalds",
    Password: "ILoveLinux",
  },
  expected: "고객이 존재합니다.",
};

const TestcaseBox = () => {
  const [currentTestcase, setCurrentTestcase] = useState(testcase);

  useEffect(() => {
    if (testcase.id) {
      setCurrentTestcase(testcase);
    }
  }, [testcase.id]);

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
      <TestcaseTable parameters={currentTestcase.parameters} />
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
