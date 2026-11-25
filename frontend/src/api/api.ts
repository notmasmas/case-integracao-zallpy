import axios from "axios";
import { showAccessDeniedToast } from "../components/ui/accessDeniedToast";
import {
  clearSession,
  expiredSessionLoginPath,
  expiredSessionSearch,
  loadSession,
  paths,
  sessionEndedLoginPath,
} from "../routes/routes";

declare module "axios" {
  interface AxiosRequestConfig {
    // When true, a 401 response is returned to the caller untouched:
    // the stored session is kept and no redirect to the login page happens.
    skipSessionRedirect?: boolean;
  }
}

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
  const url = config.url ?? "";
  const isLogin = url === "/login" || url.endsWith("/login");
  if (isLogin) {
    return config;
  }

  const session = loadSession();
  if (session?.accessToken) {
    config.headers.Authorization = `Bearer ${session.accessToken}`;
  }
  return config;
});

api.interceptors.response.use(
  (response) => response,
  (error: unknown) => {
    if (
      axios.isAxiosError<{ code?: string }>(error) &&
      error.response?.status === 403 &&
      error.response.data?.code === "FORBIDDEN"
    ) {
      showAccessDeniedToast();
      return Promise.reject(error);
    }

    if (!axios.isAxiosError(error) || error.response?.status !== 401) {
      return Promise.reject(error);
    }

    const url = error.config?.url ?? "";
    if (url === "/login" || url.endsWith("/login")) {
      return Promise.reject(error);
    }

    // The caller handles the 401 itself (e.g. the best-effort logout fired
    // when the session ends by inactivity): no cleanup, no redirect.
    if (error.config?.skipSessionRedirect) {
      return Promise.reject(error);
    }

    clearSession();
    const params = new URLSearchParams(window.location.search);
    // Any session-end reason already on the login page counts, so a late 401
    // from an in-flight request never replaces the message being shown.
    const alreadyNotified =
      window.location.pathname === paths.login &&
      params.has(expiredSessionSearch.key);
    if (!alreadyNotified) {
      const body = error.response.data as { code?: string } | undefined;
      const target =
        body?.code === "SESSION_EXPIRED"
          ? sessionEndedLoginPath("inatividade")
          : expiredSessionLoginPath();
      window.location.assign(target);
    }
    return Promise.reject(error);
  },
);

export default api;
