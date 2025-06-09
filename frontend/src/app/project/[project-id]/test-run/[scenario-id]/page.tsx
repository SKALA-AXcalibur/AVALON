import Header from "@/components/common/header/Header";
import TestRunSidebar from "@/components/test-run/TestRunSidebar";
import TestRunMainTitle from "@/components/test-run/TestRunMainTitle";
import TestRunTable from "@/components/test-run/TestRunTable";

const TestRunPage = async ({
  params,
}: {
  params: { "project-id": string; "scenario-id": string };
}) => {
  const { "project-id": projectId } = await params;

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header projectId={projectId} />
      <div className="flex flex-1">
        <TestRunSidebar />
        <main className="flex-1 p-12">
          <TestRunMainTitle />
          <TestRunTable />
        </main>
      </div>
    </div>
  );
};

export default TestRunPage;
