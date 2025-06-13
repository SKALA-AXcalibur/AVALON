import ActionButton from "../common/ActionButton";
import LinkButton from "../common/LinkButton";
import { useParams, useRouter } from "next/navigation";
import { TestcaseInfo } from "@/interfaces/testcase";
import useDeleteTestcase from "@/hooks/testcase/deleteTestcase";
import useUpdateTestcase from "@/hooks/testcase/updateTestcase";
import useCreateTestcase from "@/hooks/testcase/createTestcase";

const TestcaseNavigation = ({
  testcaseInfo,
}: {
  testcaseInfo: TestcaseInfo;
}) => {
  const router = useRouter();
  const { "project-id": projectId, "scenario-id": scenarioId } = useParams();
  const { createTestcase, isLoading: isCreatingTestcase } = useCreateTestcase();
  const { updateTestcase, isLoading: isUpdatingTestcase } = useUpdateTestcase();
  const { deleteTestcase, isLoading: isDeletingTestcase } = useDeleteTestcase();

  const handleCreateTestcase = async () => {
    const tcId = await createTestcase(scenarioId as string, testcaseInfo);
    if (!tcId) return;
    router.push(
      `/project/${projectId}/scenario/${scenarioId}/testcase/${tcId}`
    );
  };

  const handleUpdateTestcase = async () => {
    await updateTestcase(testcaseInfo);
  };

  const handleDeleteTestcase = async () => {
    await deleteTestcase(testcaseInfo.tcId);
    router.push(`/project/${projectId}/scenario/${scenarioId}`);
  };

  return (
    <div className="flex gap-2">
      <LinkButton
        href={`/project/${projectId}/scenario/${scenarioId}/testcase/new`}
        color="bg-sky-400 hover:bg-sky-500"
        ariaLabel="TC 추가"
      >
        TC 추가
      </LinkButton>
      <ActionButton
        onClick={handleDeleteTestcase}
        color="bg-red-500 hover:bg-red-600"
        isLoading={isDeletingTestcase}
      >
        삭제
      </ActionButton>
      {scenarioId === "new" ? (
        <ActionButton
          onClick={handleCreateTestcase}
          color="bg-green-500 hover:bg-green-600"
          isLoading={isCreatingTestcase}
        >
          생성
        </ActionButton>
      ) : (
        <ActionButton
          onClick={handleUpdateTestcase}
          color="bg-green-500 hover:bg-green-600"
          isLoading={isUpdatingTestcase}
        >
          저장
        </ActionButton>
      )}
    </div>
  );
};

export default TestcaseNavigation;
