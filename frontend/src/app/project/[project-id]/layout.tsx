import { Navigation } from "@/components/common/Navigation";

const ScenarioLayout = async ({
  children,
  params,
}: {
  children: React.ReactNode;
  params: Promise<{
    "project-id": string;
  }>;
}) => {
  const { "project-id": projectId } = await params;

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <header className="w-full border-b border-slate-200 py-4 px-4 md:px-8 bg-white">
        <div className="flex items-center justify-between">
          <h1 className="text-xl md:text-2xl font-bold text-slate-800 truncate">
            {projectId}
          </h1>
          <Navigation />
        </div>
      </header>
      {children}
    </div>
  );
};

export default ScenarioLayout;
