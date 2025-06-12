export const UPLOAD_STEPS = {
  UPLOAD: 0,
  ANALYZE: 1,
  CREATE_API_LIST: 2,
  CREATE_SCENARIOS: 3,
  COMPLETE: 4,
} as const;

export const STEP_NAMES: Record<number, string> = {
  [UPLOAD_STEPS.UPLOAD]: "파일 업로드",
  [UPLOAD_STEPS.ANALYZE]: "파일 분석",
  [UPLOAD_STEPS.CREATE_API_LIST]: "API 목록 생성",
  [UPLOAD_STEPS.CREATE_SCENARIOS]: "시나리오 생성",
} as const;

export const FILE_TYPES = {
  REQUIREMENT: {
    words: ["요구사항", "정의서"],
    key: "requirementFile",
  },
  INTERFACE_DEF: {
    words: ["인터페이스", "정의서"],
    key: "interfaceDef",
  },
  INTERFACE_DESIGN: {
    words: ["인터페이스", "설계서"],
    key: "interfaceDesign",
  },
} as const;
