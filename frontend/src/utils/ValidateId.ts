import { ID_VALIDATION_ERROR_MESSAGE } from "@/constants/errorMessages";

const validateId = (
  Id: string
): { isValid: boolean; errorMessage?: string } => {
  if (!Id) {
    return { isValid: false, errorMessage: ID_VALIDATION_ERROR_MESSAGE.EMPTY };
  }

  if (Id.length < 5 || Id.length > 30) {
    return {
      isValid: false,
      errorMessage: ID_VALIDATION_ERROR_MESSAGE.LENGTH,
    };
  }

  if (!/^[a-zA-Z0-9-]+$/.test(Id)) {
    return {
      isValid: false,
      errorMessage: ID_VALIDATION_ERROR_MESSAGE.FORMAT,
    };
  }

  return { isValid: true };
};

export default validateId;
