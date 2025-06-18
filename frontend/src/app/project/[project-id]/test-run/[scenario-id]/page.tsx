import { TestRunBox } from "@/components/test-run/TestRunBox";
import { ErrorBox } from "@/components/common/ErrorBox";
import { clientApiTestApi } from "@/services/client/clientApiTestApi";

const TestRunPage = async ({
  params,
}: {
  params: { "project-id": string; "scenario-id": string };
}) => {
  const { "project-id": projectId, "scenario-id": scenarioId } = await params;

  try {
    const apiTestResult = await clientApiTestApi.readApiTestResult();

    return (
      <TestRunBox
        projectId={projectId}
        scenarioId={scenarioId}
        apiTestResult={apiTestResult}
      />
    );
  } catch (error) {
    console.error(error);
    return <ErrorBox error={error} />;
  }
};

export default TestRunPage;
