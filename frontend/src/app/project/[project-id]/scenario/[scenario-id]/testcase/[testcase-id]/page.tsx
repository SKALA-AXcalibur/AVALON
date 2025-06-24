import { TestcaseBox } from "@/components/testcase/TestcaseBox";

const TestcasePage = async ({
  params,
}: {
  params: Promise<{
    "project-id": string;
    "scenario-id": string;
    "testcase-id": string;
  }>;
}) => {
  const {
    "project-id": projectId,
    "scenario-id": scenarioId,
    "testcase-id": testcaseId,
  } = await params;
  return (
    <TestcaseBox
      projectId={projectId}
      scenarioId={scenarioId}
      testcaseId={testcaseId}
    />
  );
};

export default TestcasePage;
