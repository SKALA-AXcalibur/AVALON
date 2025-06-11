import { Scenario, ScenarioInfo } from "@/interfaces/scenario";

type createScenarioRequest = Pick<
  ScenarioInfo,
  "name" | "description" | "validation"
>;

type createScenarioResponse = Pick<ScenarioInfo, "id">;

type readScenarioResponse = ScenarioInfo;

type updateScenarioRequest = Pick<
  ScenarioInfo,
  "name" | "description" | "validation"
>;

type readProjectScenariosResponse = {
  scenarioList: Pick<Scenario, "id" | "name">[];
  total: number;
};

export type {
  createScenarioRequest,
  createScenarioResponse,
  readScenarioResponse,
  updateScenarioRequest,
  readProjectScenariosResponse,
};
