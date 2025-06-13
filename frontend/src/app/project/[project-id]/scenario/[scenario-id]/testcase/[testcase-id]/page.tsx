import TestcaseBox from "@/components/testcase/TestcaseBox";

const TestcasePage = async ({
  params,
}: {
  params: {
    "project-id": string;
    "scenario-id": string;
    "testcase-id": string;
  };
}) => {
  const { "testcase-id": testcaseId } = await params;
  return <TestcaseBox testcaseId={testcaseId} />;
};

export default TestcasePage;
