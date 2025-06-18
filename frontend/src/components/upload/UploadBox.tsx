"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import FileInputBox from "./FileInputBox";
import FileListItem from "./FileListItem";
import ActionButton from "../common/ActionButton";
import useCreateScenarios from "@/hooks/upload/createScenarios";
import { useProjectStore } from "@/store/projectStore";
import validateFiles from "@/utils/validateFiles";
import { FILE_UPLOAD_ERROR_MESSAGE } from "@/constants/messages";
import { STEP_NAMES, UPLOAD_STEPS, FILE_TYPES } from "@/constants/upload";

const UploadBox = () => {
  const router = useRouter();
  const [files, setFiles] = useState<File[]>([]);
  const [error, setError] = useState<string | null>(null);
  const { createScenarios, step, setStep, isLoading } = useCreateScenarios();
  const { project } = useProjectStore();

  const handleFileSelect = (newFiles: File[]) => {
    setFiles((prevFiles) => [...prevFiles, ...newFiles]);
    setError(null);
    setStep(UPLOAD_STEPS.UPLOAD);
  };

  const handleFileDelete = (fileToDelete: File) => {
    setFiles((prevFiles) => prevFiles.filter((file) => file !== fileToDelete));
    setError(null);
    setStep(UPLOAD_STEPS.UPLOAD);
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

    try {
      setError(null);

      const uploadRequest = {
        requirementFile: findFileByType("REQUIREMENT")!,
        interfaceDef: findFileByType("INTERFACE_DEF")!,
        interfaceDesign: findFileByType("INTERFACE_DESIGN")!,
      };

      const isSuccess = await createScenarios(uploadRequest);
      if (isSuccess && project.scenarios.length > 0) {
        router.push(
          `/project/${project.id}/scenario/${project.scenarios[0].id}`
        );
      } else if (isSuccess && project.scenarios.length === 0) {
        setError(FILE_UPLOAD_ERROR_MESSAGE.EMPTY);
      }
    } catch (err) {
      setError(FILE_UPLOAD_ERROR_MESSAGE.ERROR);
      console.error(err);
    }
  };

  const getButtonText = () => {
    if (isLoading) {
      return `${step + 1}/${Object.keys(STEP_NAMES).length} ${
        STEP_NAMES[step]
      } 진행 중`;
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
        isLoading={isLoading}
      >
        {getButtonText()}
      </ActionButton>
    </div>
  );
};

export default UploadBox;
