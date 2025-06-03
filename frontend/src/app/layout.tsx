import type { Metadata } from "next";
import "./globals.css";

export const metadata: Metadata = {
  title: "AVALON",
  description: "AI-based Validation of API Logic and Operation with Novelty",
};

const RootLayout = ({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) => {
  return (
    <html lang="ko">
      <body>{children}</body>
    </html>
  );
};

export default RootLayout;
