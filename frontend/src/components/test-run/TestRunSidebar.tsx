"use client";
import Link from "next/link";
import { useTestResultStore } from "@/store/testResult";
import {
  FaCheckCircle,
  FaTimesCircle,
  FaHourglassHalf,
  FaRocket,
} from "react-icons/fa";

export const TestRunSidebar = ({ projectId }: { projectId: string }) => {
  const { testResult } = useTestResultStore();

  const getStatusDisplay = (status: string) => {
    switch (status) {
      case "성공":
        return {
          icon: <FaCheckCircle />,
          className: "text-green-500",
        };
      case "실패":
        return {
          icon: <FaTimesCircle />,
          className: "text-red-500",
        };
      case "실행중":
        return {
          icon: <FaHourglassHalf />,
          className: "text-yellow-500",
        };
      case "준비중":
        return {
          icon: <FaRocket />,
          className: "text-gray-500",
        };
      default:
        return {
          icon: <FaRocket />,
          className: "text-gray-500",
        };
    }
  };

  return (
    <aside className="w-72 bg-slate-50 border-r border-slate-200 flex flex-col">
      <div className="flex-1 p-6 overflow-y-auto">
        {testResult.scenarioList.map((s) => {
          const statusDisplay = getStatusDisplay(s.isSuccess);
          return (
            <Link
              key={s.scenarioId}
              href={`/project/${projectId}/test-run/${s.scenarioId}`}
            >
              <div className="mb-8 flex items-center justify-between hover:bg-slate-100 p-2 rounded transition-colors">
                <div className="font-bold text-slate-800">{s.scenarioName}</div>
                <span className={`text-xl ${statusDisplay.className}`}>
                  {statusDisplay.icon}
                </span>
              </div>
            </Link>
          );
        })}
      </div>
    </aside>
  );
};
