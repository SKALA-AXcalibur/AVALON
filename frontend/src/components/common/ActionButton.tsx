const ActionButton = ({
  onClick,
  color,
  disabled,
  children,
}: {
  onClick: () => void;
  color: string;
  disabled?: boolean;
  children: React.ReactNode;
}) => {
  return (
    <button
      onClick={onClick}
      className={`${color} text-white rounded-lg px-4 py-2 flex items-center justify-center gap-1 cursor-pointer`}
      disabled={disabled}
    >
      {children}
    </button>
  );
};

export default ActionButton;
