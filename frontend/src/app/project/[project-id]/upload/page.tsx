import Header from "@/components/common/header/Header";
import UploadBox from "@/components/upload/UploadBox";

const UploadPage = async ({ params }: { params: { "project-id": string } }) => {
  const { "project-id": projectId } = await params;

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header projectId={projectId} />
      <main className="flex-1 flex flex-col items-center justify-center">
        <UploadBox projectId={projectId} />
      </main>
    </div>
  );
};

export default UploadPage;
