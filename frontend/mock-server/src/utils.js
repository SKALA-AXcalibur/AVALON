// UUID v7 생성 함수 (간단한 버전)
export const generateUUIDv7 = () => {
  const timestamp = Date.now();
  const randomBytes =
    Math.random().toString(36).substring(2, 15) +
    Math.random().toString(36).substring(2, 15);
  return (timestamp.toString(16) + randomBytes)
    .padEnd(32, "0")
    .substring(0, 32);
};

// 현재 시간 ISO 형식
export const getCurrentTime = () => {
  return new Date().toISOString();
};

// 랜덤 시나리오 이름 생성
export const generateRandomName = () => {
  const nouns = [
    "Test",
    "Scenario",
    "Flow",
    "Process",
    "Workflow",
    "Sequence",
    "Chain",
    "Path",
  ];
  const randomNum = Math.floor(Math.random() * 1000);
  return `${nouns[Math.floor(Math.random() * nouns.length)]} ${randomNum}`;
};

// 랜덤 설명 생성
export const generateRandomDescription = () => {
  const descriptions = [
    "This scenario tests the basic functionality of the system",
    "A comprehensive test flow for critical operations",
    "End-to-end testing scenario for user interactions",
    "Performance testing scenario for system optimization",
    "Security validation scenario for data protection",
  ];
  return descriptions[Math.floor(Math.random() * descriptions.length)];
};

// 랜덤 검증 규칙 생성
export const generateRandomValidation = () => {
  const validations = [
    "Response time < 200ms",
    "Success rate > 99%",
    "Error rate < 0.1%",
    "Data consistency check",
    "State transition validation",
  ];
  return validations[Math.floor(Math.random() * validations.length)];
};

// 랜덤 테스트케이스 설명 생성
export const generateRandomTestCaseDescription = () => {
  const descriptions = [
    "Verify the basic functionality of the feature",
    "Test the error handling mechanism",
    "Validate the data processing flow",
    "Check the boundary conditions",
    "Test the integration with other components",
  ];
  return descriptions[Math.floor(Math.random() * descriptions.length)];
};

// 랜덤 전제조건 생성
export const generateRandomPrecondition = () => {
  const preconditions = [
    "시스템이 정상적으로 실행 중인 상태",
    "사용자가 로그인한 상태",
    "데이터베이스가 초기화된 상태",
    "네트워크 연결이 안정적인 상태",
    "필요한 리소스가 할당된 상태",
  ];
  return preconditions[Math.floor(Math.random() * preconditions.length)];
};

// 랜덤 기대결과 생성
export const generateRandomExpectedResult = () => {
  const expectedResults = [
    "성공적으로 데이터가 저장됨",
    "에러 메시지가 정확히 표시됨",
    "요청한 데이터가 정확히 반환됨",
    "시스템이 정상적으로 응답함",
    "데이터가 올바르게 업데이트됨",
  ];
  return expectedResults[Math.floor(Math.random() * expectedResults.length)];
};

export const generateRandomTestStatus = () => {
  const statuses = [2, 3, 4, 5];
  return statuses[Math.floor(Math.random() * statuses.length)];
};

export const generateRandomTestDataCategory = () => {
  const categories = ["path/query", "request", "response"];
  return categories[Math.floor(Math.random() * categories.length)];
};

export const generateRandomTestDataKoName = () => {
  const koNames = ["테스트", "테스트2", "테스트3"];
  return koNames[Math.floor(Math.random() * koNames.length)];
};

export const generateRandomTestDataName = () => {
  const names = ["test", "test2", "test3"];
  return names[Math.floor(Math.random() * names.length)];
};

export const generateRandomTestDataContext = () => {
  const contexts = ["path", "query", "body", "header"];
  return contexts[Math.floor(Math.random() * contexts.length)];
};

export const generateRandomTestDataType = () => {
  const types = ["string", "number", "boolean", "array", "object"];
  return types[Math.floor(Math.random() * types.length)];
};

export const generateRandomTestDataLength = () => {
  const lengths = [10, 20, 30];
  return lengths[Math.floor(Math.random() * lengths.length)];
};

export const generateRandomTestDataFormat = () => {
  const formats = ["text", "json", "xml"];
  return formats[Math.floor(Math.random() * formats.length)];
};

export const generateRandomTestDataDefaultValue = () => {
  const defaultValues = ["test", "test2", "test3"];
  return defaultValues[Math.floor(Math.random() * defaultValues.length)];
};

export const generateRandomTestDataRequired = () => {
  const required = [true, false];
  return required[Math.floor(Math.random() * required.length)];
};

export const generateRandomTestDataId = () => {
  const parents = [1, 2, 3];
  return parents[Math.floor(Math.random() * parents.length)];
};

export const generateRandomTestDataDesc = () => {
  const descs = ["테스트", "테스트2", "테스트3"];
  return descs[Math.floor(Math.random() * descs.length)];
};

export const generateRandomTestDataValue = () => {
  const values = ["test", "test2", "test3"];
  return values[Math.floor(Math.random() * values.length)];
};

// 랜덤 테스트케이스 상태 생성
export const generateRandomTestCaseStatus = () => {
  const statuses = ["PASS", "FAIL", null];
  return statuses[Math.floor(Math.random() * statuses.length)];
};
