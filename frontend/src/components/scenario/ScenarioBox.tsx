"use client";
import { useEffect, useState } from "react";
import ScenarioGraph from "./ScenarioGraph";
import ScenarioDesc from "./ScenarioDescription";
import ScenarioValidation from "./ScenarioValidation";
import ScenarioTitle from "./ScenarioTitle";
import ScenarioNavigation from "./ScenarioNavigation";
import useReadScenario from "@/hooks/scenario/readScenario";
import { ScenarioInfo } from "@/interfaces/scenario";

const ScenarioBox = ({ scenarioId }: { scenarioId: string }) => {
  const { readScenario, isLoading } = useReadScenario(scenarioId);
  const [scenarioInfo, setScenarioInfo] = useState<ScenarioInfo>({
    id: scenarioId,
    name: "",
    graph: "",
    description: "",
    validation: "",
  });

  useEffect(() => {
    const fetchScenarioInfo = async () => {
      const scenarioInfo = await readScenario();
      if (!scenarioInfo) return;
      setScenarioInfo(scenarioInfo);
    };
    fetchScenarioInfo();
  }, [scenarioId]);

  return (
    <>
      {isLoading ? (
        <div className="flex items-center justify-center h-full">
          <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-sky-500"></div>
        </div>
      ) : (
        <>
          <div className="flex items-center justify-between mb-4">
            <ScenarioTitle
              scenarioInfo={scenarioInfo}
              setScenarioInfo={setScenarioInfo}
            />
            <ScenarioNavigation scenarioInfo={scenarioInfo} />
          </div>
          <ScenarioGraph graph={scenarioInfo.graph} />
          <div className="min-h-[200px] flex gap-8">
            <ScenarioDesc
              scenarioInfo={scenarioInfo}
              setScenarioInfo={setScenarioInfo}
            />
            <ScenarioValidation
              scenarioInfo={scenarioInfo}
              setScenarioInfo={setScenarioInfo}
            />
          </div>
        </>
      )}
    </>
  );
};

export default ScenarioBox;
