"use client";
import { useState } from "react";
import { useParams } from "next/navigation";
import Header from "@/components/common/Header";
import FileInputBox from "@/components/upload/FileInputBox";
import FileListItem from "@/components/upload/FileListItem";
import Link from "next/link";

const UploadPage = () => {
  const params = useParams();
  const [files, setFiles] = useState<File[]>([]);

  const handleFileSelect = (newFiles: File[]) => {
    setFiles((prevFiles) => [...prevFiles, ...newFiles]);
  };

  const handleFileDelete = (fileToDelete: File) => {
    setFiles((prevFiles) => prevFiles.filter((file) => file !== fileToDelete));
  };

  return (
    <div className="min-h-screen bg-white flex flex-col">
      <Header />
      <main className="flex-1 flex flex-col items-center justify-center">
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
          <Link href={`/project/${params["project-id"]}/scenario`}>
            <button className="w-full bg-emerald-500 text-white rounded-lg py-3 mt-6 flex items-center justify-center gap-2">
              시나리오 생성하기
            </button>
          </Link>
        </div>
      </main>
    </div>
  );
};

export default UploadPage;
