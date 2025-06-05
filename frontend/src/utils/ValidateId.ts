const validateId = (
  Id: string
): { isValid: boolean; errorMessage?: string } => {
  if (!Id) {
    return { isValid: false, errorMessage: "ID를 입력해주세요." };
  }

  if (Id.length < 5 || Id.length > 30) {
    return {
      isValid: false,
      errorMessage: "ID는 5자 이상 30자 이하여야 합니다.",
    };
  }

  if (!/^[a-zA-Z0-9-]+$/.test(Id)) {
    return {
      isValid: false,
      errorMessage: "ID는 영문자와 숫자, -로만 구성되어야 합니다.",
    };
  }

  return { isValid: true };
};

export default validateId;
