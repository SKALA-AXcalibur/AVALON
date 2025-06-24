export const ErrorBox = ({
  title = "데이터를 가져오는데 실패했습니다",
  error,
}: {
  title?: string;
  error?: Error | unknown;
}) => {
  return (
    <div className="flex flex-col items-center justify-center p-6">
      <h1 className="text-2xl font-bold text-red-600 mb-4">{title}</h1>
      <p className="text-slate-600">
        {error instanceof Error
          ? error.message
          : "알 수 없는 오류가 발생했습니다"}
      </p>
    </div>
  );
};
