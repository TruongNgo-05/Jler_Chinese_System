import { api } from "@/shared/api/http";

export const orderApi = {
  path: "/platform/orders",
  create: (body) => api.post("/platform/orders", body),
  action: (id, body) => api.post(`/platform/orders/${id}/actions`, body),
};
