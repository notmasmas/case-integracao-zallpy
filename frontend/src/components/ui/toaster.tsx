import {
  Toaster as ChakraToaster,
  Portal,
  createToaster,
} from "@chakra-ui/react";

export const toaster = createToaster({
  placement: "bottom-end",
});

export function Toaster() {
  return (
    <Portal>
      <ChakraToaster toaster={toaster}>
        {(toast) => {
          const isError = toast.type === "error";
          const accentColor = isError
            ? "var(--color-warning-default)"
            : "var(--color-success-dark)";
          const surfaceColor = isError
            ? "var(--color-warning-light)"
            : "var(--color-success-light)";

          return (
            <div
              key={toast.id}
              style={{
                backgroundColor: surfaceColor,
                border: "1px solid var(--color-border-gray)",
                borderLeft: `4px solid ${accentColor}`,
                borderRadius: "var(--radius-md)",
                boxShadow: "var(--shadow-lg)",
                color: "var(--color-text-primary)",
                padding: "var(--space-12) var(--space-16)",
                minWidth: "17.5rem",
              }}
            >
              <strong style={{ color: accentColor }}>{toast.title}</strong>
              {toast.description && (
                <p style={{ color: "var(--color-text-gray-primary)" }}>
                  {toast.description}
                </p>
              )}
            </div>
          );
        }}
      </ChakraToaster>
    </Portal>
  );
}
