"use client";
import ActionButton from "@/components/common/ActionButton";
import LinkButton from "@/components/common/LinkButton";
import { useNavigation } from "@/hooks/useNavigation";
import { useRouter } from "next/navigation";

export const Navigation = () => {
  const router = useRouter();

  const { getButtons, projectId, scenarioId } = useNavigation({
    onLogoutSuccess: () => {
      router.push("/login");
    },
    onGenerateTestcasesSuccess: () => {
      router.push(`/project/${projectId}/scenario/${scenarioId}`);
    },
    onRunApiTestSuccess: () => {
      router.push(`/project/${projectId}/test-run/${scenarioId}`);
    },
  });

  const buttons = getButtons();

  return (
    <nav className="flex flex-wrap gap-2">
      {buttons.map((button, index) => {
        if (button.type === "link") {
          return (
            <LinkButton
              key={index}
              href={button.href}
              color={button.color}
              ariaLabel={button.text}
            >
              {button.text}
            </LinkButton>
          );
        } else if (button.type === "action") {
          return (
            <ActionButton
              key={index}
              onClick={button.onClick}
              color={button.color}
              disabled={button.loading}
            >
              {button.text}
            </ActionButton>
          );
        }
        return null;
      })}
    </nav>
  );
};
