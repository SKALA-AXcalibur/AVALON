import ScenarioBox from "@/components/scenario/ScenarioBox";

const ScenarioDetailPage = async ({
  params,
}: {
  params: { "project-id": string; "scenario-id": string };
}) => {
  const { "scenario-id": scenarioId } = await params;

  return <ScenarioBox scenarioId={scenarioId} />;
};

export default ScenarioDetailPage;
