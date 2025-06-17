import { getCurrentTime } from "./utils.js";
import path from "path";

export const setupReportRoutes = (server, router) => {
  // 테스트시나리오 리포트 다운로드
  server.get("/api/report/v1/scenario", (req, res) => {
    const avalon = req.cookies?.avalon;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    const currentTime = getCurrentTime();
    const reportPath = path.join(
      process.cwd(),
      "reports",
      "시나리오 리포트.csv"
    );

    res.set("requestTime", currentTime);
    res.download(
      reportPath,
      `test-scenario-report-${currentTime}.csv`,
      (err) => {
        if (err) {
          res.status(500).json({ error: "Failed to download report file" });
        }
      }
    );
  });

  // 테스트케이스 리포트 다운로드
  server.get("/api/report/v1/testcase/:scenarioId", (req, res) => {
    const avalon = req.cookies?.avalon;
    const { scenarioId } = req.params;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    const scenario = project.scenarioList.find((s) => s.id === scenarioId);

    if (!scenario) {
      return res.status(404).json({ error: "Scenario not found" });
    }

    const currentTime = getCurrentTime();
    const reportPath = path.join(
      process.cwd(),
      "reports",
      "테스트케이스 리포트.csv"
    );

    res.set("requestTime", currentTime);
    res.download(
      reportPath,
      `test-case-report-${scenarioId}-${currentTime}.csv`,
      (err) => {
        if (err) {
          res.status(500).json({ error: "Failed to download report file" });
        }
      }
    );
  });
};
