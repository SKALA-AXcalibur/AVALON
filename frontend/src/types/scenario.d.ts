import { Scenario, ScenarioInfo } from "@/interfaces/scenario";

export type createScenarioRequest = Pick<
  ScenarioInfo,
  "name" | "description" | "validation"
>;

export type createScenarioResponse = Pick<ScenarioInfo, "id">;

export type readScenarioResponse = ScenarioInfo;

export type updateScenarioRequest = Pick<
  ScenarioInfo,
  "name" | "description" | "validation"
>;

export type readProjectScenariosResponse = {
  scenarioList: Pick<Scenario, "id" | "name">[];
  total: number;
};
