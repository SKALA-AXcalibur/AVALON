const ActionButton = ({
  onClick,
  color,
  children,
  isLoading = false,
}: {
  onClick: () => void;
  color: string;
  children: React.ReactNode;
  isLoading?: boolean;
}) => {
  return (
    <button
      onClick={onClick}
      className={`${color} text-white rounded-lg px-4 py-2 flex items-center gap-1`}
    >
      {isLoading ? "Loading..." : children}
    </button>
  );
};

export default ActionButton;
