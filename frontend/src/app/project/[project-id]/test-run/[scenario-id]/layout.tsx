import { TestRunSidebar } from "@/components/test-run/TestRunSidebar";

const TestRunLayout = async ({
  children,
  params,
}: {
  children: React.ReactNode;
  params: Promise<{
    "project-id": string;
    "scenario-id": string;
  }>;
}) => {
  const { "project-id": projectId } = await params;

  return (
    <div className="flex flex-1">
      <TestRunSidebar projectId={projectId} />
      <main className="flex-1 p-12 overflow-y-auto h-[calc(100vh-84px)]">
        {children}
      </main>
    </div>
  );
};

export default TestRunLayout;
