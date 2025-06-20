"use client";
import { TestcaseResult } from "@/interfaces/apiTest";

export const TestRunTable = ({
  testcaseList,
}: {
  testcaseList: TestcaseResult[];
}) => {
  const getStatusStyle = (status: string) => {
    switch (status) {
      case "성공":
        return "bg-green-100 text-green-700";
      case "실패":
        return "bg-red-100 text-red-700";
      case "실행중":
        return "bg-yellow-100 text-yellow-700";
      case "준비중":
        return "bg-gray-100 text-gray-700";
      default:
        return "bg-gray-100 text-gray-700";
    }
  };

  return (
    <div className="bg-white border border-slate-200 rounded-lg mb-8 overflow-x-auto">
      <table className="min-w-full text-left table-fixed">
        <thead className="bg-slate-50">
          <tr>
            <th className="w-[120px] px-4 py-2 text-xs font-bold text-slate-600">
              Test ID
            </th>
            <th className="w-[200px] px-4 py-2 text-xs font-bold text-slate-600">
              테스트 내용
            </th>
            <th className="w-[200px] px-4 py-2 text-xs font-bold text-slate-600">
              기대 결과
            </th>
            <th className="w-[100px] px-4 py-2 text-xs font-bold text-slate-600">
              성공여부
            </th>
          </tr>
        </thead>
        <tbody>
          {testcaseList?.length > 0 ? (
            testcaseList.map((tc) => (
              <tr
                key={tc.tcId}
                className="border-t"
                style={{ borderColor: "#f1f5f9" }}
              >
                <td className="px-4 py-2 text-sm font-medium text-slate-600 truncate">
                  {tc.tcId}
                </td>
                <td className="px-4 py-2 text-sm text-slate-600 truncate">
                  {tc.description}
                </td>
                <td className="px-4 py-2 text-sm text-slate-600 truncate">
                  {tc.expectedResult}
                </td>
                <td className="px-4 py-2">
                  <span
                    className={`inline-block rounded-full px-3 py-1 text-xs font-medium ${getStatusStyle(
                      tc.isSuccess
                    )}`}
                  >
                    {tc.isSuccess}
                  </span>
                </td>
              </tr>
            ))
          ) : (
            <tr>
              <td colSpan={4} className="px-4 py-16 text-center text-slate-500">
                테스트 케이스가 없습니다.
              </td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
};
