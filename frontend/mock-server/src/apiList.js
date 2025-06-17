export const setupApiListRoutes = (server, router) => {
  server.post("/api/list/v1/generate/", (req, res) => {
    const avalon = req.cookies?.avalon;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;

    // 프로젝트 찾기
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    res.json({ message: "API list generation completed" });
  });
};
