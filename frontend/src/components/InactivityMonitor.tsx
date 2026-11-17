import { INACTIVITY_TIMEOUT_MS } from "../config/inactivity";
import { useInactivityMonitor } from "../hooks/useInactivityMonitor";

export type InactivityMonitorProps = { onTimeout?: () => void };

function noop() {
  // SCRUM-135 wires the automatic session termination here.
}

/** Renders nothing; watches user inactivity while it is mounted. */
export default function InactivityMonitor({ onTimeout }: InactivityMonitorProps) {
  useInactivityMonitor({ timeoutMs: INACTIVITY_TIMEOUT_MS, onTimeout: onTimeout ?? noop });
  return null;
}
