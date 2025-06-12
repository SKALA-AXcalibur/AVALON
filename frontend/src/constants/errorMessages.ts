const FILE_UPLOAD_ERROR_MESSAGE = {
  EMPTY: "시나리오가 0개 생성되어 이동할 수 없습니다.",
  ERROR: "시나리오 생성 중 오류가 발생했습니다.",
};

const ID_VALIDATION_ERROR_MESSAGE = {
  EMPTY: "ID를 입력해주세요.",
  LENGTH: "ID는 5자 이상 30자 이하여야 합니다.",
  FORMAT: "ID는 영문자와 숫자, -로만 구성되어야 합니다.",
};

const FILE_VALIDATION_ERROR_MESSAGE = {
  EMPTY: "정확히 3개의 파일을 첨부해야 합니다.",
  REQUIRED: (requiredName: string) =>
    `'${requiredName}'이 하나만 있어야 합니다.`,
};

export {
  FILE_UPLOAD_ERROR_MESSAGE,
  ID_VALIDATION_ERROR_MESSAGE,
  FILE_VALIDATION_ERROR_MESSAGE,
};
