import { api } from "@/shared/api/http";

export const accountApi = {
  update: (body) => api.put("/platform/me", body),
  changePassword: (body) => api.put("/platform/me/password", body),
};
