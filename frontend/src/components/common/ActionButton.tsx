const ActionButton = ({
  onClick,
  color,
  children,
}: {
  onClick: () => void;
  color: string;
  children: React.ReactNode;
}) => {
  return (
    <button
      onClick={onClick}
      className={`${color} text-white rounded-lg px-4 py-2 flex items-center justify-center gap-1`}
    >
      {children}
    </button>
  );
};

export default ActionButton;
