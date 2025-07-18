export const ERROR_MESSAGES = {
  PROJECT_AUTH: {
    LOGIN_FAILED:
      "로그인에 실패했습니다. 프로젝트 ID를 확인하거나 잠시 후 다시 시도해주세요.",
    DELETE_FAILED: "프로젝트 삭제에 실패했습니다.",
    INVALID_ID: "유효하지 않은 프로젝트 ID입니다.",
    NETWORK_ERROR: "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
    SERVER_ERROR: "서버 오류가 발생했습니다. 관리자에게 문의해주세요.",
    LOGOUT_FAILED: "로그아웃에 실패했습니다.",
  },

  ID_VALIDATION: {
    EMPTY: "입력해주세요.",
    LENGTH: "5자 이상 30자 이하여야 합니다.",
    FORMAT: "영문자와 숫자, -로만 구성되어야 합니다.",
  },

  SCENARIO: {
    READ_FAILED: "시나리오 정보를 불러오지 못했습니다.",
    CREATE_FAILED: "시나리오 생성에 실패했습니다.",
    UPDATE_FAILED: "시나리오 수정에 실패했습니다.",
    DELETE_FAILED: "시나리오 삭제에 실패했습니다.",
    PROJECT_READ_FAILED: "프로젝트 시나리오 조회에 실패했습니다.",
  },

  TESTCASE: {
    READ_FAILED: "테스트케이스 정보를 불러오지 못했습니다.",
    CREATE_FAILED: "테스트케이스 생성에 실패했습니다.",
    UPDATE_FAILED: "테스트케이스 수정에 실패했습니다.",
    DELETE_FAILED: "테스트케이스 삭제에 실패했습니다.",
    GENERATE_FAILED: "테스트케이스 생성에 실패했습니다.",
    API_LIST_READ_FAILED: "API 목록 조회에 실패했습니다.",
    PARAMS_READ_FAILED: "파라미터 조회에 실패했습니다.",
    SCENARIO_READ_FAILED: "시나리오 테스트케이스 조회에 실패했습니다.",
  },

  SPEC: {
    UPLOAD_FAILED: "파일 업로드에 실패했습니다.",
    ANALYZE_FAILED: "분석에 실패했습니다.",
  },

  REPORT: {
    SCENARIO_DOWNLOAD_FAILED: "시나리오 리포트 다운로드에 실패했습니다.",
    TESTCASE_DOWNLOAD_FAILED: "테스트케이스 리포트 다운로드에 실패했습니다.",
  },

  API_TEST: {
    RUN_FAILED: "API 테스트 실행에 실패했습니다.",
    RESULT_READ_FAILED: "API 테스트 결과 조회에 실패했습니다.",
    SCENARIO_RESULT_READ_FAILED:
      "시나리오 API 테스트 결과 조회에 실패했습니다.",
  },

  FILE_UPLOAD: {
    EMPTY_RESULT: "시나리오가 0개 생성되어 이동할 수 없습니다.",
    CREATION_ERROR: "시나리오 생성 중 오류가 발생했습니다.",
  },

  FILE_VALIDATION: {
    EMPTY: "파일을 선택해주세요.",
    MISSING: (fileType: string, words: readonly string[]) =>
      `${words.join(" ")} 파일이 필요합니다.`,
    DUPLICATE: (fileType: string, words: readonly string[]) =>
      `${words.join(" ")} 파일이 중복되었습니다.`,
    UNMATCHED: (fileNames: string[]) =>
      `다음 파일이 요구사항과 일치하지 않습니다: ${fileNames.join(", ")}`,
    COUNT_MISMATCH: (required: number, actual: number) =>
      `${required}개의 파일이 필요하지만 ${actual}개가 선택되었습니다.`,
    INVALID_TYPE: "지원하지 않는 파일 형식입니다.",
  },
};

export const SUCCESS_MESSAGES = {
  PROJECT_AUTH: {
    DELETE_SUCCESS: "프로젝트가 성공적으로 삭제되었습니다.",
    LOGIN_SUCCESS: "로그인이 완료되었습니다.",
  },

  SCENARIO: {
    CREATE_SUCCESS: "시나리오가 성공적으로 생성되었습니다.",
    UPDATE_SUCCESS: "시나리오가 성공적으로 수정되었습니다.",
    DELETE_SUCCESS: "시나리오가 성공적으로 삭제되었습니다.",
  },

  TESTCASE: {
    CREATE_SUCCESS: "테스트케이스가 성공적으로 생성되었습니다.",
    UPDATE_SUCCESS: "테스트케이스가 성공적으로 수정되었습니다.",
    DELETE_SUCCESS: "테스트케이스가 성공적으로 삭제되었습니다.",
  },

  FILE_UPLOAD: {
    UPLOAD_SUCCESS: "파일이 성공적으로 업로드되었습니다.",
    VALIDATION_SUCCESS: "파일 검증이 완료되었습니다.",
  },
};

export const INFO_MESSAGES = {
  SCENARIO: {
    CREATE_INFO: "시나리오가 생성될 예정입니다. 새로고침 버튼을 눌러주세요.",
    UPDATE_INFO: "시나리오가 수정될 예정입니다. 새로고침 버튼을 눌러주세요.",
  },
  TESTCASE: {
    CREATE_INFO:
      "테스트케이스가 생성될 예정입니다. 새로고침 버튼을 눌러주세요.",
  },
};

export const DELETE_MESSAGES = {
  PROJECT: "프로젝트를 삭제하시겠습니까?",
  SCENARIO: "시나리오를 삭제하시겠습니까?",
  TESTCASE: "테스트케이스를 삭제하시겠습니까?",
};
