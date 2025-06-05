"use client";
import { useState, useEffect } from "react";

type Row = { key: string; value: string };

interface TestcaseTableProps {
  parameters: { [key: string]: string };
  onParametersChange: (parameters: { [key: string]: string }) => void;
}

const TestcaseTable = ({
  parameters,
  onParametersChange,
}: TestcaseTableProps) => {
  const [rows, setRows] = useState<Row[]>([]);

  useEffect(() => {
    const newRows = Object.entries(parameters).map(([key, value]) => ({
      key,
      value,
    }));
    setRows(newRows);
  }, [parameters]);

  const addRow = () => setRows([...rows, { key: "", value: "" }]);

  const removeRow = (idx: number) => {
    const newRows = rows.filter((_, i) => i !== idx);
    setRows(newRows);
    updateTestcaseParameters(newRows);
  };

  const updateRow = (idx: number, field: "key" | "value", value: string) => {
    const newRows = rows.map((row, i) =>
      i === idx ? { ...row, [field]: value } : row
    );
    setRows(newRows);
    updateTestcaseParameters(newRows);
  };

  const updateTestcaseParameters = (newRows: Row[]) => {
    const parameters = newRows.reduce((acc, row) => {
      if (row.key && row.value) {
        acc[row.key] = row.value;
      }
      return acc;
    }, {} as { [key: string]: string });

    onParametersChange(parameters);
  };

  return (
    <div className="bg-white border border-slate-200 rounded-lg mb-8 overflow-x-auto">
      <table className="min-w-full text-left">
        <thead className="bg-slate-50">
          <tr>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">Key</th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              Value
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800 w-12">
              <button
                className="bg-sky-400 text-white rounded-full w-6 h-6 flex items-center justify-center"
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
                <input
                  className="w-full bg-transparent outline-none"
                  value={row.value}
                  onChange={(e) => updateRow(idx, "value", e.target.value)}
                  placeholder="Value"
                />
              </td>
              <td className="px-4 py-2 text-center">
                <button
                  className="bg-red-400 text-white rounded-full w-6 h-6 flex items-center justify-center"
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

export default TestcaseTable;
