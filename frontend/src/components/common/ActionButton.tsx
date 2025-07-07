import { ComponentProps } from "react";

interface ActionButtonProps extends ComponentProps<"button"> {
  children: React.ReactNode;
  color?: string;
  loading?: boolean;
}

export const ActionButton = ({
  children,
  color,
  disabled,
  ...props
}: ActionButtonProps) => {
  const baseStyle =
    "px-4 py-2 rounded-md font-bold transition-colors duration-200 cursor-pointer disabled:cursor-not-allowed";
  const defaultColor = "text-slate-700 bg-transparent hover:text-red-600";

  return (
    <button
      className={`${baseStyle} ${color || defaultColor}`}
      disabled={disabled}
      {...props}
    >
      {children}
    </button>
  );
};
