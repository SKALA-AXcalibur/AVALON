const ActionButton = ({
  onClick,
  color,
  disabled,
  children,
}: {
  onClick: () => void | Promise<void>;
  color: string;
  disabled?: boolean;
  children: React.ReactNode;
}) => {
  return (
    <button
      onClick={onClick}
      className={`${color} text-white rounded-lg px-4 py-2 flex items-center justify-center gap-1 cursor-pointer disabled:opacity-50 disabled:cursor-not-allowed`}
      disabled={disabled}
    >
      {children}
    </button>
  );
};

export default ActionButton;
