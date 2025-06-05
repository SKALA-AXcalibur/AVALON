const jsonServer = require("json-server");
const server = jsonServer.create();
const router = jsonServer.router("./login/db.json");
const middlewares = jsonServer.defaults();

// UUID v7 생성 함수 (간단한 버전)
function generateUUIDv7() {
  const timestamp = Date.now();
  const randomBytes =
    Math.random().toString(36).substring(2, 15) +
    Math.random().toString(36).substring(2, 15);
  return (timestamp.toString(16) + randomBytes)
    .padEnd(32, "0")
    .substring(0, 32);
}

// 현재 시간 ISO 형식
function getCurrentTime() {
  return new Date().toISOString();
}

server.use(middlewares);
server.use(jsonServer.bodyParser);

// 커스텀 프로젝트 생성 API
server.post("/api/project/v1/", (req, res) => {
  const { projectId } = req.body;

  if (!projectId) {
    return res.status(400).json({ error: "projectId is required" });
  }

  // 기존 프로젝트 확인
  const db = router.db;
  const existingProject = db
    .get("projects")
    .find({ projectId: projectId })
    .value();

  const currentTime = getCurrentTime();
  const avalon = generateUUIDv7();

  // 응답 헤더 설정
  res.set("requestTime", currentTime);
  res.cookie("avalon", avalon, {
    httpOnly: true,
    secure: false, // 개발환경에서는 false
    sameSite: "lax",
  });

  if (existingProject) {
    // 기존 프로젝트 ID 반환
    return res.json({
      projectId: existingProject.projectId,
      message: "Existing project found",
    });
  } else {
    // 새로운 프로젝트 생성
    const newProject = {
      id: Date.now(), // 간단한 ID 생성
      projectId: projectId,
      createdAt: currentTime,
    };

    db.get("projects").push(newProject).write();

    return res.json({
      projectId: projectId,
      message: "New project created",
    });
  }
});

// 커스텀 프로젝트 삭제 API
server.delete("/api/project/v1/:projectId", (req, res) => {
  const { projectId } = req.params;
  const db = router.db;
  const projects = db.get("projects");

  // 프로젝트 존재 여부 확인
  const project = projects.find({ projectId }).value();

  if (!project) {
    return res.status(404).json({ error: "Project not found" });
  }

  // 프로젝트 삭제
  projects.remove({ projectId }).write();

  // 응답 헤더 설정
  res.set("requestTime", getCurrentTime());

  return res.status(200).json({
    message: "Project deleted successfully",
    projectId: projectId,
  });
});

// 기본 json-server 라우터 사용
server.use(router);

const PORT = 3001;
server.listen(PORT, () => {
  console.log(`JSON Server is running on http://localhost:${PORT}`);
  console.log(
    `Project API endpoint: POST http://localhost:${PORT}/api/project/v1/`
  );
  console.log(
    `Project Delete API endpoint: DELETE http://localhost:${PORT}/api/project/v1/:projectId`
  );
});
