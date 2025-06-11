import { ScenarioInfo } from "@/interfaces/scenario";

const ScenarioTitle = ({
  scenarioInfo,
  setScenarioInfo,
}: {
  scenarioInfo: ScenarioInfo;
  setScenarioInfo: (scenarioInfo: ScenarioInfo) => void;
}) => {
  return (
    <h2 className="text-xl font-bold text-slate-800">
      {scenarioInfo.id}
      <input
        type="text"
        value={scenarioInfo.name}
        onChange={(e) =>
          setScenarioInfo({ ...scenarioInfo, name: e.target.value })
        }
        className="rounded-lg ml-2 p-1"
        placeholder="시나리오 이름을 입력하세요"
      />
    </h2>
  );
};

export default ScenarioTitle;
