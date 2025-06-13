"use client";
import { useState } from "react";
import { TestData } from "@/interfaces/testcase";

const TestcaseDataTable = ({
  testDataList,
  onTestDataListChange,
}: {
  testDataList: TestData[];
  onTestDataListChange: (testDataList: TestData[]) => void;
}) => {
  const [rows, setRows] = useState<TestData[]>(testDataList);

  const addRow = () => {
    const newRows = [...rows, { key: "", type: "string", value: "" }];
    setRows(newRows);
    updateTestcaseParameters(newRows);
  };

  const removeRow = (idx: number) => {
    const newRows = rows.filter((_, i) => i !== idx);
    setRows(newRows);
    updateTestcaseParameters(newRows);
  };

  const updateRow = (idx: number, field: keyof TestData, value: string) => {
    const newRows = rows.map((row, i) =>
      i === idx ? { ...row, [field]: value } : row
    );
    setRows(newRows);
    updateTestcaseParameters(newRows);
  };

  const updateTestcaseParameters = (newRows: TestData[]) => {
    const testDataList = newRows.filter((row) => row.key && row.value);
    onTestDataListChange(testDataList);
  };

  return (
    <div className="bg-white border border-slate-200 rounded-lg mb-8 overflow-x-auto">
      <table className="min-w-full text-left table-fixed">
        <colgroup>
          <col className="w-5/12" />
          <col className="w-1/12" />
          <col className="w-6/12" />
          <col className="w-16" />
        </colgroup>
        <thead className="bg-slate-50">
          <tr>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">Key</th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">Type</th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              Value
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              <button
                className="bg-sky-400 text-white rounded-full w-6 h-6 flex items-center justify-center mx-auto"
                onClick={addRow}
                aria-label="Add row"
                type="button"
              >
                +
              </button>
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
              <td className="px-4 py-2 text-sm font-medium text-slate-900">
                <input
                  className="w-full bg-transparent outline-none"
                  value={row.key}
                  onChange={(e) => updateRow(idx, "key", e.target.value)}
                  placeholder="Key"
                />
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <select
                  className="w-full bg-transparent outline-none"
                  value={row.type}
                  onChange={(e) => updateRow(idx, "type", e.target.value)}
                >
                  <option value="string">String</option>
                  <option value="number">Number</option>
                  <option value="boolean">Boolean</option>
                </select>
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">
                <input
                  className="w-full bg-transparent outline-none"
                  value={row.value}
                  onChange={(e) => updateRow(idx, "value", e.target.value)}
                  placeholder="Value"
                />
              </td>
              <td className="px-4 py-2 text-center">
                <button
                  className="bg-red-400 text-white rounded-full w-6 h-6 flex items-center justify-center mx-auto"
                  onClick={() => removeRow(idx)}
                  aria-label="Remove row"
                  type="button"
                >
                  −
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TestcaseDataTable;
