import Header from "@/components/common/header/Header";
import Sidebar from "@/components/common/Sidebar";

const ScenarioLayout = async ({
  children,
  params,
}: {
  children: React.ReactNode;
  params: {
    "project-id": string;
    "scenario-id": string;
    "testcase-id"?: string;
  };
}) => {
  const {
    "project-id": projectId,
    "scenario-id": scenarioId,
    "testcase-id": testcaseId,
  } = await params;

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header projectId={projectId} />
      <div className="flex flex-1">
        <Sidebar
          projectId={projectId}
          scenarioId={scenarioId}
          testcaseId={testcaseId}
        />
        <main className="flex-1 p-12 overflow-y-auto h-[calc(100vh-128px)]">
          {children}
        </main>
      </div>
    </div>
  );
};

export default ScenarioLayout;
