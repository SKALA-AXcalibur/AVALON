interface Scenario {
  id: string;
  name: string;
}

interface ScenarioInfo {
  id: string;
  name: string;
  graph: string;
  description: string;
  validation: string;
}

export type { Scenario, ScenarioInfo };
