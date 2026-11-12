import { api } from "@/shared/api/http";

export const authApi = {
  login: (body) => api.post("/auth/login", body),
  register: (body) => api.post("/auth/register", body),
  forgotPassword: (body) => api.post("/auth/forgot-password", body),
  resetPassword: (body) => api.post("/auth/reset-password", body),
  me: () => api.get("/platform/me"),
  logout: (refreshToken) =>
    api.post("/auth/logout", null, { params: { refreshToken } }),
};
