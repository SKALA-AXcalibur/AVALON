import Header from "@/components/common/Header";
import Sidebar from "@/components/common/Sidebar";
import TestcaseBox from "@/components/testcase/TestcaseBox";

const TestcasePage = () => {
  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header />
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
