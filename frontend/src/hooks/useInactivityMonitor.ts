import { useEffect, useRef } from "react";

export const ACTIVITY_EVENTS = [
  "mousedown",
  "keydown",
  "touchstart",
  "scroll",
  "wheel",
  "mousemove",
] as const;

/** Minimum gap between two writes of the last-activity timestamp. */
export const ACTIVITY_THROTTLE_MS = 1000;

export const DEFAULT_CHECK_INTERVAL_MS = 15_000;

export type InactivityMonitorOptions = {
  timeoutMs: number;
  onTimeout: () => void;
  onActivity?: () => void;
  enabled?: boolean;
  checkIntervalMs?: number;
};

const LISTENER_OPTIONS: AddEventListenerOptions = { capture: true, passive: true };

/**
 * Tracks the time since the last DOM interaction and calls `onTimeout` once
 * when `timeoutMs` is reached. After firing it stops observing until the
 * component remounts or one of `enabled`, `timeoutMs`, `checkIntervalMs`
 * changes. API requests do not count as activity.
 *
 * `onActivity` is called for each interaction that passes the throttle, at
 * most once per `ACTIVITY_THROTTLE_MS`.
 *
 * Elapsed time is measured from a timestamp (not a single long timer) and is
 * re-checked when the tab becomes visible, because browsers throttle timers in
 * background tabs.
 */
export function useInactivityMonitor({
  timeoutMs,
  onTimeout,
  onActivity,
  enabled = true,
  checkIntervalMs = DEFAULT_CHECK_INTERVAL_MS,
}: InactivityMonitorOptions): void {
  // Read at fire time, so a new closure on every render neither restarts the
  // countdown nor re-registers listeners.
  const onTimeoutRef = useRef(onTimeout);
  onTimeoutRef.current = onTimeout;
  const onActivityRef = useRef(onActivity);
  onActivityRef.current = onActivity;

  const lastActivityAtRef = useRef(0);
  const firedRef = useRef(false);

  useEffect(() => {
    if (!enabled) {
      return;
    }

    lastActivityAtRef.current = Date.now();
    firedRef.current = false;

    const handleActivity = () => {
      const now = Date.now();
      if (now - lastActivityAtRef.current < ACTIVITY_THROTTLE_MS) {
        return;
      }
      lastActivityAtRef.current = now;
      onActivityRef.current?.();
    };

    const check = () => {
      if (firedRef.current || Date.now() - lastActivityAtRef.current < timeoutMs) {
        return;
      }
      firedRef.current = true;
      stop();
      onTimeoutRef.current();
    };

    const handleVisibilityChange = () => {
      if (document.visibilityState === "visible") {
        check();
      }
    };

    for (const eventName of ACTIVITY_EVENTS) {
      window.addEventListener(eventName, handleActivity, LISTENER_OPTIONS);
    }
    document.addEventListener("visibilitychange", handleVisibilityChange);
    const intervalId = window.setInterval(check, checkIntervalMs);

    function stop() {
      for (const eventName of ACTIVITY_EVENTS) {
        window.removeEventListener(eventName, handleActivity, LISTENER_OPTIONS);
      }
      document.removeEventListener("visibilitychange", handleVisibilityChange);
      window.clearInterval(intervalId);
    }

    return stop;
  }, [enabled, timeoutMs, checkIntervalMs]);
}
