"use client";
import { useState } from "react";
import { useRouter } from "next/navigation";
import FileInputBox from "./FileInputBox";
import FileListItem from "./FileListItem";
import ActionButton from "../common/ActionButton";
import useCreateScenarios from "@/hooks/upload/createScenarios";
import { useProjectStore } from "@/store/projectStore";

const UploadBox = () => {
  const router = useRouter();
  const [files, setFiles] = useState<File[]>([]);
  const [error, setError] = useState<string | null>(null);
  const { createScenarios, step, setStep, isLoading } = useCreateScenarios();
  const { project } = useProjectStore();

  const handleFileSelect = (newFiles: File[]) => {
    setFiles((prevFiles) => [...prevFiles, ...newFiles]);
    setError(null);
    setStep(0);
  };

  const handleFileDelete = (fileToDelete: File) => {
    setFiles((prevFiles) => prevFiles.filter((file) => file !== fileToDelete));
    setError(null);
    setStep(0);
  };

  const validateFiles = (): boolean => {
    const requiredNames = [
      "요구사항 정의서",
      "인터페이스 정의서",
      "인터페이스 설계서",
    ];
    const fileNames = files.map((file) => file.name);

    if (files.length !== 3) {
      setError("정확히 3개의 파일을 첨부해야 합니다.");
      return false;
    }

    for (const requiredName of requiredNames) {
      const matchingFiles = fileNames.filter((name) =>
        name.normalize("NFC").includes(requiredName)
      );
      if (matchingFiles.length !== 1) {
        setError(`'${requiredName}'이 하나만 있어야 합니다.`);
        return false;
      }
    }

    return true;
  };

  const handleCreateScenario = async () => {
    if (!validateFiles()) return;

    try {
      setError(null);

      const uploadRequest = {
        requirementFile: files.find((file) =>
          file.name.includes("요구사항 정의서")
        )!,
        interfaceDef: files.find((file) =>
          file.name.includes("인터페이스 정의서")
        )!,
        interfaceDesign: files.find((file) =>
          file.name.includes("인터페이스 설계서")
        )!,
      };

      const isSuccess = await createScenarios(uploadRequest);
      if (isSuccess && project.scenarios.length > 0) {
        router.push(
          `/project/${project.id}/scenario/${project.scenarios[0].id}`
        );
      } else if (isSuccess && project.scenarios.length === 0) {
        setError("시나리오 생성가 0개 생성되어 이동할 수 없습니다.");
      }
    } catch (err) {
      setError("시나리오 생성 중 오류가 발생했습니다.");
      console.error(err);
    }
  };

  const getButtonText = () => {
    const STEP_NAME: Record<number, string> = {
      0: "파일 업로드",
      1: "파일 분석",
      2: "API 목록 생성",
      3: "시나리오 생성",
    };
    if (isLoading) {
      return `${step + 1}/${Object.keys(STEP_NAME).length} ${
        STEP_NAME[step]
      } 진행 중`;
    } else if (step === 0) {
      return "시나리오 생성하기";
    } else {
      return `${STEP_NAME[step]} 이어서 생성하기`;
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
