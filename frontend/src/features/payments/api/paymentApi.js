import { api } from "@/shared/api/http";

export const paymentApi = {
  optionsPath: "/platform/payment-options",
  get: (id) => api.get(`/platform/orders/${id}/payment`),
};
