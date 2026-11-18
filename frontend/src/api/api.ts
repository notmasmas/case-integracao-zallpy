import axios from "axios";
import {
  clearSession,
  expiredSessionLoginPath,
  expiredSessionSearch,
  loadSession,
  paths,
} from "../routes/routes";

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
    if (!axios.isAxiosError(error) || error.response?.status !== 401) {
      return Promise.reject(error);
    }

    const url = error.config?.url ?? "";
    if (url === "/login" || url.endsWith("/login")) {
      return Promise.reject(error);
    }

    clearSession();
    const params = new URLSearchParams(window.location.search);
    const alreadyNotified =
      window.location.pathname === paths.login &&
      params.get(expiredSessionSearch.key) === expiredSessionSearch.value;
    if (!alreadyNotified) {
      window.location.assign(expiredSessionLoginPath());
    }
    return Promise.reject(error);
  },
);

export default api;
