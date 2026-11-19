import { useCallback, useRef } from "react";
import api from "../api/api";

/**
 * Returns a stable callback that tells the backend the user is active
 * (`POST /session/activity`), at most once per `intervalMs`. The first call
 * sends right away, which also covers a reload after some idle time.
 *
 * A 401 here means the session really expired, so the shared interceptor is
 * left to redirect to the login page.
 */
export function useSessionActivityReporter(intervalMs: number): () => void {
  const lastReportAtRef = useRef(0);

  return useCallback(() => {
    const now = Date.now();
    if (now - lastReportAtRef.current < intervalMs) {
      return;
    }
    lastReportAtRef.current = now;
    void api.post("/session/activity").catch(() => {});
  }, [intervalMs]);
}
