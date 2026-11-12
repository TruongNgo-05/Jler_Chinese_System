import { api } from "@/shared/api/http";

export const cartApi = {
  path: "/platform/cart",
  add: (body) => api.post("/platform/cart", body),
  remove: (id) => api.delete(`/platform/cart/${id}`),
  adjust: (id, body) => api.post(`/platform/cart/${id}/adjust`, body),
  quote: (code) => api.get("/platform/cart/quote", { params: { code } }),
};
