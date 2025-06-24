import Link from "next/link";
import { ComponentProps } from "react";

interface LinkButtonProps extends ComponentProps<"a"> {
  children: React.ReactNode;
  href: string;
  color?: string;
  ariaLabel: string;
}

export const LinkButton = ({
  children,
  href,
  color,
  ariaLabel,
  ...props
}: LinkButtonProps) => {
  const baseStyle =
    "px-4 py-2 rounded-md font-bold transition-colors duration-200";
  const defaultColor = "text-slate-700 bg-transparent hover:text-red-600";

  return (
    <Link
      href={href}
      className={`${baseStyle} ${color || defaultColor}`}
      aria-label={ariaLabel}
      {...props}
    >
      {children}
    </Link>
  );
};
