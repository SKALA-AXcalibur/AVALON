import {
  generateRandomTestCaseDescription,
  generateRandomPrecondition,
  generateRandomExpectedResult,
  generateRandomTestStatus,
  generateRandomTestDataCategory,
  generateRandomTestDataId,
  generateRandomTestDataKoName,
  generateRandomTestDataName,
  generateRandomTestDataContext,
  generateRandomTestDataType,
  generateRandomTestDataLength,
  generateRandomTestDataFormat,
  generateRandomTestDataDefaultValue,
  generateRandomTestDataRequired,
  generateRandomTestDataDesc,
  generateRandomTestDataValue,
} from "./utils.js";

export const CreateTestData = () => {
  return {
    paramId: generateRandomTestDataId(),
    category: generateRandomTestDataCategory(),
    koName: generateRandomTestDataKoName(),
    name: generateRandomTestDataName(),
    context: generateRandomTestDataContext(),
    type: generateRandomTestDataType(),
    length: generateRandomTestDataLength(),
    format: generateRandomTestDataFormat(),
    defaultValue: generateRandomTestDataDefaultValue(),
    required: generateRandomTestDataRequired(),
    parent: generateRandomTestDataName(),
    desc: generateRandomTestDataDesc(),
    value: generateRandomTestDataValue(),
  };
};

// 테스트케이스 생성 함수
const createTestCase = () => {
  return {
    tcId: `testcase-${Date.now()}-${Math.random()
      .toString(36)
      .substring(2, 8)}`,
    precondition: generateRandomPrecondition(),
    description: generateRandomTestCaseDescription(),
    expectedResult: generateRandomExpectedResult(),
    status: generateRandomTestStatus(),
    testDataList: Array.from({ length: 3 }, () => CreateTestData()),
  };
};

export const setupTestcaseRoutes = (server, router) => {
  // 모든 시나리오에 대해 TC 생성
  server.post("/api/tc/v1/", (req, res) => {
    const avalon = req.cookies?.avalon;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    const updatedScenarioList = project.scenarioList.map((scenario) => {
      const testcaseList = Array.from({ length: 3 }, () => createTestCase());
      return {
        ...scenario,
        testcaseList,
      };
    });

    db.get("projects")
      .find({ avalon: avalon })
      .assign({ scenarioList: updatedScenarioList })
      .write();

    res.json({ message: "Test cases created successfully" });
  });

  // 시나리오별 TC 목록 조회
  server.get("/api/tc/v1/scenario/:scenarioId", (req, res) => {
    const avalon = req.cookies?.avalon;
    const { scenarioId } = req.params;
    const { offset = 0, query = 10 } = req.query;

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

    const tcList = scenario.testcaseList.slice(
      parseInt(offset, 10),
      parseInt(offset, 10) + parseInt(query, 10)
    );
    const tcTotal = scenario.testcaseList.length;

    res.json({
      tcList: tcList.map((tc) => ({
        tcId: tc.tcId,
      })),
      tcTotal,
    });
  });

  // TC 상세 정보 조회
  server.get("/api/tc/v1/:tcId", (req, res) => {
    const avalon = req.cookies?.avalon;
    const { tcId } = req.params;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    let testcase = null;
    for (const scenario of project.scenarioList) {
      const found = scenario.testcaseList.find((tc) => tc.tcId === tcId);
      if (found) {
        testcase = found;
        break;
      }
    }

    if (!testcase) {
      return res.status(404).json({ error: "Test case not found" });
    }

    res.json({
      tcId: testcase.tcId,
      precondition: testcase.precondition,
      description: testcase.description,
      expectedResult: testcase.expectedResult,
      status: testcase.status,
      testDataList: testcase.testDataList,
    });
  });

  // TC 수정
  server.put("/api/tc/v1/:tcId", (req, res) => {
    const avalon = req.cookies?.avalon;
    const { tcId } = req.params;
    const { precondition, description, expectedResult, status, testDataList } =
      req.body;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    let updated = false;
    const updatedScenarioList = project.scenarioList.map((scenario) => {
      const updatedTestcaseList = scenario.testcaseList.map((tc) => {
        if (tc.tcId === tcId) {
          updated = true;
          return {
            ...tc,
            precondition,
            description,
            expectedResult,
            status,
            testDataList: tc.testDataList.map((data) => {
              const matchingData = testDataList.find(
                (newData) => data.paramId === newData.paramId
              );
              if (matchingData) {
                return { ...data, value: matchingData.value };
              }
              return data;
            }),
          };
        }
        return tc;
      });
      return { ...scenario, testcaseList: updatedTestcaseList };
    });

    if (!updated) {
      return res.status(404).json({ error: "Test case not found" });
    }

    db.get("projects")
      .find({ avalon: avalon })
      .assign({ scenarioList: updatedScenarioList })
      .write();

    res.json({ message: "Test case updated successfully" });
  });

  // TC 삭제
  server.delete("/api/tc/v1/:tcId", (req, res) => {
    const avalon = req.cookies?.avalon;
    const { tcId } = req.params;

    if (!avalon) {
      return res.status(401).json({ error: "Authentication required" });
    }

    const db = router.db;
    const project = db.get("projects").find({ avalon: avalon }).value();

    if (!project) {
      return res.status(404).json({ error: "Project not found" });
    }

    let deleted = false;
    const updatedScenarioList = project.scenarioList.map((scenario) => {
      const updatedTestcaseList = scenario.testcaseList.filter(
        (tc) => tc.tcId !== tcId
      );
      if (updatedTestcaseList.length !== scenario.testcaseList.length) {
        deleted = true;
      }
      return { ...scenario, testcaseList: updatedTestcaseList };
    });

    if (!deleted) {
      return res.status(404).json({ error: "Test case not found" });
    }

    db.get("projects")
      .find({ avalon: avalon })
      .assign({ scenarioList: updatedScenarioList })
      .write();

    res.json({ message: "Test case deleted successfully" });
  });

  // API 조회

  // API 선택

  // TC 추가
  server.post("/api/tc/v1/scenario/:scenarioId", (req, res) => {
    const avalon = req.cookies?.avalon;
    const { scenarioId } = req.params;
    const { precondition, description, expectedResult, testDataList } =
      req.body;

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

    const newTestCase = {
      tcId: `testcase-${Date.now()}-${Math.random()
        .toString(36)
        .substring(2, 8)}`,
      precondition,
      description,
      expectedResult,
      testDataList:
        typeof testDataList === "string"
          ? JSON.parse(testDataList)
          : testDataList,
    };

    const updatedScenarioList = project.scenarioList.map((s) => {
      if (s.id === scenarioId) {
        return {
          ...s,
          testcaseList: [...s.testcaseList, newTestCase],
        };
      }
      return s;
    });

    db.get("projects")
      .find({ avalon: avalon })
      .assign({ scenarioList: updatedScenarioList })
      .write();

    res.json({ tcId: newTestCase.tcId });
  });
};
