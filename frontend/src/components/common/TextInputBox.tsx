const TextInputBox = ({
  title,
  value,
  placeholder,
  onChange,
}: {
  title: string;
  value: string;
  placeholder: string;
  onChange: (e: React.ChangeEvent<HTMLTextAreaElement>) => void;
}) => {
  return (
    <div className="h-full bg-slate-50 border border-slate-200 rounded-lg p-6 flex-1 flex flex-col">
      <h3 className="font-medium text-slate-700 mb-2 flex-shrink-0">{title}</h3>
      <textarea
        value={value}
        onChange={onChange}
        className="w-full flex-1 p-2 border border-slate-300 rounded-lg resize-none overflow-auto"
        placeholder={placeholder}
      />
    </div>
  );
};

export default TextInputBox;
