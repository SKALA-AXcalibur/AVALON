import { TestRunBox } from "@/components/test-run/TestRunBox";
import { apiTestApi } from "@/services/apiTest";

const TestRunPage = async ({
  params,
}: {
  params: { "project-id": string; "scenario-id": string };
}) => {
  const { "project-id": projectId, "scenario-id": scenarioId } = await params;

  try {
    const apiTestResult = await apiTestApi.readApiTestResult();

    return (
      <TestRunBox
        projectId={projectId}
        scenarioId={scenarioId}
        apiTestResult={apiTestResult}
      />
    );
  } catch (error) {
    console.error(error);
    return (
      <div className="flex flex-col items-center justify-center p-6">
        <h1 className="text-2xl font-bold text-red-600 mb-4">
          데이터를 가져오는데 실패했습니다
        </h1>
        <p className="text-slate-600">
          {error instanceof Error
            ? error.message
            : "알 수 없는 오류가 발생했습니다"}
        </p>
      </div>
    );
  }
};

export default TestRunPage;
