import TestRunSidebar from "@/components/test-run/TestRunSidebar";
import TestRunMainTitle from "@/components/test-run/TestRunMainTitle";
import TestRunTable from "@/components/test-run/TestRunTable";

const TestRunPage = async () => {
  return (
    <div className="flex flex-1">
      <TestRunSidebar />
      <main className="flex-1 p-12">
        <TestRunMainTitle />
        <TestRunTable />
      </main>
    </div>
  );
};

export default TestRunPage;
