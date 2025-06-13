import Header from "@/components/common/header/Header";

const ScenarioLayout = async ({
  children,
  params,
}: {
  children: React.ReactNode;
  params: {
    "project-id": string;
  };
}) => {
  const { "project-id": projectId } = await params;

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header projectId={projectId} />
      {children}
    </div>
  );
};

export default ScenarioLayout;
