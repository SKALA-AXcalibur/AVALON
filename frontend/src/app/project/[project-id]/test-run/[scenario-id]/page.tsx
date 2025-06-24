import { TestRunBox } from "@/components/test-run/TestRunBox";

const TestRunPage = async ({
  params,
}: {
  params: { "project-id": string; "scenario-id": string };
}) => {
  const { "scenario-id": scenarioId } = await params;

  return <TestRunBox scenarioId={scenarioId} />;
};

export default TestRunPage;
