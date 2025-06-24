"use client";
import { ChangeEvent } from "react";

export const FileInputBox = ({
  onFileSelect,
}: {
  onFileSelect: (files: File[]) => void;
}) => {
  const handleFileChange = (e: ChangeEvent<HTMLInputElement>) => {
    if (e.target.files) {
      const fileList = Array.from(e.target.files);
      onFileSelect(fileList);
    }
  };

  return (
    <div className="mb-4">
      <label className="block text-gray-700 font-medium mb-2">파일 선택</label>
      <div className="flex gap-2">
        <input
          type="file"
          multiple
          onChange={handleFileChange}
          className="hidden"
          id="file-input"
        />
        <input
          className="flex-1 border border-gray-300 rounded-lg px-4 py-2"
          placeholder="파일 첨부"
          readOnly
        />
        <label
          htmlFor="file-input"
          className="bg-blue-500 text-white rounded-lg px-4 py-2 cursor-pointer"
        >
          파일 찾기
        </label>
      </div>
    </div>
  );
};
