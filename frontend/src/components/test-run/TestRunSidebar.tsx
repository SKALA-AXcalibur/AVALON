"use client";
import Link from "next/link";
import { useParams } from "next/navigation";

const scenarioList = [
  {
    id: "scenario-1",
    title: "시나리오 #1",
    icon: "fail",
    iconColor: "text-red-500",
  },
  {
    id: "scenario-2",
    title: "시나리오 #2",
    icon: "success",
    iconColor: "text-green-500",
  },
];

const TestRunSidebar = () => {
  const params = useParams();

  return (
    <aside className="w-72 bg-slate-50 border-r border-slate-200 flex flex-col">
      <div className="flex-1 p-6 overflow-y-auto">
        {scenarioList.map((s) => (
          <Link
            key={s.id}
            href={`/project/${params["project-id"]}/test-run/${s.id}`}
          >
            <div key={s.id} className="mb-8 flex items-center justify-between">
              <div className="font-bold text-slate-800">{s.title}</div>
              <span className={`material-icons ${s.iconColor}`}>{s.icon}</span>
            </div>
          </Link>
        ))}
      </div>
      <div className="p-6 border-t border-slate-200">
        <button className="w-full bg-emerald-500 text-white rounded-lg py-3 flex items-center justify-center gap-2">
          시나리오 다운로드
        </button>
      </div>
    </aside>
  );
};

export default TestRunSidebar;
