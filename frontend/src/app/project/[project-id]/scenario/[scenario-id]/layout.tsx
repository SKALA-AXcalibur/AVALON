import { Sidebar } from "@/components/common/Sidebar";

const ScenarioLayout = async ({
  children,
  params,
}: {
  children: React.ReactNode;
  params: {
    "project-id": string;
    "scenario-id": string;
  };
}) => {
  const { "project-id": projectId, "scenario-id": scenarioId } = await params;

  return (
    <div className="flex flex-1">
      <Sidebar projectId={projectId} scenarioId={scenarioId} />
      <main className="flex-1 p-12 overflow-y-auto h-[calc(100vh-84px)]">
        {children}
      </main>
    </div>
  );
};

export default ScenarioLayout;
