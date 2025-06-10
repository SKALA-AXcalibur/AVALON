import useDeleteScenario from "@/hooks/scenario/deleteScenario";
import ActionButton from "../common/ActionButton";
import LinkButton from "../common/LinkButton";
import useUpdateScenario from "@/hooks/scenario/updateScenario";
import useCreateScenario from "@/hooks/scenario/createScenario";
import { ScenarioInfo } from "@/interfaces/scenario";

const ScenarioNavigation = ({
  scenarioInfo,
}: {
  scenarioInfo: ScenarioInfo;
}) => {
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

  return (
    <div className="flex gap-2">
      <LinkButton
        href={`/scenario/${scenarioInfo.id}/tc/new`}
        color="bg-sky-400 hover:bg-sky-500"
        ariaLabel="TC 추가"
      >
        TC 추가
      </LinkButton>
      <ActionButton
        onClick={deleteScenario}
        color="bg-red-500 hover:bg-red-600"
        isLoading={isDeletingScenario}
      >
        삭제
      </ActionButton>
      {scenarioInfo.id === "new" ? (
        <ActionButton
          onClick={createScenario}
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
