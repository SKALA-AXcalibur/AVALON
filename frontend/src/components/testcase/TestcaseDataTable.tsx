"use client";
import { useEffect, useState } from "react";
import { Param } from "@/interfaces/testcase";

export const TestcaseDataTable = ({
  testDataList,
  onTestDataListChange,
}: {
  testDataList: Param[];
  onTestDataListChange: (testDataList: Param[]) => void;
}) => {
  const [rows, setRows] = useState<Param[]>(testDataList);

  useEffect(() => {
    setRows(testDataList);
  }, [testDataList]);

  const updateValue = (idx: number, value: string) => {
    const newRows = rows.map((row, i) => (i === idx ? { ...row, value } : row));
    setRows(newRows);
    onTestDataListChange(newRows);
  };

  return (
    <div className="bg-white border border-slate-200 rounded-lg mb-8 overflow-x-auto">
      <table className="min-w-full text-left table-fixed">
        <colgroup>
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-1/12" />
          <col className="w-2/12" />
        </colgroup>
        <thead className="bg-slate-50">
          <tr>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              한글명
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              영문명
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              항목유형
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">단계</th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              데이터타입
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">길이</th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">포맷</th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              기본값
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              필수여부
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              상위항목명
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">설명</th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              테스트값
            </th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row, idx) => (
            <tr
              key={idx}
              className="border-t"
              style={{ borderColor: "#f1f5f9" }}
            >
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.koName || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.name || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.category || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.context || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.type || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.length || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.format || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">
                  {row.defaultValue || "-"}
                </span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">
                  {row.required ? "Y" : "N"}
                </span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.parent || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <span className="text-slate-700">{row.desc || "-"}</span>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <input
                  className="w-full bg-transparent outline-none border-b border-slate-300 focus:border-sky-500 px-1 py-1"
                  value={row.value || ""}
                  onChange={(e) => updateValue(idx, e.target.value)}
                  placeholder="테스트값 입력"
                />
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};
