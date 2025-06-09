import Link from "next/link";

const LinkButton = ({
  href,
  color,
  children,
  ariaLabel,
}: {
  href: string;
  color: string;
  children: React.ReactNode;
  ariaLabel: string;
}) => {
  return (
    <Link
      href={href}
      className={`${color} text-white rounded-lg px-4 py-2 flex items-center gap-1`}
      aria-label={ariaLabel}
    >
      {children}
    </Link>
  );
};

export default LinkButton;
