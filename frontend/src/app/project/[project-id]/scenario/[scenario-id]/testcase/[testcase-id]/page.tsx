import Header from "@/components/common/header/Header";
import Sidebar from "@/components/common/Sidebar";
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
  const { "project-id": projectId } = await params;

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header projectId={projectId} />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-12">
          <TestcaseBox />
        </main>
      </div>
    </div>
  );
};

export default TestcasePage;
