import { API_BASE_URL as base } from "@/config/environment";
import axios from "axios";

export const api = axios.create({ baseURL: base, timeout: 15000 });
api.interceptors.request.use((config) => {
  const token = localStorage.getItem("accessToken");
  if (token) config.headers.Authorization = `Bearer ${token}`;
  return config;
});
let refreshing;
api.interceptors.response.use(
  (r) => r,
  async (error) => {
    const config = error.config;
    if (
      error.response?.status === 401 &&
      config &&
      !config.retry &&
      !config.url.startsWith("/auth/")
    ) {
      const refreshToken = localStorage.getItem("refreshToken");
      if (refreshToken) {
        try {
          if (!refreshing)
            refreshing = axios
              .post(`${base}/auth/refresh`, null, {
                params: { refreshToken },
                timeout: 15000,
              })
              .then((r) => {
                localStorage.setItem("accessToken", r.data.accessToken);
                return r.data.accessToken;
              })
              .finally(() => {
                refreshing = null;
              });
          await refreshing;
          config.retry = true;
          return api(config);
        } catch {
          localStorage.removeItem("accessToken");
          localStorage.removeItem("refreshToken");
          window.dispatchEvent(new Event("auth-expired"));
        }
      } else window.dispatchEvent(new Event("auth-expired"));
    }
    return Promise.reject(error);
  },
);
