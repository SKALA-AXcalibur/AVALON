import { ERROR_MESSAGES } from "@/constants/messages";
import { FILE_TYPES } from "@/constants/upload";

export const validateFiles = (files: File[]) => {
  if (files.length === 0) {
    return {
      isValid: false,
      errorMessage: ERROR_MESSAGES.FILE_VALIDATION.EMPTY,
    };
  }

  for (const [fileType, config] of Object.entries(FILE_TYPES)) {
    const { words } = config;

    const matchingFiles = files.filter((file) => {
      const normalizedName = file.name.normalize("NFC");
      return words.every((word) => normalizedName.includes(word));
    });

    if (matchingFiles.length === 0) {
      return {
        isValid: false,
        errorMessage: ERROR_MESSAGES.FILE_VALIDATION.MISSING(fileType, words),
      };
    }

    if (matchingFiles.length > 1) {
      return {
        isValid: false,
        errorMessage: ERROR_MESSAGES.FILE_VALIDATION.DUPLICATE(fileType, words),
      };
    }
  }

  const unmatchedFiles = files.filter((file) => {
    const normalizedName = file.name.normalize("NFC");
    return !Object.values(FILE_TYPES).some((config) =>
      config.words.every((word) => normalizedName.includes(word)),
    );
  });

  if (unmatchedFiles.length > 0) {
    return {
      isValid: false,
      errorMessage: ERROR_MESSAGES.FILE_VALIDATION.UNMATCHED(
        unmatchedFiles.map((f) => f.name),
      ),
    };
  }

  const requiredFileCount = Object.keys(FILE_TYPES).length;
  if (files.length !== requiredFileCount) {
    return {
      isValid: false,
      errorMessage: ERROR_MESSAGES.FILE_VALIDATION.COUNT_MISMATCH(
        requiredFileCount,
        files.length,
      ),
    };
  }

  return { isValid: true };
};
