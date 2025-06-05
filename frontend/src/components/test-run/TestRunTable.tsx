"use client";

const rows = [
  {
    id: "TC-001",
    name: "존재하는 고객 확인",
    input: "ID: Torsvalds / Password: ILoveLinux",
    expected: "고객이 존재합니다.",
    result: { label: "성공", color: "bg-green-100 text-green-700" },
  },
  {
    id: "TC-002",
    name: "존재하지 않는 고객 확인",
    input: "ID: test_user / Password: test1234",
    expected: "고객이 존재하지 않습니다.",
    result: { label: "실패", color: "bg-red-100 text-red-700" },
  },
  {
    id: "TC-003",
    name: "잘못된 비밀번호로 로그인 시도",
    input: "ID: admin / Password: admin1234",
    expected: "비밀번호가 일치하지 않습니다.",
    result: { label: "실패", color: "bg-red-100 text-red-700" },
  },
];

const TestRunTable = () => {
  return (
    <div className="bg-white border border-slate-200 rounded-lg mb-8 overflow-x-auto">
      <table className="min-w-full text-left">
        <thead className="bg-slate-50">
          <tr>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              Test ID
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              테스트 명
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              입력 데이터
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              기대 결과
            </th>
            <th className="px-4 py-2 text-xs font-bold text-slate-800">
              성공여부
            </th>
          </tr>
        </thead>
        <tbody>
          {rows.map((row) => (
            <tr
              key={row.id}
              className="border-t"
              style={{ borderColor: "#f1f5f9" }}
            >
              <td className="px-4 py-2 text-sm font-medium text-slate-900">
                {row.id}
              </td>
              <td className="px-4 py-2 text-sm text-slate-600">{row.name}</td>
              <td className="px-4 py-2 text-sm text-slate-600">{row.input}</td>
              <td className="px-4 py-2 text-sm text-slate-600">
                {row.expected}
              </td>
              <td className="px-4 py-2">
                <span
                  className={`inline-block rounded-full px-3 py-1 text-xs font-medium ${row.result.color}`}
                >
                  {row.result.label}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

export default TestRunTable;
