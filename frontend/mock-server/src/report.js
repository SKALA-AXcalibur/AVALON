import { getCurrentTime } from "./utils.js";
import fs from "fs";
import path from "path";

export const setupReportRoutes = (server, router) => {
  // 테스트시나리오 리포트 다운로드
  server.get("/api/report/v1/scenario", async (req, res) => {
    const avalon = req.cookies?.avalon;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    try {
      const currentTime = getCurrentTime();
      const reportPath = path.join(
        process.cwd(),
        "reports",
        "시나리오 리포트.csv",
      );

      // 파일을 버퍼로 읽어오기
      const fileBuffer = await fs.promises.readFile(reportPath);

      // 응답 헤더 설정
      res.set({
        "Content-Type": "application/octet-stream",
        "Content-Disposition": `attachment; filename="test-scenario-report-${currentTime}.csv"`,
        "Content-Length": fileBuffer.length,
        requestTime: currentTime,
      });

      // 버퍼를 blob으로 전송
      res.send(fileBuffer);
    } catch (err) {
      console.error("File read error:", err);
      res.status(500).json({ error: "Failed to read report file" });
    }
  });

  // 테스트케이스 리포트 다운로드
  server.get("/api/report/v1/testcase/:scenarioId", async (req, res) => {
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

    try {
      const currentTime = getCurrentTime();
      const reportPath = path.join(
        process.cwd(),
        "reports",
        "테스트케이스 리포트.csv",
      );

      // 파일을 버퍼로 읽어오기
      const fileBuffer = await fs.promises.readFile(reportPath);

      // 응답 헤더 설정
      res.set({
        "Content-Type": "application/octet-stream",
        "Content-Disposition": `attachment; filename="test-case-report-${scenarioId}-${currentTime}.csv"`,
        "Content-Length": fileBuffer.length,
        requestTime: currentTime,
      });

      // 버퍼를 blob으로 전송
      res.send(fileBuffer);
    } catch (err) {
      console.error("File read error:", err);
      res.status(500).json({ error: "Failed to read report file" });
    }
  });
};
