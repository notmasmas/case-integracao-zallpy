export const paths = {
  login: "/",
  panel: "/painel",
} as const;

export type UserRole = "CUSTOMER" | "SUPPORT";

export type Session = {
  role: UserRole;
  name: string;
  accessToken: string;
};

const sessionKey = "ecovolt.session";

export function saveSession(session: Session) {
  sessionStorage.setItem(sessionKey, JSON.stringify(session));
}

export function loadSession(): Session | null {
  const raw = sessionStorage.getItem(sessionKey);
  if (!raw) {
    return null;
  }

  try {
    const parsed = JSON.parse(raw) as Session;
    if (parsed.role !== "CUSTOMER" && parsed.role !== "SUPPORT") {
      return null;
    }
    return parsed;
  } catch {
    return null;
  }
}
