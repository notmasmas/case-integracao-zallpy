import axios from "axios";
import { clearSession, loadSession } from "../routes/routes";

const api = axios.create({
  baseURL: "http://localhost:8080",
  headers: {
    "Content-Type": "application/json",
  },
});

api.interceptors.request.use((config) => {
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
    if (window.location.pathname !== "/") {
      window.location.assign("/");
    }
    return Promise.reject(error);
  },
);

export default api;
