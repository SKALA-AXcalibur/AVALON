import Header from "@/components/common/header/Header";
import Sidebar from "@/components/common/Sidebar";
import ScenarioBox from "@/components/scenario/ScenarioBox";

const ScenarioDetailPage = async ({
  params,
}: {
  params: { "project-id": string; "scenario-id": string };
}) => {
  const { "project-id": projectId, "scenario-id": scenarioId } = await params;

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header projectId={projectId} />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-12">
          <ScenarioBox scenarioId={scenarioId} />
        </main>
      </div>
    </div>
  );
};

export default ScenarioDetailPage;
