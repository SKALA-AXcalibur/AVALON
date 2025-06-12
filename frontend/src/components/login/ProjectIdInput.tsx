"use client";
import { useState } from "react";
import validateId from "@/utils/validateId";

const ProjectIdInput = () => {
  const [projectId, setProjectId] = useState("");
  const [error, setError] = useState<string | null>(null);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setProjectId(value);

    const validation = validateId(value);
    setError(validation.isValid ? null : validation.errorMessage || null);
  };

  return (
    <div className="flex-1">
      <input
        name="projectId"
        value={projectId}
        onChange={handleChange}
        className={`w-full border rounded-lg px-4 py-3 text-lg ${
          error
            ? "border-red-500 focus:border-red-500"
            : "border-gray-300 focus:border-blue-500"
        } focus:outline-none focus:ring-2 focus:ring-opacity-50 ${
          error ? "focus:ring-red-500" : "focus:ring-blue-500"
        }`}
        placeholder="project id"
        aria-label="프로젝트 ID"
        aria-invalid={!!error}
        aria-describedby={error ? "projectId-error" : undefined}
      />
      <div className="h-4 mt-1">
        {error && (
          <p id="projectId-error" className="text-sm text-red-600" role="alert">
            {error}
          </p>
        )}
      </div>
    </div>
  );
};

export default ProjectIdInput;
