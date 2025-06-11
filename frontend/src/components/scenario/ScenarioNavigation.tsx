import useDeleteScenario from "@/hooks/scenario/deleteScenario";
import ActionButton from "../common/ActionButton";
import LinkButton from "../common/LinkButton";
import useUpdateScenario from "@/hooks/scenario/updateScenario";
import useCreateScenario from "@/hooks/scenario/createScenario";
import { ScenarioInfo } from "@/interfaces/scenario";
import { useRouter } from "next/navigation";
import { useProjectStore } from "@/store/projectStore";

const ScenarioNavigation = ({
  scenarioInfo,
}: {
  scenarioInfo: ScenarioInfo;
}) => {
  const router = useRouter();
  const { project } = useProjectStore();

  const { createScenario, isLoading: isCreatingScenario } = useCreateScenario(
    scenarioInfo.name,
    scenarioInfo.description,
    scenarioInfo.validation
  );
  const { updateScenario, isLoading: isUpdatingScenario } = useUpdateScenario(
    scenarioInfo.id,
    scenarioInfo.name,
    scenarioInfo.description,
    scenarioInfo.validation
  );
  const { deleteScenario, isLoading: isDeletingScenario } = useDeleteScenario(
    scenarioInfo.id
  );

  const handleDeleteScenario = async () => {
    const isSuccess = await deleteScenario();
    if (!isSuccess) return;
    if (project.scenarios.length === 0) {
      router.push(`/project/${project.id}/upload`);
    } else {
      router.push(`/project/${project.id}/scenario/${project.scenarios[0].id}`);
    }
  };

  const handleCreateScenario = async () => {
    const scenarioId = await createScenario();
    if (!scenarioId) return;
    router.push(`/project/${project.id}/scenario/${scenarioId}`);
  };

  return (
    <div className="flex gap-2">
      <LinkButton
        href={`/project/${project.id}/scenario/${scenarioInfo.id}/testcase/new`}
        color="bg-sky-400 hover:bg-sky-500"
        ariaLabel="TC 추가"
      >
        TC 추가
      </LinkButton>
      <ActionButton
        onClick={handleDeleteScenario}
        color="bg-red-500 hover:bg-red-600"
        isLoading={isDeletingScenario}
      >
        삭제
      </ActionButton>
      {scenarioInfo.id === "new" ? (
        <ActionButton
          onClick={handleCreateScenario}
          color="bg-green-500 hover:bg-green-600"
          isLoading={isCreatingScenario}
        >
          생성
        </ActionButton>
      ) : (
        <ActionButton
          onClick={updateScenario}
          color="bg-green-500 hover:bg-green-600"
          isLoading={isUpdatingScenario}
        >
          저장
        </ActionButton>
      )}
    </div>
  );
};

export default ScenarioNavigation;
