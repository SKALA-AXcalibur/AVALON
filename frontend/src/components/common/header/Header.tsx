import Navigation from "./Navigation";

const Header = ({ projectId }: { projectId: string }) => {
  return (
    <header className="w-full border-b border-slate-200 py-4 px-4 md:px-8 bg-white">
      <div className="flex items-center justify-between">
        <h1 className="text-xl md:text-2xl font-bold text-slate-800 truncate">
          {projectId}
        </h1>
        <Navigation projectId={projectId} />
      </div>
    </header>
  );
};

export default Header;
