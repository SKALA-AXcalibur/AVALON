import { ScenarioResult } from "@/interfaces/apiTest";
import { clientApiTestApi } from "@/services/client/clientApiTestApi";
import { clientReportApi } from "@/services/client/clientReportApi";
import { downloadFile } from "@/utils/downloadFile";
import { useEffect, useState } from "react";

export const useTestRun = (scenarioId: string) => {
  const [scenario, setScenario] = useState<Omit<ScenarioResult, "isSuccess">>({
    scenarioId: scenarioId,
    scenarioName: "",
    tcList: [],
  });
  const [loadingStates, setLoadingStates] = useState({
    scenarioResult: false,
    scenarioReport: false,
    testcaseReport: false,
  });

  useEffect(() => {
    const fetchScenarioResult = async () => {
      try {
        setLoadingStates((prev) => ({ ...prev, scenarioResult: true }));
        const scenarioResult =
          await clientApiTestApi.readApiTestScenarioResult(scenarioId);
        setScenario(scenarioResult);
      } catch (error) {
        console.error(error);
      } finally {
        setLoadingStates((prev) => ({ ...prev, scenarioResult: false }));
      }
    };
    fetchScenarioResult();
  }, [scenarioId]);

  const readScenarioReport = async () => {
    setLoadingStates((prev) => ({ ...prev, scenarioReport: true }));
    try {
      const response = await clientReportApi.readScenarioReport();
      downloadFile(response, "scenario-report.csv");
    } catch (error) {
      console.error("Failed to download scenario report:", error);
    } finally {
      setLoadingStates((prev) => ({ ...prev, scenarioReport: false }));
    }
  };

  const readTestcaseReport = async () => {
    setLoadingStates((prev) => ({ ...prev, testcaseReport: true }));
    try {
      const response = await clientReportApi.readTestcaseReport(scenarioId);
      downloadFile(response, `testcase-report-${scenarioId}.csv`);
    } catch (error) {
      console.error("Failed to download testcase report:", error);
    } finally {
      setLoadingStates((prev) => ({ ...prev, testcaseReport: false }));
    }
  };

  return {
    scenario,
    loadingStates,
    readTestcaseReport,
    readScenarioReport,
  };
};
