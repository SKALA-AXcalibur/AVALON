export const ERROR_MESSAGES = {
  PROJECT_AUTH: {
    LOGIN_FAILED:
      "로그인에 실패했습니다. 프로젝트 ID를 확인하거나 잠시 후 다시 시도해주세요.",
    DELETE_FAILED: "프로젝트 삭제에 실패했습니다.",
    INVALID_ID: "유효하지 않은 프로젝트 ID입니다.",
    NETWORK_ERROR: "네트워크 오류가 발생했습니다. 잠시 후 다시 시도해주세요.",
    SERVER_ERROR: "서버 오류가 발생했습니다. 관리자에게 문의해주세요.",
  },

  ID_VALIDATION: {
    EMPTY: "ID를 입력해주세요.",
    LENGTH: "ID는 5자 이상 30자 이하여야 합니다.",
    FORMAT: "ID는 영문자와 숫자, -로만 구성되어야 합니다.",
  },
};

export const SUCCESS_MESSAGES = {
  PROJECT_AUTH: {
    DELETE_SUCCESS: "프로젝트가 성공적으로 삭제되었습니다.",
    LOGIN_SUCCESS: "로그인이 완료되었습니다.",
  },
};

export const FILE_UPLOAD_ERROR_MESSAGE = {
  EMPTY: "시나리오가 0개 생성되어 이동할 수 없습니다.",
  ERROR: "시나리오 생성 중 오류가 발생했습니다.",
};

export const FILE_VALIDATION_ERROR_MESSAGE = {
  EMPTY: "정확히 3개의 파일을 첨부해야 합니다.",
  REQUIRED: (requiredName: string) =>
    `'${requiredName}'이 하나만 있어야 합니다.`,
};
