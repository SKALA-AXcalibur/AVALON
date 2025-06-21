import { ERROR_MESSAGES } from "@/constants/messages";

export const validateId = (
  id: string,
): { isValid: boolean; errorMessage?: string } => {
  if (!id) {
    return { isValid: false, errorMessage: ERROR_MESSAGES.ID_VALIDATION.EMPTY };
  }

  if (id.length < 5 || id.length > 30) {
    return {
      isValid: false,
      errorMessage: ERROR_MESSAGES.ID_VALIDATION.LENGTH,
    };
  }

  if (!/^[a-zA-Z0-9-]+$/.test(id)) {
    return {
      isValid: false,
      errorMessage: ERROR_MESSAGES.ID_VALIDATION.FORMAT,
    };
  }

  return { isValid: true };
};
