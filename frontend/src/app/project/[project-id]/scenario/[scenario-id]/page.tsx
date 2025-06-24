import { ScenarioBox } from "@/components/scenario/ScenarioBox";

const ScenarioDetailPage = async ({
  params,
}: {
  params: { "project-id": string; "scenario-id": string };
}) => {
  const { "project-id": projectId, "scenario-id": scenarioId } = await params;

  return <ScenarioBox projectId={projectId} scenarioId={scenarioId} />;
};

export default ScenarioDetailPage;
