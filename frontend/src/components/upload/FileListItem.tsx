"use client";

interface FileListItemProps {
  file: File;
  onDelete: (file: File) => void;
}

const FileListItem = ({ file, onDelete }: FileListItemProps) => {
  const formatFileSize = (bytes: number) => {
    if (bytes === 0) return "0 Bytes";
    const k = 1024;
    const sizes = ["Bytes", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + " " + sizes[i];
  };

  return (
    <div className="bg-gray-50 rounded-lg p-4 flex items-center justify-between mb-2">
      <div className="flex items-center gap-2">
        <span className="text-gray-800 font-medium">{file.name}</span>
        <span className="text-gray-500 text-sm">
          ({formatFileSize(file.size)})
        </span>
      </div>
      <button
        onClick={() => onDelete(file)}
        className="bg-red-500 text-white rounded-lg px-3 py-1 flex items-center gap-1"
      >
        삭제
      </button>
    </div>
  );
};

export default FileListItem;
