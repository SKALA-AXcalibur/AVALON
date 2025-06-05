import Header from "@/components/common/Header";
import Sidebar from "@/components/common/Sidebar";
import ScenarioBox from "@/components/scenario/ScenarioBox";

const ScenarioDetailPage = () => {
  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header />
      <div className="flex flex-1">
        <Sidebar />
        <main className="flex-1 p-12">
          <ScenarioBox />
        </main>
      </div>
    </div>
  );
};

export default ScenarioDetailPage;
