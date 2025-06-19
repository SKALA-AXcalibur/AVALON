"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import { FileInputBox } from "./FileInputBox";
import { FileListItem } from "./FileListItem";
import ActionButton from "../common/ActionButton";
import { useFileUpload } from "@/hooks/useFileUpload";
import { useProjectStore } from "@/store/projectStore";
import { validateFiles } from "@/utils/validateFiles";
import { STEP_NAMES, UPLOAD_STEPS, FILE_TYPES } from "@/constants/upload";
import { ERROR_MESSAGES } from "@/constants/messages";

export const UploadBox = () => {
  const router = useRouter();
  const [files, setFiles] = useState<File[]>([]);
  const [error, setError] = useState<string | null>(null);
  const { project } = useProjectStore();

  const { createScenarios, step, resetStep, isLoading } = useFileUpload({
    onError: (errorMessage) => {
      setError(errorMessage);
    },
  });

  const handleFileSelect = (newFiles: File[]) => {
    setFiles((prevFiles) => [...prevFiles, ...newFiles]);
    setError(null);
    resetStep();
  };

  const handleFileDelete = (fileToDelete: File) => {
    setFiles((prevFiles) => prevFiles.filter((file) => file !== fileToDelete));
    setError(null);
    resetStep();
  };

  const findFileByType = (type: keyof typeof FILE_TYPES) => {
    const { words } = FILE_TYPES[type];
    return files.find((file) =>
      words.every((word) => file.name.normalize("NFC").includes(word))
    );
  };

  const handleCreateScenario = async () => {
    const { isValid, errorMessage } = validateFiles(files);
    if (!isValid) {
      setError(errorMessage || null);
      return;
    }

    setError(null);

    const uploadRequest = {
      requirementFile: findFileByType("REQUIREMENT")!,
      interfaceDef: findFileByType("INTERFACE_DEF")!,
      interfaceDesign: findFileByType("INTERFACE_DESIGN")!,
      databaseDesign: findFileByType("DATABASE_DESIGN")!,
    };

    const result = await createScenarios(uploadRequest);
    if (result.success && result.scenarios.length > 0) {
      router.push(`/project/${project.id}/scenario/${result.scenarios[0].id}`);
    } else if (result.success && result.scenarios.length === 0) {
      setError(ERROR_MESSAGES.FILE_UPLOAD.EMPTY_RESULT);
      resetStep();
    } else {
      setError(ERROR_MESSAGES.FILE_UPLOAD.CREATION_ERROR);
      resetStep();
    }
  };

  const getButtonText = () => {
    if (isLoading) {
      const totalSteps = Object.keys(STEP_NAMES).length;
      const currentStepIndex = Object.keys(STEP_NAMES).findIndex(
        (stepKey) => Number(stepKey) === step
      );
      const displayStep = currentStepIndex >= 0 ? currentStepIndex + 1 : 1;

      return `${displayStep}/${totalSteps} ${STEP_NAMES[step]} 진행 중`;
    } else if (step === UPLOAD_STEPS.UPLOAD) {
      return "시나리오 생성하기";
    } else {
      return `${STEP_NAMES[step]} 이어서 생성하기`;
    }
  };

  return (
    <div className="bg-white shadow-lg rounded-xl p-8 w-[672px]">
      <h1 className="text-2xl font-bold text-gray-800 mb-6">파일 첨부</h1>
      <FileInputBox onFileSelect={handleFileSelect} />
      {files.map((file, index) => (
        <FileListItem
          key={`${file.name}-${index}`}
          file={file}
          onDelete={handleFileDelete}
        />
      ))}
      {error && <p className="text-red-500 mt-4 text-sm">{error}</p>}
      <ActionButton
        onClick={handleCreateScenario}
        color="w-full bg-emerald-500 hover:bg-emerald-600 items-center justify-center mt-4"
        disabled={isLoading}
      >
        {getButtonText()}
      </ActionButton>
    </div>
  );
};
