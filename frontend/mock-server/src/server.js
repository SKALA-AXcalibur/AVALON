import jsonServer from "json-server";
import cookieParser from "cookie-parser";
import { fileURLToPath } from "url";
import { dirname, join } from "path";
import { setupLoginRoutes } from "./login.js";
import { setupScenarioRoutes } from "./scenario.js";
import { setupApiListRoutes } from "./apiList.js";
import { setupSpecRoutes } from "./spec.js";
import { setupReportRoutes } from "./report.js";
import { setupTestcaseRoutes } from "./testcase.js";
import { setupApiTestRoutes } from "./apiTest.js";

const __filename = fileURLToPath(import.meta.url);
const __dirname = dirname(__filename);

const server = jsonServer.create();
const middlewares = jsonServer.defaults();

// 기본 미들웨어 설정
server.use(middlewares);
server.use(jsonServer.bodyParser);
server.use(cookieParser());

// 통합된 데이터베이스 라우터 생성
const router = jsonServer.router(join(__dirname, "../db.json"));

// API 라우트 설정
setupLoginRoutes(server, router);
setupScenarioRoutes(server, router);
setupApiListRoutes(server, router);
setupSpecRoutes(server, router);
setupReportRoutes(server, router);
setupTestcaseRoutes(server, router);
setupApiTestRoutes(server, router);

// 기본 json-server 라우터 사용
server.use(router);

const PORT = 3001;
server.listen(PORT, () => {
  console.log(`Mock Server is running on http://localhost:${PORT}`);
});
