import { ScenarioInfo } from "@/interfaces/scenario";

const ScenarioValidation = ({
  scenarioInfo,
  setScenarioInfo,
}: {
  scenarioInfo: ScenarioInfo;
  setScenarioInfo: (scenarioInfo: ScenarioInfo) => void;
}) => {
  return (
    <div className="bg-slate-50 border border-slate-200 rounded-lg p-6 flex-1">
      <h3 className="font-medium text-slate-700 mb-2">검증포인트</h3>
      <div className="h-[200px] pr-2">
        <textarea
          value={scenarioInfo.validation}
          onChange={(e) =>
            setScenarioInfo({ ...scenarioInfo, validation: e.target.value })
          }
          className="w-full h-full p-2 border border-slate-300 rounded-lg resize-none"
          placeholder="시나리오에 대한 상세 설명을 입력하세요"
        />
      </div>
    </div>
  );
};

export default ScenarioValidation;
