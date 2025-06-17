import { FILE_VALIDATION_ERROR_MESSAGE } from "@/constants/messages";

const validateFiles = (files: File[]) => {
  const requiredWords = {
    "요구사항 정의서": ["요구사항", "정의서"],
    "인터페이스 정의서": ["인터페이스", "정의서"],
    "인터페이스 설계서": ["인터페이스", "설계서"],
  };
  const fileNames = files.map((file) => file.name);

  if (files.length !== 3) {
    return {
      isValid: false,
      errorMessage: FILE_VALIDATION_ERROR_MESSAGE.EMPTY,
    };
  }

  for (const [requiredName, words] of Object.entries(requiredWords)) {
    const matchingFiles = fileNames.filter((name) => {
      const normalizedName = name.normalize("NFC");
      return words.every((word) => normalizedName.includes(word));
    });
    if (matchingFiles.length !== 1) {
      return {
        isValid: false,
        errorMessage: FILE_VALIDATION_ERROR_MESSAGE.REQUIRED(requiredName),
      };
    }
  }

  return { isValid: true };
};

export default validateFiles;
