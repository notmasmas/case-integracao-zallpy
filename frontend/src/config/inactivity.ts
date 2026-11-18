export const DEFAULT_INACTIVITY_TIMEOUT_MINUTES = 15;

const MS_PER_MINUTE = 60 * 1000;

/**
 * Converts the raw `VITE_INACTIVITY_TIMEOUT_MINUTES` value into milliseconds.
 * Anything that is not a finite number greater than zero falls back to the
 * default, so a bad value never makes the monitor fire immediately.
 */
export function resolveInactivityTimeoutMs(raw: string | undefined): number {
  const minutes = Number(raw?.trim());
  const valid = Number.isFinite(minutes) && minutes > 0;
  return (valid ? minutes : DEFAULT_INACTIVITY_TIMEOUT_MINUTES) * MS_PER_MINUTE;
}

export const INACTIVITY_TIMEOUT_MS = resolveInactivityTimeoutMs(
  import.meta.env.VITE_INACTIVITY_TIMEOUT_MINUTES,
);
