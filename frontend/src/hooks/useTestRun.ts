import { ScenarioResult } from "@/interfaces/apiTest";
import { clientApiTestApi } from "@/services/client/clientApiTestApi";
import { clientReportApi } from "@/services/client/clientReportApi";
import { downloadFile } from "@/utils/downloadFile";
import { useEffect, useState, useCallback } from "react";
import { useTestResultStore } from "@/store/testResult";

export const useTestRun = (scenarioId: string) => {
  const [scenario, setScenario] = useState<Omit<ScenarioResult, "isSuccess">>({
    scenarioId: scenarioId,
    scenarioName: "",
    tcList: [],
  });
  const { setTestResult } = useTestResultStore();
  const [loadingStates, setLoadingStates] = useState({
    apiTestResult: false,
    scenarioResult: false,
    scenarioReport: false,
    testcaseReport: false,
  });

  const readApiTestScenarioResult = useCallback(async (scenarioId: string) => {
    try {
      setLoadingStates((prev) => ({ ...prev, scenarioResult: true }));
      const scenarioResult = await clientApiTestApi.readApiTestScenarioResult(
        scenarioId
      );
      setScenario(scenarioResult);
    } catch (error) {
      console.error(error);
    } finally {
      setLoadingStates((prev) => ({ ...prev, scenarioResult: false }));
    }
  }, []);

  useEffect(() => {
    readApiTestScenarioResult(scenarioId);
  }, [scenarioId, readApiTestScenarioResult]);

  const readApiTestResult = async () => {
    setLoadingStates((prev) => ({ ...prev, apiTestResult: true }));
    try {
      const testResult = await clientApiTestApi.readApiTestResult();
      setTestResult({
        scenarioList: testResult.scenarioList.map((scenario) => ({
          ...scenario,
          tcList: [],
        })),
      });
    } catch (error) {
      console.error(error);
    } finally {
      setLoadingStates((prev) => ({ ...prev, apiTestResult: false }));
    }
  };

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
    readApiTestScenarioResult,
    readApiTestResult,
    readTestcaseReport,
    readScenarioReport,
  };
};
